package com.bhu.vas.api.rpc.devices.stub;

import java.util.Map;

import com.bhu.vas.api.vto.*;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceURouterRestRpcServiceStub implements IDeviceURouterRestRpcService{
	
	private final IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceURouterRestRpcServiceStub(IDeviceURouterRestRpcService deviceURouterRestRpcService) {
        this.deviceURouterRestRpcService = deviceURouterRestRpcService;
    }

	@Override
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterEnter(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status,
			int start, int size) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterHdList(uid, wifiId, status, start, size);
	}
	
	@Override
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterRealtimeRate(uid, wifiId);
	}
	
	@Override
	public RpcResponseDTO<URouterPeakRateVTO> urouterPeakRate(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterPeakRate(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterBlockList(uid, wifiId, start, size);
	}

	@Override
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid,String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterSetting(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid,
			String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterLinkMode(uid, wifiId);
	}

	
	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut,
			String pt) {
		if(uid == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt) || StringUtils.isEmpty(pt)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterUserMobileDeviceRegister(uid, d, dt, dm, cv, pv, ut, pt);
	}
	

	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid,
			String d, String dt) {
		if(uid == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterUserMobileDeviceDestory(uid, d, dt);
	}
	
	
	@Override
	public RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId) {
		return deviceURouterRestRpcService.urouterAdminPassword(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId) {
		return deviceURouterRestRpcService.urouterVapPassword(uid, wifiId);
	}


}
