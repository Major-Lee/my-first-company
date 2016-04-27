package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceRestRpcServiceStub implements IDeviceRestRpcService{
	
	private final IDeviceRestRpcService deviceRestRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceRestRpcServiceStub(IDeviceRestRpcService deviceRestRpcService) {
        this.deviceRestRpcService = deviceRestRpcService;
    }

	@Override
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
	}

//	@Override
//	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, int pageNo, int pageSize) {
//		if(pageNo < 0 || pageSize < 0) 
//			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
//		
//		return deviceRestRpcService.fetchWDevicesByKeyword(keyword, pageNo, pageSize);
//	}
	
	@Override
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword,
			String region, String excepts, int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchWDevicesByKeyword(keyword, region, excepts, pageNo, pageSize);
	}
	
	@Override
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeywords(String mac, String sn, 
			String orig_swver, String origvapmodule,String adr, String work_mode,
			String config_mode, String devicetype, Boolean online,Boolean moduleonline, Boolean newVersionDevice, Boolean canOperateable,
			String region, String excepts, String groupids, String groupids_excepts, int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		return deviceRestRpcService.fetchWDevicesByKeywords(mac, sn, orig_swver,origvapmodule, adr,work_mode, config_mode, 
				devicetype, online,moduleonline, newVersionDevice,canOperateable, region, excepts, groupids, groupids_excepts, pageNo, pageSize);
	}
	
	@Override
	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions){
		if(StringUtils.isEmpty(regions)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchWDeviceRegionCount(regions);
	}
	
	@Override
	public TailPage<WifiDeviceVTO> fetchRecentWDevice(int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchRecentWDevice(pageNo, pageSize);
	}
	
	@Override
	public TailPage<HandsetDeviceVTO> fetchHDevices(String wifiId, int pageNo, int pageSize) {
		if(StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchHDevices(wifiId, pageNo, pageSize);
	}
	
	@Override
	public StatisticsGeneralVTO fetchStatisticsGeneral(){
		return deviceRestRpcService.fetchStatisticsGeneral();
	}

	@Override
	public RpcResponseDTO<List<PersistenceCMDDetailDTO>> fetchDevicePersistenceDetailCMD(String wifiId) {
		if(StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchDevicePersistenceDetailCMD(wifiId);
	}

	@Override
	public RpcResponseDTO<String> fetchDevicePresent(String wifiId) {
		if (StringUtils.isEmpty(wifiId))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		return deviceRestRpcService.fetchDevicePresent(wifiId);
	}

	@Override
	public RpcResponseDTO<TailPage<WifiDeviceVTO1>> fetchBySearchConditionMessage(String message, int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchBySearchConditionMessage(message, pageNo, pageSize);

	}

	@Override
	public RpcResponseDTO<UserSearchConditionDTO> storeUserSearchCondition(int uid,String message,String desc) {
		if(StringUtils.isEmpty(message))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.storeUserSearchCondition(uid, message, desc);
	}

	@Override
	public RpcResponseDTO<Boolean> removeUserSearchCondition(int uid, long ts) {
		return deviceRestRpcService.removeUserSearchCondition(uid, ts);
	}

	@Override
	public RpcResponseDTO<Boolean> removeUserSearchConditions(int uid, String message_ts_splits) {
		return deviceRestRpcService.removeUserSearchConditions(uid, message_ts_splits);
	}
	
	@Override
	public RpcResponseDTO<TailPage<UserSearchConditionDTO>> fetchUserSearchConditions(int uid, int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceRestRpcService.fetchUserSearchConditions(uid, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<List<UserAgentVTO>> fetchAgents(int uid) {
		return deviceRestRpcService.fetchAgents(uid);
	}

	@Override
	public RpcResponseDTO<String> exportWifiDeviceResult(int uid, String message) {
		return deviceRestRpcService.exportWifiDeviceResult(uid, message);
	}
	
	@Override
	public RpcResponseDTO<String> exportOrderResult(int uid, String message) {
		return deviceRestRpcService.exportOrderResult(uid, message);
	}

	/*@Override
	public Collection<GeoMapVTO> fetchGeoMap() {
		return deviceRestRpcService.fetchGeoMap();
	}*/


}
