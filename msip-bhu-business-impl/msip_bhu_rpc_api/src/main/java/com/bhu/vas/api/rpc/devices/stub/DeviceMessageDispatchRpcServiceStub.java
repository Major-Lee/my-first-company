package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceMessageDispatchRpcServiceStub implements IDeviceMessageDispatchRpcService{
	
	private final IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceMessageDispatchRpcServiceStub(IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService) {
        this.deviceMessageDispatchRpcService = deviceMessageDispatchRpcService;
    }

	@Override
	public void messageDispatch(String ctx, String payload, ParserHeader parserHeader) {
		if(StringUtils.isEmpty(ctx) || parserHeader == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		deviceMessageDispatchRpcService.messageDispatch(ctx, payload, parserHeader);
	}

	@Override
	public void cmupWithWifiDeviceOnlines(String ctx, List<WifiDeviceDTO> devices) {
		if(StringUtils.isEmpty(ctx)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		deviceMessageDispatchRpcService.cmupWithWifiDeviceOnlines(ctx, devices);
	}
}
