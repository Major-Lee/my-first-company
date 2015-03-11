package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.rpc.devices.dto.DeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceDTO;

public interface IDeviceRpcService {
	public boolean deviceRegister(DeviceDTO dto);
	public boolean wifiDeviceRegister(WifiDeviceDTO dto);
}
