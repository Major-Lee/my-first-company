package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;

public interface IDeviceRpcService {
	//public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceRegister(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceLogout(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto);
}
