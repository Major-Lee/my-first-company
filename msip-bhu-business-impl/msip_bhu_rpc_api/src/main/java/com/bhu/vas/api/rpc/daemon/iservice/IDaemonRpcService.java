package com.bhu.vas.api.rpc.daemon.iservice;

import java.util.List;

import com.bhu.vas.api.dto.CmCtxInfo;

public interface IDaemonRpcService {
	public boolean cmJoinService(CmCtxInfo info);
	public boolean cmLeave(CmCtxInfo info);
	public boolean wifiDevicesOnline(String ctx,List<String> macs);
	public boolean wifiDeviceOnline(String ctx,String mac);
	public boolean wifiDeviceOffline(String ctx,String mac);
	public boolean wifiDeviceCmdDown(String ctx,String mac,String cmd );
	public boolean wifiDeviceCmdsDown(String ctx,String mac,List<String> cmds );
	
	/*public boolean wifiDeviceCmdsDown(List<DownCmds> downCmds );
	public boolean wifiDeviceCmdDown(DownCmds downCmd);*/
	//public boolean wifiDeviceSerialTaskComming(String ctx,String payload, ParserHeader parserHeader);//String mac,QuerySerialReturnDTO dto);
	public boolean wifiDevicesOnlineTimer();
	public boolean wifiDevicesSimulateCmdTimer();
	//public boolean wifiDevicesLocationQuerySerialTimer();
}
