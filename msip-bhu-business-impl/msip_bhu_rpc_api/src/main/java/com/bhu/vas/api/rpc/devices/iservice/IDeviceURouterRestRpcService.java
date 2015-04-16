package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;


public interface IDeviceURouterRestRpcService {
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId);
	
	public RpcResponseDTO<List<URouterHdVTO>> urouterHdList(Integer uid, String wifiId, int status, int start, int size);
	
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId);
	
	public RpcResponseDTO<List<URouterHdVTO>> urouterBlockList(Integer uid, String wifiId);
}
