package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMark;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMarkPK;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.URouterDeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceMarkService;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;

/**
 * device urouter Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceURouterRestBusinessFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiHandsetDeviceMarkService wifiHandsetDeviceMarkService;
	
	@Resource
	private URouterDeviceFacadeService uRouterDeviceFacadeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	/**
	 * urouter 入口界面数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId){
		try{
			//WifiDevice device_entity = uRouterDeviceFacadeService.validateDevice(wifiId);
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = uRouterDeviceFacadeService.validateDeviceSetting(wifiId);

			WifiDeviceSettingDTO dto = entity.getInnerModel();
			URouterEnterVTO vto = new URouterEnterVTO();
			if(!StringUtils.isEmpty(dto.getPower())){
				vto.setPower(Integer.parseInt(dto.getPower()));
			}
			vto.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
			//vto.setWd_date_rx_rate(device_entity.getData_rx_rate());
			//vto.setData_rx_rate_peak(device_entity.getData_rx_rate());
			
			String[] ret = fetchRealtimeRate(wifiId);
			vto.setTx_rate(ret[0]);
			vto.setRx_rate(ret[1]);
			vto.setRate_peak(ret[2]);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	public static final int HDList_Online_Status = 1;//获取在线终端列表
	public static final int HDList_Offline_Status = 2;//获取离线终端列表
	/**
	 * 获取urouter设备在线终端列表
	 * @param uid
	 * @param wifiId
	 * @param start
	 * @param size
	 * @return
	 */
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status, int start, int size){
		try{
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);

			List<URouterHdVTO> vtos = null;
			long total = 0;
			Set<Tuple> presents = null;
			switch(status){
				case HDList_Online_Status:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId);
					break;
				case HDList_Offline_Status:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOfflineSize(wifiId);
					break;
				default:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchPresents(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentSize(wifiId);
			}
	
			if(!presents.isEmpty()){
				List<WifiHandsetDeviceMarkPK> mark_pks = new ArrayList<WifiHandsetDeviceMarkPK>();
				for(Tuple tuple : presents){
					mark_pks.add(new WifiHandsetDeviceMarkPK(wifiId, tuple.getElement()));
				}
				List<WifiHandsetDeviceMark> mark_entitys = wifiHandsetDeviceMarkService.findByIds(mark_pks, true, true);
				if(!mark_entitys.isEmpty()){
					vtos = new ArrayList<URouterHdVTO>();
					int cursor = 0;
					WifiHandsetDeviceMark mark_entity = null;
					for(Tuple tuple : presents){
						mark_entity = mark_entitys.get(cursor);
						boolean online = WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore());
						URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(tuple.getElement(), online, mark_entity);
						vtos.add(vto);
						cursor++;
					}
				}
			}
			if(vtos == null)
				vtos = Collections.emptyList();
			
			Map<String, Object> payload = PageHelper.partialAllList(vtos, total, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备的实时速率
	 * a:如果存在数据null 下发实时速率指令 
	 * b:如果存在数据waiting 什么都不做
	 * c:如果存在数据非ab 返回数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId){
		try{
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
		
			URouterRealtimeRateVTO vto = new URouterRealtimeRateVTO();
			String[] ret = fetchRealtimeRate(wifiId);
			vto.setTx_rate(ret[0]);
			vto.setRx_rate(ret[1]);
			vto.setTs(System.currentTimeMillis());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备的实时上行行速率
	 * @param wifiId
	 * @return 0：上行速率 1：下行速率 2:网速 下行速率峰值
	 */
	public String[] fetchRealtimeRate(String wifiId){
		List<String> result = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getRate(wifiId);
		//获取实时的上下行速率
		String realtime_tx_rate = result.get(0);
		String realtime_rx_rate = result.get(1);
		String rx_peak_rate = result.get(5);
		//如果有实时速率数据 就直接返回
		if(!StringUtils.isEmpty(realtime_tx_rate) && !StringUtils.isEmpty(realtime_rx_rate)){
			return new String[]{realtime_tx_rate, realtime_rx_rate, rx_peak_rate};
		}
		String waiting = result.get(4);
		//如果waiting没有标记 则发送指令查询
		if(StringUtils.isEmpty(waiting)){
			//调用异步消息下发实时速率指令
			deliverMessageService.sendDeviceRealtimeRateFetchActionMessage(wifiId);
		}
		
		//返回last的速率数据
		String last_tx_rate = result.get(2);
		String last_rx_rate = result.get(3);
		return new String[]{last_tx_rate, last_rx_rate, rx_peak_rate};
	}
	
	/**
	 * 获取黑名单列表数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size){
		try{
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
			
			List<URouterHdVTO> vtos = null;
			int total = 0;
			
			WifiDeviceSetting entity = uRouterDeviceFacadeService.validateDeviceSetting(wifiId);
			WifiDeviceSettingDTO dto = entity.getInnerModel();
			WifiDeviceSettingAclDTO acl_dto = DeviceHelper.matchDefaultAcl(dto);
			if(acl_dto != null){
				List<String> block_hd_macs_all = acl_dto.getMacs();
				if(block_hd_macs_all != null && !block_hd_macs_all.isEmpty()){
					total = block_hd_macs_all.size();
					List<String> block_hd_macs = PageHelper.partialList(acl_dto.getMacs(), start, size);
					
					List<WifiHandsetDeviceMarkPK> mark_pks = BusinessModelBuilder.toWifiHandsetDeviceMarkPKs(wifiId, block_hd_macs);
					if(!mark_pks.isEmpty()){
						vtos = new ArrayList<URouterHdVTO>();
						List<WifiHandsetDeviceMark> mark_entitys = wifiHandsetDeviceMarkService.findByIds(mark_pks, true, true);
						int cursor = 0;
						for(String block_hd_mac : block_hd_macs){
							URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(block_hd_mac, false, mark_entitys.get(cursor));
							vtos.add(vto);
							cursor++;
						}
					}
				}
			}
			if(vtos == null)
				vtos = Collections.emptyList();
			
			Map<String, Object> payload = PageHelper.partialAllList(vtos, total, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备设置信息
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId){
		try{
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
			
			WifiDevice device_entity = uRouterDeviceFacadeService.validateDevice(wifiId);
			WifiDeviceSetting setting_entity = uRouterDeviceFacadeService.validateDeviceSetting(wifiId);
			WifiDeviceSettingDTO setting_dto = setting_entity.getInnerModel();
			
			URouterSettingVTO vto = new URouterSettingVTO();
			vto.setMac(device_entity.getId());
			vto.setOem_swver(device_entity.getOem_swver());
			vto.setOl(device_entity.isOnline());
			vto.setUptime(DeviceHelper.getDeviceUptime(device_entity));
			
			vto.setMode(DeviceHelper.getDeviceMode(setting_dto));
			//获取正常的vap
			WifiDeviceSettingVapDTO normal_vap = DeviceHelper.getUrouterDeviceVap(setting_dto);
			if(normal_vap != null){
				vto.setVap_auth(normal_vap.getAuth());
				vto.setVap_name(normal_vap.getName());
				vto.setVap_ssid(normal_vap.getSsid());
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
}
