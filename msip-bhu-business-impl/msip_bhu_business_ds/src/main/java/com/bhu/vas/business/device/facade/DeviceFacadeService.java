package com.bhu.vas.business.device.facade;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceAlarm;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.builder.BusinessModelBuilder;
import com.bhu.vas.business.device.service.HandsetDeviceService;
import com.bhu.vas.business.device.service.WifiDeviceAlarmService;
import com.bhu.vas.business.device.service.WifiDeviceService;
import com.bhu.vas.business.device.service.WifiHandsetDeviceRelationMService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceAlarmService wifiDeviceAlarmService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	/**
	 * 
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线状态Redis更新
	 * @param dto
	 */
	public void wifiDeviceOnline(String ctx, WifiDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

//		try{
		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
		WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(wifi_device_entity.getId());
		if(exist_wifi_device_entity == null){
			wifiDeviceService.insert(wifi_device_entity);
		}else{
			if(exist_wifi_device_entity.isOnline()){
				//说明设备离线消息未能到达，需要对wifi对应的移动设备列表进行清除，清除掉wifi设备本次时间之前的数据
			}
			wifiDeviceService.update(wifi_device_entity);
		}
		//2：wifi设备在线状态Redis更新
		WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), ctx);
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(ex.getMessage(), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
	}
	
	/**
	 * wifi设备离线
	 * 1:wifi设备基础信息表中的在线状态更新
	 * 2:wifi设备在线状态redis更新
	 * 3:在此wifi上的移动设备的在线状态更新
	 * @param ctx
	 * @param mac
	 */
	public void wifiDeviceOffline(String ctx, String mac){
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		String lowercase_mac = mac.toLowerCase();
//		try{
		//1:wifi设备基础信息表中的在线状态更新
		WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(lowercase_mac);
		if(exist_wifi_device_entity != null){
			exist_wifi_device_entity.setOnline(false);
			wifiDeviceService.update(exist_wifi_device_entity);
		}
		//2:wifi设备在线状态redis更新
		//首先进行验证,判断ctx信息是否一致,如果一致则删除
		String ctx_present = WifiDevicePresentService.getInstance().getPresent(lowercase_mac);
		if(ctx.equals(ctx_present)){
			WifiDevicePresentService.getInstance().removePresent(lowercase_mac);
			//3:在此wifi上的移动设备的在线状态更新
//			CommonCriteria mc = new CommonCriteria();
//			mc.createCriteria().andColumnEqualTo("online", true)
//							   .andColumnEqualTo("last_wifi_id", lowercase_mac);
//			List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByCommonCriteria(mc);
			List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(lowercase_mac);
			if(!handset_devices_online_entitys.isEmpty()){
				for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
					handset_devices_online_entity.setOnline(false);
					//handsetDeviceService.update(handset_devices_online_entity);
				}
				handsetDeviceService.updateAll(handset_devices_online_entitys);
			}
		}
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(ex.getMessage(), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
	}
	
	/**
	 * wifi设备告警信息
	 * @param ctx
	 * @param dto
	 */
	public void wifiDeviceAlarm(String ctx, WifiDeviceAlarmDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac_addr()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

//		try{
		WifiDeviceAlarm wifi_device_alarm_entity = BusinessModelBuilder.wifiDeviceAlarmDtoToEntity(dto);
		wifiDeviceAlarmService.insert(wifi_device_alarm_entity);
			//wifiDeviceAlarmService
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(ex.getMessage(), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
	}
	
	/**
	 * 移动设备上线
	 * 1:移动设备基础信息更新
	 * 2:移动设备连接wifi设备的流水记录
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOnline(String ctx, String wifiId, HandsetDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

//		try{
		//1:移动设备基础信息更新
		HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
		handset_device_entity.setLast_wifi_id(wifiId);
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(handset_device_entity.getId());
		if(exist_handset_device_entity == null){
			handsetDeviceService.insert(handset_device_entity);
		}else{
			handsetDeviceService.update(handset_device_entity);
		}
		//2:移动设备连接wifi设备的接入记录(非流水)
		wifiHandsetDeviceRelationMService.addRelation(wifiId, dto.getMac(), handset_device_entity.
				getLast_login_at());
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(ex.getMessage(), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
	}
	/**
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

//		try{
		//1:更新移动设备的online状态为false
			//HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac().toLowerCase());
		if(exist_handset_device_entity != null){
			exist_handset_device_entity.setOnline(false);
			handsetDeviceService.update(exist_handset_device_entity);
		}
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(ex.getMessage(), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
	}
	/**
	 * 移动设备连接状态sync
	 * TODO: 单条同步数据 移动设备的在线状态有可能会清理不掉 比如 移动设备在cm断线期间下线 单条数据同步过来不方便清理已经不在线的移动设备状态
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String wifiId, HandsetDeviceDTO dto){
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac());
		if(exist_handset_device_entity.isOnline()){
			
		}else{
			this.handsetDeviceOnline(ctx, wifiId, dto);
		}
	}
}
