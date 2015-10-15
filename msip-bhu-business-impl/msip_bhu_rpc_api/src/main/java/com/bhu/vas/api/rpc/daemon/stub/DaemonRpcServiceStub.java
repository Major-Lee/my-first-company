package com.bhu.vas.api.rpc.daemon.stub;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.DownCmds;
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
	public boolean wifiDeviceOnline(String ctx,String mac) {
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceOnline(ctx,mac);
	}
	@Override
	public boolean wifiDeviceOffline(String ctx,String mac) {
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceOffline(ctx,mac);
	}
	@Override
	public boolean wifiDeviceCmdDown(String ctx,String mac, String cmd) {
		if(/*StringUtils.isEmpty(ctx) || */StringUtils.isEmpty(mac) || StringUtils.isEmpty(cmd)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceCmdDown(ctx,mac,cmd);
	}
	
	@Override
	public boolean wifiDeviceCmdsDown(String ctx, String mac, List<String> cmds) {
		if(StringUtils.isEmpty(mac) || cmds == null || cmds.isEmpty()) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceCmdsDown(ctx,mac,cmds);
	}
	
/*	@Override
	public boolean cmJoinService(CmCtxInfo info) {
		if(info == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.cmJoinService(info);
	}

	@Override
	public boolean cmLeave(CmCtxInfo info) {
		if(info == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.cmLeave(info);
	}*/

	/*@Override
	public boolean wifiDeviceSerialTaskComming(String ctx, String payload, ParserHeader parserHeader){//String mac,QuerySerialReturnDTO dto) {
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(payload) || parserHeader == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDeviceSerialTaskComming(ctx,payload,parserHeader);
	}*/

	@Override
	public boolean wifiDevicesOnline(String ctx, List<String> macs) {
		if(StringUtils.isEmpty(ctx) || macs == null || macs.isEmpty()) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiDevicesOnline(ctx,macs);
	}

	@Override
	public boolean wifiDevicesOnlineTimer() {
		return daemonRpcService.wifiDevicesOnlineTimer();
	}

	@Override
	public boolean wifiDevicesSimulateCmdTimer() {
		return daemonRpcService.wifiDevicesSimulateCmdTimer();
	}

	@Override
	public boolean wifiMultiDevicesCmdsDown(DownCmds... downCmds) {
		if(downCmds == null || downCmds.length ==0) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return daemonRpcService.wifiMultiDevicesCmdsDown(downCmds);
	}

	/*public boolean wifiDevicesLocationQuerySerialTimer(){
		return daemonRpcService.wifiDevicesLocationQuerySerialTimer();
	}*/
	/*@Override
	public boolean deviceRegister(DeviceDTO dto) {
		// TODO Auto-generated method stub
		//System.out.println("deviceRegister stub:");
		if(dto == null || StringUtils.isEmpty(dto.getMac())) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRpcService.deviceRegister(dto);
	}*/
}
