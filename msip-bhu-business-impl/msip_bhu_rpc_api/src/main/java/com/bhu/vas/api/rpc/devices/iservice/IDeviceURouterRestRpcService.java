package com.bhu.vas.api.rpc.devices.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.*;


public interface IDeviceURouterRestRpcService {
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status, int start, int size);
	
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId);
	
	public RpcResponseDTO<URouterPeakRateVTO> urouterPeakRate(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size);
	
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId);
	
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid, String wifiId);
	
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut, String pt);
	
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid, String d, String dt);

	RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId);

	RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId);
}
