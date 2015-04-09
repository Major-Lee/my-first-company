package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.vto.URouterEnterVTO;


public interface IDeviceURouterRestRpcService {
	public URouterEnterVTO urouterEnter(String wifiId);
}
