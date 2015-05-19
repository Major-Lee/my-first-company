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
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
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
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakRateVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
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
	private DeviceFacadeService deviceFacadeService;
	
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
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);

			WifiDeviceSettingDTO dto = entity.getInnerModel();
			URouterEnterVTO vto = new URouterEnterVTO();
			String power = DeviceHelper.getURouterDevicePower(dto);
			if(!StringUtils.isEmpty(power)){
				vto.setPower(Integer.parseInt(power));
			}
			vto.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
			//vto.setWd_date_rx_rate(device_entity.getData_rx_rate());
			//vto.setData_rx_rate_peak(device_entity.getData_rx_rate());
			
			String[] ret = fetchRealtimeRate(wifiId);
			vto.setTx_rate(ret[0]);
			vto.setRx_rate(ret[1]);
			//vto.setRate_peak(ret[2]);
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
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			//用户访问终端列表时 判断上报timeout进行获取
			if(!WifiDeviceRealtimeRateStatisticsStringService.getInstance().isHDRateWaiting(wifiId)){
				deliverMessageService.sendDeviceHDRateFetchActionMessage(wifiId);
			}
			
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
			//System.out.println("###################presents.size():"+presents.size());
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
					WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
					for(Tuple tuple : presents){
						mark_entity = mark_entitys.get(cursor);
						boolean online = WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore());
						URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(tuple.getElement(), online, mark_entity, setting_dto);
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
			deviceFacadeService.validateUserDevice(uid, wifiId);
		
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
	 * 获取设备的网速测试
	 * a:返回当前网速数据 并重新下发网速测试指令 
	 * b:如果存在数据waiting 不重新下发指令
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterPeakRateVTO> urouterPeakRate(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
		
			URouterPeakRateVTO vto = new URouterPeakRateVTO();
			vto.setRx_peak_rate(fetchPeakRate(wifiId));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备的实时上行行速率
	 * @param wifiId
	 * @return 0：上行速率 1：下行速率
	 */
	public String[] fetchRealtimeRate(String wifiId){
		List<String> result = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getRate(wifiId);
		//获取实时的上下行速率
		String realtime_tx_rate = result.get(0);
		String realtime_rx_rate = result.get(1);
		//String rx_peak_rate = result.get(5);
		//如果有实时速率数据 就直接返回
		if(!StringUtils.isEmpty(realtime_tx_rate) && !StringUtils.isEmpty(realtime_rx_rate)){
			return new String[]{realtime_tx_rate, realtime_rx_rate};
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
		return new String[]{last_tx_rate, last_rx_rate};
	}
	
	/**
	 * 获取设备的网速测试数据
	 * @param wifiId
	 * @return
	 */
	public String fetchPeakRate(String wifiId){
		List<String> result = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getPeak(wifiId);
		String peak_rate = result.get(0);
		String peak_rate_waiting = result.get(1);
		//如果waiting没有标记 则发送指令查询
		if(StringUtils.isEmpty(peak_rate_waiting)){
			//调用异步消息下发网速测试指令
			deliverMessageService.sendQueryDeviceSpeedFetchActionMessage(wifiId);
		}
		return peak_rate;
	}
	
	/**
	 * 获取黑名单列表数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			List<URouterHdVTO> vtos = null;
			int total = 0;
			
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
						WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
						int cursor = 0;
						for(String block_hd_mac : block_hd_macs){
							URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(block_hd_mac, false, mark_entitys.get(cursor), setting_dto);
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
			WifiDevice device_entity = deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting setting_entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			WifiDeviceSettingDTO setting_dto = setting_entity.getInnerModel();
			
			URouterSettingVTO vto = new URouterSettingVTO();
			vto.setMac(device_entity.getId());
			vto.setOem_swver(device_entity.getOem_swver());
			vto.setOem_hdver(device_entity.getOem_hdver());
			vto.setOl(device_entity.isOnline());
			vto.setUptime(DeviceHelper.getCurrentDeviceUptime(device_entity));
			vto.setWan_ip(device_entity.getWan_ip());
			//vto.setMode(DeviceHelper.getDeviceMode(setting_dto));
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
	
	/**
	 * 获取设备的上网方式设置
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting setting_entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			URouterModeVTO vto = new URouterModeVTO();
			
			WifiDeviceSettingDTO setting_dto = setting_entity.getInnerModel();
			if(setting_dto != null){
				WifiDeviceSettingLinkModeDTO mode_dto = setting_dto.getMode();
				if(mode_dto != null){
					vto.setIp(mode_dto.getReal_ipaddr());
					vto.setMode(DeviceHelper.getDeviceMode(mode_dto.getModel()));
					vto.setNetmask(mode_dto.getReal_netmask());
					vto.setP_un(mode_dto.getUsername());
					vto.setP_pwd(mode_dto.getPassword_rsa());
					vto.setGateway(mode_dto.getGateway());
					vto.setDns(mode_dto.getDns());
				}
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
}
