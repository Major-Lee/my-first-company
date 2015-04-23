package com.bhu.vas.rpc.facade;

import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.business.ds.user.service.UserDeviceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	@Resource
	private DeliverMessageService deliverMessageService;
	@Resource
	private UserDeviceService userDeviceService;

	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token) {
		boolean validate = IegalTokenHashService.getInstance().validateUserToken(token,uidParam);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(validate?Boolean.TRUE:Boolean.FALSE);
	}
	/**
	 * 检查手机号是否注册过
	 * @param countrycode
	 * @param acc
	 * @return 
	 * 		true 系统不存在此手机号  
	 * 		false系统存在此手机号 ，并带有错误码
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc){
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST,Boolean.FALSE);
		}else{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
	}
			
/*	public RpcResponseDTO<UserDTO> createNewUser(int countrycode, String acc,
			String nick, String sex, String device,String regIp,String deviceuuid, String captcha) {
		
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){//userService.isPermalinkExist(permalink)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
			//return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
		}
		
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
				ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
				}
			}
		}
		User user = new User();
		user.setCountrycode(countrycode);
		user.setMobileno(acc);
		//user.addSafety(SafetyBitMarkHelper.mobileno);
		//user.setPlainpwd(RuntimeConfiguration.Default_Whisper_Pwd);
		user.setNick(nick);
		user.setSex(sex);
		user.setLastlogindevice_uuid(deviceuuid);
		user.setRegip(regIp);
		//标记用户注册时使用的设备，缺省为DeviceEnum.Android
		user.setRegdevice(device);
		//标记用户最后登录设备，缺省为DeviceEnum.PC
		user.setLastlogindevice(device);
		user = this.userService.insert(user);
		UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
		
		// token validate code
		UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
		}
		deliverMessageService.sendUserRegisteredActionMessage(user.getId(), null, device,regIp);
		UserDTO payload = new UserDTO();
		payload.setId(user.getId());
		payload.setCountrycode(countrycode);
		payload.setMobileno(acc);
		payload.setNick(nick);
		payload.setAtoken(uToken.getAccess_token());
		payload.setRtoken(uToken.getRefresh_token());
		payload.setReg(true);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
	}
	
	public RpcResponseDTO<UserDTO> userLogin(int countrycode, String acc,String device,String remoteIp,String captcha) {
		//step 2.生产环境下的手机号验证码验证
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
				ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
				}
			}
		}
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		System.out.println("1. userid:"+uid);
		if(uid == null || uid.intValue() == 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		User user = this.userService.getById(uid);
		System.out.println("2. user:"+user);
		if(user == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		
		UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, false);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
		}
		deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		
		UserDTO payload = new UserDTO();
		payload.setId(user.getId());
		payload.setCountrycode(countrycode);
		payload.setMobileno(acc);
		payload.setNick(user.getNick());
		payload.setAtoken(uToken.getAccess_token());
		payload.setRtoken(uToken.getRefresh_token());
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		//Map<String,Object> map = userLoginDataService.buildLoginData(user);
        //SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
	}*/
	
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String device,String remoteIp) {
		UserToken uToken = null;
		try{
			uToken = userTokenService.validateUserAccessToken(aToken);
			System.out.println("~~~~~step4 id:"+uToken.getId()+" token:"+uToken.getAccess_token());
			//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(uToken.getId().intValue(), uToken.getAccess_token());
		}catch(TokenValidateBusinessException ex){
			int validateCode = ex.getValidateCode();
			System.out.println("~~~~step5 failure~~~~~~token validatecode:"+validateCode);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
			//return;
		}
		
		User user  = userService.getById(uToken.getId());
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), 
				uToken.getAccess_token(), uToken.getRefresh_token(), false,
				userDeviceService.fetchBindDevicesWithLimit(user.getId(), 3));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		/*UserDTO payload = new UserDTO();
		payload.setId(user.getId());
		payload.setCountrycode(user.getCountrycode());
		payload.setMobileno(user.getMobileno());
		payload.setNick(user.getNick());
		payload.setAtoken(uToken.getAccess_token());
		payload.setRtoken(uToken.getRefresh_token());
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);*/
	}
	
	/**
	 * 登录或注册接口
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode,
			String acc, String device, String remoteIp, String captcha) {
		//step 2.生产环境下的手机号验证码验证
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
				ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
				}
			}
		}
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		System.out.println("1. userid:"+uid);
		User user = null;
		UserToken uToken = null;
		boolean reg = false;
		if(uid == null || uid.intValue() == 0){//注册
			//return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			reg = true;
			user = new User();
			user.setCountrycode(countrycode);
			user.setMobileno(acc);
			//user.addSafety(SafetyBitMarkHelper.mobileno);
			//user.setPlainpwd(RuntimeConfiguration.Default_Whisper_Pwd);
			//user.setNick(nick);
			//user.setSex(sex);
			//user.setLastlogindevice_uuid(deviceuuid);
			user.setRegip(remoteIp);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(device);
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(device);
			user = this.userService.insert(user);
			UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			deliverMessageService.sendUserRegisteredActionMessage(user.getId(), null, device,remoteIp);
		}else{//登录
			reg = false;
			user = this.userService.getById(uid);
			System.out.println("2. user:"+user);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			if(StringUtils.isEmpty(user.getRegip())){
				user.setRegip(remoteIp);
			}
			if(!user.getLastlogindevice().equals(device)){
				user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
			}
			this.userService.update(user);
			
			uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, false);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		}
		
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(user.getId(), countrycode, acc, user.getNick(), 
				uToken.getAccess_token(), uToken.getRefresh_token(), reg,
				userDeviceService.fetchBindDevicesWithLimit(uid,3));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		/*UserDTO payload = new UserDTO();
		payload.setId(user.getId());
		payload.setCountrycode(countrycode);
		payload.setMobileno(acc);
		payload.setNick(user.getNick());
		payload.setAtoken(uToken.getAccess_token());
		payload.setRtoken(uToken.getRefresh_token());
		payload.setReg(reg);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);*/
	}
}
