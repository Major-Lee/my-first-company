package com.bhu.vas.business.device.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;
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
	public void wifiDeviceRegister(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto){
		if(dto == null || contextDto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(contextDto.getInfo().toString()))
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
			WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), contextDto.getInfo().toString());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
}
