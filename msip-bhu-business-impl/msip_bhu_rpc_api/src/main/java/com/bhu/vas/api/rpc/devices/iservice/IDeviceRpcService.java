package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.rpc.devices.dto.DeviceDTO;

public interface IDeviceRpcService {
	public boolean deviceRegister(DeviceDTO dto);
}
