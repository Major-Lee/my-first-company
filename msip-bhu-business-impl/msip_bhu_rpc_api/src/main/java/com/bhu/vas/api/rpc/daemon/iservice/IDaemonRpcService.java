package com.bhu.vas.api.rpc.daemon.iservice;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;

public interface IDaemonRpcService {
	public boolean cmJoinService(CmCtxInfo info);
	public boolean cmLeave(CmCtxInfo info);
	public boolean wifiDeviceOnline(WifiDeviceContextDTO dto);
	public boolean wifiDeviceOffline(WifiDeviceContextDTO dto);
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto,String cmd );
}
