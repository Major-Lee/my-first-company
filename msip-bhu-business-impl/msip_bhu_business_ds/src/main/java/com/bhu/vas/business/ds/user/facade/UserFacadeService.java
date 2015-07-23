package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceStateService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;

@Service
public class UserFacadeService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserTokenService userTokenService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private UserMobileDeviceService userMobileDeviceService;
	
	@Resource
	private UserMobileDeviceStateService userMobileDeviceStateService;

	
	public boolean clearUsersMarkByUid(int uid){
		User byId = userService.getById(uid);
		if(byId != null){
			UniqueFacadeService.removeByMobileno(byId.getCountrycode(), byId.getMobileno());
		}else{
			return false;
		}
		userTokenService.deleteById(uid);
		List<String> bindedMacs = new ArrayList<String>();
		List<UserDevice> bindedDevices = userDeviceService.fetchBindDevicesWithLimit(uid, 100);
		if(bindedDevices!= null && !bindedDevices.isEmpty()){
			for(UserDevice device:bindedDevices){
				bindedMacs.add(device.getMac());
			}
		}
		if(!bindedMacs.isEmpty()){
			WifiDeviceMobilePresentStringService.getInstance().destoryMobilePresent(bindedMacs);
		}
		userDeviceService.clearBindedDevices(uid);
		userMobileDeviceService.deleteById(uid);
		userMobileDeviceStateService.deleteById(uid);
		return true;
	}
	
	public boolean clearUsersMarkByMobileno(int countrycode,String mobileno){
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,mobileno);
		if(uid == null){
			return false;
		}
		return clearUsersMarkByUid(uid.intValue());
	}
}
