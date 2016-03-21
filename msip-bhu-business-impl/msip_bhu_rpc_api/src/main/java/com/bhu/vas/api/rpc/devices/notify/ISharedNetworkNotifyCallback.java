package com.bhu.vas.api.rpc.devices.notify;

import java.util.List;

import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;

public interface ISharedNetworkNotifyCallback {
	void notify(ParamSharedNetworkDTO current,List<String> dmacs);
}
