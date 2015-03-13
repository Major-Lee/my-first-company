package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;

public interface IDeviceRpcService {
	//public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceRegister(String message, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceLogout(String message, WifiDeviceContextDTO contextDto);
}
