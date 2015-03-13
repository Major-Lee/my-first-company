package com.bhu.vas.api.rpc.daemon.iservice;

import com.bhu.vas.api.dto.CmInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;

public interface IDaemonRpcService {
	public boolean cmJoinService(CmInfo info);
	public boolean cmLeave(CmInfo info);
	public boolean wifiDeviceOnline(WifiDeviceContextDTO dto);
	public boolean wifiDeviceOffline(WifiDeviceContextDTO dto);
	public boolean wifiDeviceCmdDown(CmInfo info,String cmd );
}
