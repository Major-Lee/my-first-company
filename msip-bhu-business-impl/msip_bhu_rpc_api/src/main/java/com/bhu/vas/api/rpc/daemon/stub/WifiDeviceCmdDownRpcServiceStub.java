package com.bhu.vas.api.rpc.daemon.stub;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.daemon.iservice.IWifiDeviceCmdDownRpcService;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceContextDTO;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class WifiDeviceCmdDownRpcServiceStub implements IWifiDeviceCmdDownRpcService{
	
	private final IWifiDeviceCmdDownRpcService wifiDeviceCmdDownRpcService;
	
    // 构造函数传入真正的远程代理对象
    public WifiDeviceCmdDownRpcServiceStub(IWifiDeviceCmdDownRpcService wifiDeviceCmdDownRpcService) {
        this.wifiDeviceCmdDownRpcService = wifiDeviceCmdDownRpcService;
    }

	@Override
	public boolean wifiDeviceRegister(WifiDeviceContextDTO dto) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return wifiDeviceCmdDownRpcService.wifiDeviceRegister(dto);
	}

	@Override
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto, String cmd) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return wifiDeviceCmdDownRpcService.wifiDeviceCmdDown(dto,cmd);
	}

	/*@Override
	public boolean deviceRegister(DeviceDTO dto) {
		// TODO Auto-generated method stub
		//System.out.println("deviceRegister stub:");
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRpcService.deviceRegister(dto);
	}*/
}
