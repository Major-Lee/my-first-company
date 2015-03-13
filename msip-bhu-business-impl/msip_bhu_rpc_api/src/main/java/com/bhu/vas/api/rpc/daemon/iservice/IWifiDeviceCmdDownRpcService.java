package com.bhu.vas.api.rpc.daemon.iservice;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;

public interface IWifiDeviceCmdDownRpcService {
	public boolean wifiDeviceRegister(WifiDeviceContextDTO dto);
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto,String cmd );
}
