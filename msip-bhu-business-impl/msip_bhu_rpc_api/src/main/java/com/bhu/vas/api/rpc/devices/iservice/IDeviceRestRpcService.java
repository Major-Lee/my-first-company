package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceRestRpcService {
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize);
	//public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, int pageNo, int pageSize);
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, String region, String excepts, 
			int pageNo, int pageSize);
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeywords(String mac, String sn,
			String orig_swver,String origvapmodule, String adr, String work_mode,
			String config_mode, String devicetype, Boolean online,Boolean moduleonline, Boolean newVersionDevice, 
			String region, String excepts, String groupids, String groupids_excepts, int pageNo, int pageSize);
	public StatisticsGeneralVTO fetchStatisticsGeneral();
	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions);
	public TailPage<WifiDeviceVTO> fetchRecentWDevice(int pageNo, int pageSize);
	public TailPage<HandsetDeviceVTO> fetchHDevices(String wifiId, int pageNo, int pageSize);
	
	public RpcResponseDTO<List<PersistenceCMDDetailDTO>> fetchDevicePersistenceDetailCMD(String wifiId);
	//public Collection<GeoMapVTO> fetchGeoMap();
}
