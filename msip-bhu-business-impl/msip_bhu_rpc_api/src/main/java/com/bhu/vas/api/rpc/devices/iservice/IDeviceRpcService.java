package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.rpc.devices.dto.DeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceDTO;

public interface IDeviceRpcService {
	public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceRegister(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto);
	public boolean wifiDeviceLogout(WifiDeviceDTO dto, WifiDeviceContextDTO contextDto);
}
