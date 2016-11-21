package com.bhu.vas.api.rpc.devices.notify;

import java.util.List;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;

public interface ISharedNetworkModifyNotifyCallback {
	void notify(List<WifiDeviceSharedNetwork> result);
}
