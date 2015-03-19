package com.bhu.vas.api.rpc.daemon.iservice;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.header.ParserHeader;

public interface IDaemonRpcService {
	public boolean cmJoinService(CmCtxInfo info);
	public boolean cmLeave(CmCtxInfo info);
	public boolean wifiDeviceOnline(String ctx,String mac);
	public boolean wifiDeviceOffline(String ctx,String mac);
	public boolean wifiDeviceCmdDown(String ctx,String mac,String cmd );
	
	public boolean wifiDeviceSerialTaskComming(String ctx,String payload, ParserHeader parserHeader);//String mac,QuerySerialReturnDTO dto);
}
