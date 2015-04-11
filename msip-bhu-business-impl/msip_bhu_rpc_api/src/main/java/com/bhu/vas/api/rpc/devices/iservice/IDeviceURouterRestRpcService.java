package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;


public interface IDeviceURouterRestRpcService {
	public URouterEnterVTO urouterEnter(Integer uid, String wifiId);
	
	public List<URouterHdVTO> urouterHdList(Integer uid, String wifiId, int status, int start, int size);
}
