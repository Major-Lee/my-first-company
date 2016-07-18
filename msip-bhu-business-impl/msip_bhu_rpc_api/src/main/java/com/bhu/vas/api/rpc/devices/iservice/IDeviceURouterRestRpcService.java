package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.vto.URouterAdminPasswordVTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdDetailVTO;
import com.bhu.vas.api.vto.URouterHdHostNameVTO;
import com.bhu.vas.api.vto.URouterMainEnterVTO;
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakSectionsDTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.api.vto.URouterVapPasswordVTO;
import com.bhu.vas.api.vto.URouterWSCommunityVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigMutilVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.api.vto.guest.URouterVisitorListVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceURouterRestRpcService {
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId);
	RpcResponseDTO<URouterMainEnterVTO> urouterMainEnter(Integer uid, String wifiId);
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status, int start, int size,Boolean filterWiredHandset);
	public RpcResponseDTO<Map<String, Object>> urouterAllHdList(Integer uid, String wifiId, int start, int size,Boolean filterWiredHandset);
	RpcResponseDTO<URouterHdDetailVTO> urouterHdDetail(Integer uid, String wifiId, String hd_mac);

	RpcResponseDTO<Long> urouterHdModifyAlias(Integer uid, String wifiId, String hd_mac, String alias);
	
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId);
	
	public RpcResponseDTO<String> urouterPeakSection(Integer uid, String wifiId, int type, int period, int duration);
	
	public RpcResponseDTO<URouterPeakSectionsDTO> urouterPeakSectionFetch(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size);
	
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId);
	
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid, String wifiId);
	
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut, String pt);
	
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid, String d, String dt);
	
	public RpcResponseDTO<Map<String,Object>> urouterPlugins(Integer uid, String wifiId);
	
	public RpcResponseDTO<Boolean> urouterUpdPluginTerminalOnline(Integer uid, String wifiId, boolean on, 
			boolean stranger_on, boolean alias_on, String timeslot, int timeslot_mode);
	
	public RpcResponseDTO<Boolean> urouterUpdNotifyReward(Integer uid, boolean on);

	public RpcResponseDTO<Boolean> urouterUpdPluginWifisniffer(Integer uid,String wifiId, boolean on);
	public RpcResponseDTO<DeviceUsedStatisticsDTO> urouterDeviceUsedStatusQuery(Integer uid,String wifiId);
	public RpcResponseDTO<URouterDeviceConfigVTO> urouterConfigs(Integer uid, String mac);
	public RpcResponseDTO<URouterDeviceConfigMutilVTO> urouterConfigsSupportMulti(Integer uid, String mac);
	
	public RpcResponseDTO<List<URouterHdHostNameVTO>> terminalHostnames(Integer uid,String dmac, String hmacs);

	RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId);

	RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId);
	
	public RpcResponseDTO<Map<String, Object>> urouterWSRecent(Integer uid, String mac, int start, int size);
	
	public RpcResponseDTO<Map<String, Object>> urouterWSNeighbour(Integer uid, String mac, int start, int size);
	
	public RpcResponseDTO<Boolean> urouterWSFocus(Integer uid, String hd_mac, boolean focus);
	
	public RpcResponseDTO<Boolean> urouterWSNick(Integer uid, String hd_mac, String nick);
	
	public RpcResponseDTO<Map<String,Object>> urouterWSDetails(Integer uid, String mac, String hd_mac, int start, int size);

	public RpcResponseDTO<URouterWSCommunityVTO> urouterWSCommunity(Integer uid, String mac);


	RpcResponseDTO<URouterVisitorListVTO> urouterVisitorList(Integer uid, String mac, int start, int size);

	RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOffline(Integer uid, String mac, int start, int size);

	RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOnline(Integer uid, String mac, int start, int size);

	RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListAll(Integer uid, String mac, int start, int size);

	RpcResponseDTO<Boolean> urouterVisitorRemoveHandset(Integer uid, String mac, String hd_mac);

	RpcResponseDTO<TailPage<UserDeviceDTO>> urouterFetchBySearchConditionMessage(Integer uid, String message, int pageNo, int pageSize);

	RpcResponseDTO<Integer> countOnlineByTimestamp(Integer uid, String mac, Long timestamp);


}
