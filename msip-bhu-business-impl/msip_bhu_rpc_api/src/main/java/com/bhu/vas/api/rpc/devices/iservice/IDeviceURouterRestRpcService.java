package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;


public interface IDeviceURouterRestRpcService {
	public URouterEnterVTO urouterEnter(Integer uid, String wifiId);
	
	public List<URouterHdVTO> urouterHdOnlineList(Integer uid, String wifiId, int start, int size);
}
