package com.bhu.vas.api.rpc.devices.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;


public interface IDeviceURouterRestRpcService {
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status, int start, int size);
	
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size);
	
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId);
}
