package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
	
	
	public URouterEnterVTO urouterEnter(String wifiId){
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
		if(entity == null) {
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		}
		return null;
	}
}
