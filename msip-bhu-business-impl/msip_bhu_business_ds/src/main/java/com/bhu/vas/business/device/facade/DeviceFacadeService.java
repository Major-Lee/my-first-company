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
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());

		try{
			//1:wifi设备基础信息更新
			WifiDevice wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
			WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(wifi_device_entity.getId());
			if(exist_wifi_device_entity == null){
				wifiDeviceService.insert(wifi_device_entity);
			}else{
				wifiDeviceService.update(wifi_device_entity);
			}
			//2：wifi设备在线状态Redis更新
			WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), ctx);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
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
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		
		try{
			//1:wifi设备基础信息表中的在线状态更新
			WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(mac);
			if(exist_wifi_device_entity != null){
				exist_wifi_device_entity.setOnline(false);
				wifiDeviceService.update(exist_wifi_device_entity);
			}
			//2:wifi设备在线状态redis更新
			//首先进行验证,判断ctx信息是否一致,如果一致则删除
			String ctx_present = WifiDevicePresentService.getInstance().getPresent(mac);
			if(ctx.equals(ctx_present)){
				WifiDevicePresentService.getInstance().removePresent(mac);
				//3:在此wifi上的移动设备的在线状态更新
				List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(mac);
				if(!handset_devices_online_entitys.isEmpty()){
					for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
						handset_devices_online_entity.setOnline(false);
					}
					handsetDeviceService.updateAll(handset_devices_online_entitys);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	/**
	 * wifi设备告警信息
	 * @param ctx
	 * @param dto
	 */
	public void wifiDeviceAlarm(String ctx, WifiDeviceAlarmDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac_addr()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());

		try{
			WifiDeviceAlarm wifi_device_alarm_entity = BusinessModelBuilder.wifiDeviceAlarmDtoToEntity(dto);
			wifiDeviceAlarmService.insert(wifi_device_alarm_entity);
			//wifiDeviceAlarmService
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
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
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());

		try{
			//1:移动设备基础信息更新
			HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
			handset_device_entity.setLast_wifi_id(wifiId);
			HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(handset_device_entity.getId());
			if(exist_handset_device_entity == null){
				handsetDeviceService.insert(handset_device_entity);
			}else{
				handsetDeviceService.update(handset_device_entity);
			}
			//2:移动设备连接wifi设备的流水记录
			//wifiHandsetDeviceRelationMService.addRelation(dto.getMac(), wifiId, handset_device_entity.
			//		getLast_login_at());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	/**
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());

		try{
			//1:更新移动设备的online状态为false
			//HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
			HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac());
			if(exist_handset_device_entity != null){
				exist_handset_device_entity.setOnline(false);
				handsetDeviceService.update(exist_handset_device_entity);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	/**
	 * 移动设备连接状态sync
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String wifiId, HandsetDeviceDTO dto){
		this.handsetDeviceOnline(ctx, wifiId, dto);
	}
}
