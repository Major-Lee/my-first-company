package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
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
	public RpcResponseDTO<List<URouterHdVTO>> urouterHdList(Integer uid, String wifiId, int status,
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
	public RpcResponseDTO<List<URouterHdVTO>> urouterBlockList(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterBlockList(uid, wifiId);
	}

}
