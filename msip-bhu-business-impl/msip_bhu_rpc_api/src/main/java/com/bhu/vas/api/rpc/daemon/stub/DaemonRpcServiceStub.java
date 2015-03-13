package com.bhu.vas.api.rpc.daemon.stub;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.CmInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DaemonRpcServiceStub implements IDaemonRpcService{
	
	private final IDaemonRpcService daemonRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DaemonRpcServiceStub(IDaemonRpcService daemonRpcService) {
        this.daemonRpcService = daemonRpcService;
    }

	@Override
	public boolean wifiDeviceOnline(WifiDeviceContextDTO dto) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceOnline(dto);
	}
	@Override
	public boolean wifiDeviceOffline(WifiDeviceContextDTO dto) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceOffline(dto);
	}
	@Override
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto, String cmd) {
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceCmdDown(dto,cmd);
	}

	@Override
	public boolean cmJoinService(CmInfo info) {
		if(info == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.cmJoinService(info);
	}

	@Override
	public boolean cmLeave(CmInfo info) {
		if(info == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.cmLeave(info);
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
