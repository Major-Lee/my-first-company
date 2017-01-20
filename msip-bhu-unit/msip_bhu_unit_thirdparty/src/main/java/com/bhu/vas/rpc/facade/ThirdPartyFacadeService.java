package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.business.bucache.redis.serviceimpl.thirdparty.ThirdPartyDeviceService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 第三方业务的service
 * @author yetao
 *
 */
@Service
public class ThirdPartyFacadeService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyFacadeService.class);
	
	@Resource 
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;
	
	public Boolean gomeBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			ThirdPartyDeviceService.bindDevice(mac);
			return Boolean.TRUE;
		} else {
			WifiDeviceSharedealConfigs wifiDeviceSharedeal = wifiDeviceSharedealConfigsService.getById(mac);
			if(wifiDeviceSharedeal == null || wifiDeviceSharedeal.getDistributor() != BusinessRuntimeConfiguration.GomeDistributorId)
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			ThirdPartyDeviceService.bindDevice(mac);
		}
		return Boolean.TRUE;
	}
	
	public Boolean gomeUnBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac))
			ThirdPartyDeviceService.unBindDevice(mac);
		return Boolean.TRUE;
	}
}
