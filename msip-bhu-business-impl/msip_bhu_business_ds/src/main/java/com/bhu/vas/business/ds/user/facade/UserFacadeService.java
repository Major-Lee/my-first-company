package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceStateService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

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

	//@Resource
	//private UserOAuthStateService userOAuthStateService;
	
	public boolean clearUsersMarkByUid(int uid){
		User byId = userService.getById(uid);
		if(byId != null){
			userService.deleteById(uid);
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
		if(uid == null || uid.intValue() == 0){
			return false;
		}
		return clearUsersMarkByUid(uid.intValue());
	}
	
	public User getUserByMobileno(String mobileno){
		return getUserByMobileno(null, mobileno);
	}
	
	public User getUserByMobileno(Integer countrycode, String mobileno){
		if(StringUtils.isEmpty(mobileno)) return null;
		if(countrycode == null) countrycode = 86;
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
				.andColumnEqualTo("countrycode", countrycode)
				.andColumnEqualTo("mobileno", mobileno);
		List<User> result = userService.findModelByModelCriteria(mc);
		if(result == null || result.isEmpty()) return null;
		return result.get(0);
	}
	
/*	public UserSnsStateDTO updateUserSnsInfo(int uid, ApplicationIdentify identify, GeneralOAuth2AccessToken generalOAuth2AccessToken) throws Exception{
		UserSnsStatePK pk = new UserSnsStatePK(uid,identify.toString());
		UserSnsState model = userSnsStateService.getById(pk);
		System.out.println("++++++++++++++ updateUserSnsInfo :" + model);
		if(model != null){
			System.out.println("++++++++++++++ getJUser :" + generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			UserSnsFriendDTO snsUserDto = applicationSupport.getJUser(identify, generalOAuth2AccessToken, generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			System.out.println("++++++++++++++ getJUser :" + snsUserDto);
			if(snsUserDto != null){
				UserSnsStateDTO dto = new UserSnsStateDTO();
				dto.setAuid(snsUserDto.getAuid());
				dto.setNick(snsUserDto.getNick());
				dto.setAvatar(snsUserDto.getAvatar());
				dto.setIdentify(identify.toString());
				model.putInnerModel(dto);
				userSnsStateService.update(model);
				return dto;
			}
		}
		return null;
	}*/
}
