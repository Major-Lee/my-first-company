package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.URouterAdminPasswordVTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdHostNameVTO;
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakRateVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.api.vto.URouterVapPasswordVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;


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
	
	public RpcResponseDTO<Map<String,Object>> urouterPlugins(Integer uid, String wifiId);
	
	public RpcResponseDTO<Boolean> urouterUpdPluginTerminalOnline(Integer uid, String wifiId, boolean on, 
			boolean stranger_on, String timeslot, int timeslot_mode);
	public RpcResponseDTO<Boolean> urouterUpdPluginWifisniffer(Integer uid,String wifiId, boolean on);
	public RpcResponseDTO<URouterDeviceConfigVTO> urouterConfigs(Integer uid, String mac);
	
	public RpcResponseDTO<List<URouterHdHostNameVTO>> terminalHostnames(Integer uid, String macs);

	RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId);

	RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId);
}
