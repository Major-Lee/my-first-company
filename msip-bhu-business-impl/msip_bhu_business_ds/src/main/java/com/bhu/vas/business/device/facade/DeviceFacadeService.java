package com.bhu.vas.business.device.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.builder.BusinessModelBuilder;
import com.bhu.vas.business.device.service.WifiDeviceService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/**
	 * 
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线更新
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
			//2：wifi设备在线更新
			WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), ctx);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	/**
	 * 移动设备上线
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto){
		
	}
	/**
	 * 移动设备下线
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto){
		
	}
	/**
	 * 移动设备连接状态sync
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, HandsetDeviceDTO dto){
		
	}
}
