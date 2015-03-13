package com.bhu.vas.api.rpc.devices.stub;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.DeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceServiceStub implements IDeviceRpcService{
	
	private final IDeviceRpcService deviceRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceServiceStub(IDeviceRpcService deviceRpcService) {
        this.deviceRpcService = deviceRpcService;
    }

	@Override
	public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto) {
		// TODO Auto-generated method stub
		//System.out.println("deviceRegister stub:");
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRpcService.deviceRegister(dto, contextDto);
	}

	@Override
	public boolean wifiDeviceRegister(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRpcService.wifiDeviceRegister(dto, contextDto);
	}

	@Override
	public boolean wifiDeviceLogout(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto) {
		// TODO Auto-generated method stub
		return false;
	}
}
