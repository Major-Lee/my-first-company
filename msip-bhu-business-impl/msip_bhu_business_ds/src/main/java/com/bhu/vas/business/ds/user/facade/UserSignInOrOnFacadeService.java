package com.bhu.vas.business.ds.user.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceStateService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserSignInOrOnFacadeService {
	@Resource
	private UserService userService;
	
	@Resource
	private UserTokenService userTokenService;
	
	/*	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private UserMobileDeviceService userMobileDeviceService;*/
	
	@Resource
	private UserMobileDeviceStateService userMobileDeviceStateService;
	public UserInnerExchangeDTO commonOAuthUserCreate(String nick,String device,String regIp,String deviceuuid, UserType userType){
		return commonUserCreate(0,null,nick,null,null,device,regIp,deviceuuid,userType,null);
	}
	
	/**
	 * 通用用户登录相关数据提取
	 * @param uid
	 * @param device
	 * @param regIp
	 * @param deviceuuid
	 * @param userType
	 * @return
	 */
	public UserInnerExchangeDTO commonUserLogin(User user,String device,String regIp,String deviceuuid, UserType userType){
		//User user = this.userService.getById(uid);
		if(user == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(regIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, false);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
		}
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false), uToken);
	}
	
	public UserInnerExchangeDTO commonUserValidate(User user,UserTokenDTO uToken,String device,String regIp,String deviceuuid){
		//User user = this.userService.getById(uid);
		if(user == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(regIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false), uToken);
	}
	
	/**
	 * 通用创建用户接口
	 * @param countrycode
	 * @param acc  可以为空
	 * @param nick 可以为空
	 * @param pwd
	 * @param sex
	 * @param device
	 * @param regIp
	 * @param deviceuuid
	 * @param userType
	 * @param org
	 * @return
	 */
	public UserInnerExchangeDTO commonUserCreate(int countrycode, String acc,
			String nick,String pwd, String sex, String device,String regIp,String deviceuuid, UserType userType,String org){
		//判定acc是否已经存在
		if(StringUtils.isNotEmpty(acc) && UniqueFacadeService.checkMobilenoExist(countrycode,acc)){
			throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
		}
		//判定nick是否已经存在
		if(StringUtils.isNotEmpty(nick) && UniqueFacadeService.checkNickExist(nick)){
			throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
		}
		UserTokenDTO uToken = null;
		User user = new User();
		user.setCountrycode(countrycode);
		user.setMobileno(acc);
		if(StringUtils.isNotEmpty(pwd)){
			user.setPlainpwd(pwd);
		}
		/*if(StringUtils.isNotEmpty(nick)){
			//判定nick是否已经存在
			if(UniqueFacadeService.checkNickExist(nick)){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
			}else{
				user.setNick(nick);
			}
		}*/
		user.setNick(nick);
		user.setSex(sex);
		user.setLastlogindevice_uuid(deviceuuid);
		user.setRegip(regIp);
		//标记用户注册时使用的设备，缺省为DeviceEnum.Android
		user.setRegdevice(device);
		//标记用户最后登录设备，缺省为DeviceEnum.PC
		user.setLastlogindevice(device);
		user.setUtype(userType.getIndex());
		user.setOrg(org);
		user = this.userService.insert(user);
		UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
		if(StringUtils.isNotEmpty(nick)){
			UniqueFacadeService.uniqueNickRegister(user.getId(), nick);
		}
		
		// token validate code
		uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
		}
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, true), uToken);
	}
	
	public UserInnerExchangeDTO commonUserProfile(User user){
		if(user == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false), null);
	}
	
	public UserInnerExchangeDTO commonUserResetPwd(User user,String pwd, String device,String resetIp){
		UserTokenDTO uToken = null;
		if(StringUtils.isNotEmpty(pwd)){
			user.setPlainpwd(pwd);
			user.setPassword(null);
		}
		user.setRegip(resetIp);
		//标记用户注册时使用的设备，缺省为DeviceEnum.Android
		user.setRegdevice(device);
		//标记用户最后登录设备，缺省为DeviceEnum.PC
		user.setLastlogindevice(device);
		user = this.userService.update(user);
		// token validate code
		uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
		}
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false), uToken);
	}
	
	public UserInnerExchangeDTO commonUserChangedPwd(User user,String pwd, String npwd){
		if(!BCryptHelper.checkpw(pwd,user.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.LOGIN_UNAME_OR_PWD_INVALID);
		}
		if(StringUtils.isNotEmpty(npwd)){
			user.setPlainpwd(npwd);
			user.setPassword(null);
		}
		user = this.userService.update(user);
		// token validate code
		UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
		}
		return UserInnerExchangeDTO.build(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false), uToken);
	}
}
