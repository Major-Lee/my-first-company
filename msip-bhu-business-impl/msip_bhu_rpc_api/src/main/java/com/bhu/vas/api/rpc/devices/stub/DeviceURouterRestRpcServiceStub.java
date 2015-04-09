package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceURouterRestRpcServiceStub implements IDeviceURouterRestRpcService{
	
	private final IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceURouterRestRpcServiceStub(IDeviceURouterRestRpcService deviceURouterRestRpcService) {
        this.deviceURouterRestRpcService = deviceURouterRestRpcService;
    }

	@Override
	public URouterEnterVTO urouterEnter(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterEnter(uid, wifiId);
	}

	@Override
	public List<URouterHdVTO> urouterHdOnlineList(Integer uid, String wifiId,
			int start, int size) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		return deviceURouterRestRpcService.urouterHdOnlineList(uid, wifiId, start, size);
	}

}
