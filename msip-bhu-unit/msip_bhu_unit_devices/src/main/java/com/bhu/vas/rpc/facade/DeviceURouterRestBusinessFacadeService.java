package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMark;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMarkPK;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsHashService;
import com.bhu.vas.business.ds.device.facade.URouterDeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceMarkService;
import com.smartwork.msip.cores.helper.ArithHelper;
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
		WifiDevice device_entity = null;
		WifiDeviceSetting entity = null;
		try{
			device_entity = uRouterDeviceFacadeService.validateDevice(wifiId);
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
			entity = uRouterDeviceFacadeService.validateDeviceSetting(wifiId);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
		
		WifiDeviceSettingDTO dto = entity.getInnerModel();
		URouterEnterVTO vto = new URouterEnterVTO();
		if(!StringUtils.isEmpty(dto.getPower())){
			vto.setPower(Integer.parseInt(dto.getPower()));
		}
		vto.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
		vto.setWd_date_rx_rate(device_entity.getData_rx_rate());
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
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
	public RpcResponseDTO<List<URouterHdVTO>> urouterHdList(Integer uid, String wifiId, int status, int start, int size){
		try{
			uRouterDeviceFacadeService.validateUserDevice(uid, wifiId);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
		
		List<URouterHdVTO> vtos = null;
		Set<Tuple> presents = null;
		switch(status){
			case HDList_Online_Status:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, start, size);
				break;
			case HDList_Offline_Status:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, start, size);
				break;
			default:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchPresents(wifiId, start, size);
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
					URouterHdVTO vto = new URouterHdVTO();
					vto.setHd_mac(tuple.getElement());
					vto.setOnline(WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore()));
					mark_entity = mark_entitys.get(cursor);
					if(mark_entity != null){
						vto.setN(mark_entity.getHd_name());
						//Data_rx_limit 设备发送终端的限速 kbps 转换成 bps
						vto.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(mark_entity.getData_rx_limit()));
						vto.setRx_limit(ArithHelper.unitConversionDoKbpsTobps(mark_entity.getData_tx_limit()));
						//Data_rx_rate是设备接收终端的速率 反过来就是终端的上行速率 bps
						vto.setTx_rate(mark_entity.getData_rx_rate());
						//Data_tx_rate是设备发送终端的速率 反过来就是终端的下行速率 bps
						vto.setRx_rate(mark_entity.getData_tx_rate());
					}
					vtos.add(vto);
					cursor++;
				}
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
			}
		}
		vtos = Collections.emptyList();
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
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
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
		
		URouterRealtimeRateVTO vto = new URouterRealtimeRateVTO();
		Map<String, String> rate_map = WifiDeviceRealtimeRateStatisticsHashService.getInstance().getRate(wifiId);
		if(rate_map == null){
			//调用异步消息下发实时速率指令
			
		}else{
			if(rate_map.containsValue(WifiDeviceRealtimeRateStatisticsHashService.WaitingMark)){
				//等待设备上报实时速率数据
			}
			else{
				
			}
		}
		
		rate_map.containsValue(WifiDeviceRealtimeRateStatisticsHashService.getInstance().WaitingMark);
		if(rate_map != null){
			BeanUtils.copyProperties(rate_map, vto);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
	}
	
}
