package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceIndustryVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDevicePresentVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.bhu.vas.api.vto.device.UpgradeCheckVTO;
import com.bhu.vas.api.vto.statistics.DeviceStatisticsVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceRestRpcService {
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize);
	//public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, int pageNo, int pageSize);
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, String region, String excepts, 
			int pageNo, int pageSize);
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeywords(String mac, String sn,
			String orig_swver,String origvapmodule, String adr, String work_mode,
			String config_mode, String devicetype, Boolean online,Boolean moduleonline, Boolean newVersionDevice, Boolean canOperateable,
			String region, String excepts, String groupids, String groupids_excepts, int pageNo, int pageSize);
	public StatisticsGeneralVTO fetchStatisticsGeneral();
	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions);
	public TailPage<WifiDeviceVTO> fetchRecentWDevice(int pageNo, int pageSize);
	public TailPage<HandsetDeviceVTO> fetchHDevices(String wifiId, int pageNo, int pageSize);
	
	public RpcResponseDTO<List<PersistenceCMDDetailDTO>> fetchDevicePersistenceDetailCMD(String wifiId);
	public RpcResponseDTO<String> fetchDevicePresent(String wifiId);
	public RpcResponseDTO<List<TailPage<WifiDeviceVTO1>>> fetchBySearchConditionMessages(int pageNo, int pageSize, String... messages);

	//public Collection<GeoMapVTO> fetchGeoMap();
	
	public RpcResponseDTO<UserSearchConditionDTO> storeUserSearchCondition(int uid, String message, String desc);
	public RpcResponseDTO<Boolean> removeUserSearchCondition(int uid, long ts);
	public RpcResponseDTO<Boolean> removeUserSearchConditions(int uid, String message_ts_splits);
	public RpcResponseDTO<TailPage<UserSearchConditionDTO>> fetchUserSearchConditions(int uid, int pageNo, int pageSize);
	public RpcResponseDTO<List<UserAgentVTO>> fetchAgents(int uid);
	public RpcResponseDTO<String> exportWifiDeviceResult(int uid, String message);
	public RpcResponseDTO<String> exportOrderResult(int uid, String message, int messagetype, String start_date, String end_date);

	public long countByUCExtensionOnline(int uid, String t_uc_extension, String online);
	public RpcResponseDTO<DeviceStatisticsVTO> deviceStatistics(String d_snk_turnstate, String d_snk_type);
	public RpcResponseDTO<List<WifiDevicePresentVTO>> fetchDevicesPresent(List<String> dmacs);
	public RpcResponseDTO<Boolean> deviceInfoUpdate(int uid, List<String> dmacs, String industry, String merchant_name);
	public RpcResponseDTO<List<WifiDeviceIndustryVTO>> fetchIndustyList();
	public RpcResponseDTO<UpgradeCheckVTO> checkDeviceUpgradeNoAction(String mac, String origswver);
}
