package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class URouterDeviceFacadeService {
	private final Logger logger = LoggerFactory.getLogger(URouterDeviceFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	/**
	 * 1:验证设备是否存在
	 * 2:验证设备是否在线
	 * @param wifiId
	 * @return
	 */
	public WifiDevice validateDevice(String wifiId){
		//验证设备是否存在
		WifiDevice device_entity = wifiDeviceService.getById(wifiId);
		if(device_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
		}
		//验证设备是否在线
		if(!device_entity.isOnline()){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE);
		}
		return device_entity;
	}
	/**
	 * 验证设备是否加载配置
	 * @param wifiId
	 * @return
	 */
	public WifiDeviceSetting validateDeviceSetting(String wifiId){
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
		if(entity == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_NOTEXIST);
		}
		return entity;
	}
	
	/**
	 * 验证用户是否管理设备
	 * @param uid
	 * @param wifiId
	 */
	public UserDevice validateUserDevice(Integer uid, String wifiId){
		//验证用户是否管理设备
		UserDevice userdevice_entity = userDeviceService.getById(new UserDevicePK(wifiId, uid));
		if(userdevice_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_BINDED);
		}
		return userdevice_entity;
	}
}
