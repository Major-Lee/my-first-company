package com.bhu.vas.api.rpc.devices.stub;

import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;

public class DeviceMessageDispatchRpcServiceStub implements IDeviceMessageDispatchRpcService{
	
	private final IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceMessageDispatchRpcServiceStub(IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService) {
        this.deviceMessageDispatchRpcService = deviceMessageDispatchRpcService;
    }

	@Override
	public void messageDispatch(ParserHeader parserHeader) {
	}

	/*@Override
	public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto) {
		// TODO Auto-generated method stub
		//System.out.println("deviceRegister stub:");
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRpcService.deviceRegister(dto, contextDto);
	}*/

//	@Override
//	public boolean wifiDeviceRegister(String message, WifiDeviceContextDTO contextDto) {
//		if(StringUtils.isEmpty(message)) 
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
//		return deviceRpcService.wifiDeviceRegister(message, contextDto);
//	}
//
//	@Override
//	public boolean wifiDeviceLogout(String message, WifiDeviceContextDTO contextDto) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
