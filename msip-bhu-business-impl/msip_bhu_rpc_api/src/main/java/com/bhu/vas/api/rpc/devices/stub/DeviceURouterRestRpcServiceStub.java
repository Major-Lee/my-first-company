package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
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
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.api.vto.guest.URouterVisitorListVTO;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceURouterRestRpcServiceStub implements IDeviceURouterRestRpcService{
	
	private final IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceURouterRestRpcServiceStub(IDeviceURouterRestRpcService deviceURouterRestRpcService) {
        this.deviceURouterRestRpcService = deviceURouterRestRpcService;
    }

	@Override
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterEnter(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status,
			int start, int size,Boolean filterWiredHandset) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterHdList(uid, wifiId, status, start, size,filterWiredHandset);
	}

	@Override
	public RpcResponseDTO<URouterHdDetailVTO> urouterHdDetail(Integer uid, String wifiId, String hd_mac) {
		if(uid == null || StringUtils.isEmpty(wifiId))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);

		return deviceURouterRestRpcService.urouterHdDetail(uid, wifiId, hd_mac);
	}

	@Override
	public RpcResponseDTO<Long> urouterHdModifyAlias(Integer uid, String wifiId, String hd_mac, String alias) {
		if(uid == null || StringUtils.isEmpty(wifiId))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);

		return deviceURouterRestRpcService.urouterHdModifyAlias(uid, wifiId, hd_mac, alias);
	}

	@Override
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterRealtimeRate(uid, wifiId);
	}
	
	@Override
	public RpcResponseDTO<String> urouterPeakSection(Integer uid, String wifiId, int type, int period, int duration) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterPeakSection(uid, wifiId, type, period, duration);
	}
	
	@Override
	public RpcResponseDTO<URouterPeakSectionsDTO> urouterPeakSectionFetch(Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterPeakSectionFetch(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterBlockList(uid, wifiId, start, size);
	}

	@Override
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid,String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterSetting(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid,
			String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterLinkMode(uid, wifiId);
	}

	
	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut,
			String pt) {
		if(uid == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt) || StringUtils.isEmpty(pt)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterUserMobileDeviceRegister(uid, d, dt, dm, cv, pv, ut, pt);
	}
	

	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid,
			String d, String dt) {
		if(uid == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterUserMobileDeviceDestory(uid, d, dt);
	}
	
	@Override
	public RpcResponseDTO<Map<String, Object>> urouterPlugins(Integer uid,
			String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterPlugins(uid, wifiId);
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterUpdPluginTerminalOnline(Integer uid,
			String wifiId, boolean on, boolean stranger_on,boolean alias_on, String timeslot,
			int timeslot_mode) {
		if(uid == null || StringUtils.isEmpty(wifiId) || StringUtils.isEmpty(timeslot)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterUpdPluginTerminalOnline(uid, wifiId, on, 
				stranger_on, alias_on, timeslot, timeslot_mode);
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterUpdPluginWifisniffer(Integer uid,
			String wifiId, boolean on) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterUpdPluginWifisniffer(uid, wifiId, on);
	}
	
	@Override
	public RpcResponseDTO<DeviceUsedStatisticsDTO> urouterDeviceUsedStatusQuery(
			Integer uid, String wifiId) {
		if(uid == null || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterDeviceUsedStatusQuery(uid, wifiId);
	}
	
	@Override
	public RpcResponseDTO<URouterDeviceConfigVTO> urouterConfigs(Integer uid,
			String mac) {
		if(uid == null || StringUtils.isEmpty(mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterConfigs(uid, mac);
	}
	
	@Override
	public RpcResponseDTO<List<URouterHdHostNameVTO>> terminalHostnames(Integer uid, String macs) {
		if(uid == null || StringUtils.isEmpty(macs)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.terminalHostnames(uid, macs);
	}
	
	@Override
	public RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId) {
		return deviceURouterRestRpcService.urouterAdminPassword(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId) {
		return deviceURouterRestRpcService.urouterVapPassword(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> urouterWSRecent(Integer uid, String mac, int start, int size) {
		if(uid == null || StringUtils.isEmpty(mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterWSRecent(uid, mac, start, size);
	}
	
	@Override
	public RpcResponseDTO<Map<String, Object>> urouterWSNeighbour(Integer uid, String mac, int start, int size) {
		if(uid == null || StringUtils.isEmpty(mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterWSNeighbour(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<Boolean> urouterWSFocus(Integer uid, String hd_mac, boolean focus) {
		if(uid == null || StringUtils.isEmpty(hd_mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		if(!StringHelper.isValidMac(hd_mac))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);

		return deviceURouterRestRpcService.urouterWSFocus(uid, hd_mac, focus);
	}

	@Override
	public RpcResponseDTO<Boolean> urouterWSNick(Integer uid, String hd_mac, String nick) {
		if(uid == null || StringUtils.isEmpty(hd_mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		if(!StringHelper.isValidMac(hd_mac))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		if(!StringUtils.isEmpty(nick)){
			int length = StringHelper.realStringCharlength(nick);
			if(length > 12){
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFISTASNIFFER_NICK_LENGTH_INVALID);
			}
			//WIFISTASNIFFER_NICK_LENGTH_INVALID
		}
		
		return deviceURouterRestRpcService.urouterWSNick(uid, hd_mac, nick);
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterWSDetails(Integer uid, String mac, String hd_mac, int start, int size) {
		if(uid == null || StringUtils.isEmpty(mac) || StringUtils.isEmpty(hd_mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterWSDetails(uid, mac, hd_mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterWSCommunityVTO> urouterWSCommunity(Integer uid, String mac) {
		if(uid == null || StringUtils.isEmpty(mac)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		
		return deviceURouterRestRpcService.urouterWSCommunity(uid, mac);
	}


	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorList(Integer uid, String mac, int start, int size) {
		return deviceURouterRestRpcService.urouterVisitorList(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOffline(Integer uid, String mac, int start, int size) {
		return deviceURouterRestRpcService.urouterVisitorListOffline(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOnline(Integer uid, String mac, int start, int size) {
		return deviceURouterRestRpcService.urouterVisitorListOnline(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListAll(Integer uid, String mac, int start, int size) {
		return deviceURouterRestRpcService.urouterVisitorListAll(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<Boolean> urouterVisitorRemoveHandset(Integer uid, String mac, String hd_mac) {
		return deviceURouterRestRpcService.urouterVisitorRemoveHandset(uid, mac, hd_mac);
	}



	@Override
	public RpcResponseDTO<URouterMainEnterVTO> urouterMainEnter(Integer uid,
			String wifiId) {
		return deviceURouterRestRpcService.urouterMainEnter(uid, wifiId);
	}
}
