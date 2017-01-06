package com.bhu.vas.rpc.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.tag.vto.GroupUsersStatisticsVTO;
import com.bhu.vas.api.rpc.user.dto.UserConfigsStateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserActivity;
import com.bhu.vas.api.rpc.user.model.UserConfigsState;
import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.api.vto.agent.UserActivityVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetGroupPresentHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserSignInOrOnFacadeService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserActivityService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserConfigsStateService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.bhu.vas.validate.UserTypeValidateService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;
	@Resource
	private UserSignInOrOnFacadeService userSignInOrOnFacadeService;
	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;

	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	
//	@Resource
//	private UserDeviceService userDeviceService;

	@Resource
	private UserMobileDeviceService userMobileDeviceService;
//	@Resource
//	private UserDeviceFacadeService userDeviceFacadeService;
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private UserActivityService userActivityService;
	@Resource
	private UserIncomeService userIncomeService;
	
	
	@Resource
	private UserConfigsStateService userConfigsStateService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;

	/**
	 * 需要兼容uidParam为空的情况
	 * @param uidParam
	 * @param token
	 * @param d_uuid
	 * @return
	 */
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token,String d_uuid) {
		//if(StringUtils.isEmpty(uidParam)) 
		//	return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UID_EMPTY);
		try{
			int uid = -1;
			if(StringUtils.isNotEmpty(uidParam)){
				uid = Integer.parseInt(uidParam);
			}
			boolean validate = IegalTokenHashService.getInstance().validateUserToken(token,uid);
			/*//token 验证正确，需要进行uuid比对
			if(!validate && uid>0 && StringUtils.isNotEmpty(d_uuid)){//验证不通过，则需要通过uuid进行比对，看是否uuid变更
				User user  = userService.getById(uid);
				if(user != null && StringUtils.isNotEmpty(user.getLastlogindevice_uuid()) && !user.getLastlogindevice_uuid().equals(d_uuid)){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UUID_VALID_SELFOTHER_HANDSET_CHANGED);
				}
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(validate?Boolean.TRUE:Boolean.FALSE);
		}catch(TokenValidateBusinessException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}catch(NumberFormatException fex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
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
	
	/**
	 * 检查nick是否注册过
	 * @param nick
	 * @return
	 */
	public RpcResponseDTO<Boolean> checkNick(String nick){
		if(UniqueFacadeService.checkNickExist(nick)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST,Boolean.FALSE);
		}else{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
	}
			
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String d_udid,String device,String remoteIp) {
		UserTokenDTO uToken = null;
		try{
			uToken = userTokenService.validateUserAccessToken(aToken);
			//System.out.println("~~~~~step4 id:"+uToken.getId()+" token:"+uToken.getAtoken());
			//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(uToken.getId(), uToken.getAtoken());
		}catch(TokenValidateBusinessException ex){
			int validateCode = ex.getValidateCode();
			System.out.println("~~~~step5 failure~~~~~~token validatecode:"+validateCode);
			/*//token 验证错误，需要进行uuid比对
			if(StringUtils.isNotEmpty(d_udid)){
				if(ex.getUid() >0 && (validateCode== ITokenService.Access_Token_NotExist || validateCode== ITokenService.Access_Token_NotMatch)){
					User user  = userService.getById(ex.getUid());
					if(user != null && StringUtils.isNotEmpty(user.getLastlogindevice_uuid()) && !user.getLastlogindevice_uuid().equals(d_udid)){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UUID_VALID_SELFOTHER_HANDSET_CHANGED);
					}
				}
			}*/
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		User user  = userService.getById(uToken.getId());
		if(user == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		
		UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserValidate(user,uToken, device, remoteIp,d_udid);
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				userExchange);//,userWifiDeviceFacadeService.fetchBindDevices(userExchange.getUser().getId()));
		
		deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	private static final String Implication_SessionCreate_Captchacode = "hello_bbs_486732";

	/**
	 * uRouter APP 登录或注册接口
	 * 验证码登录和注册都会进行token重置
	 * 此接口只支持手机号码及验证码登录
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode,
			String acc, String captcha, String device, String remoteIp,String d_uuid) {
		
		if(!Implication_SessionCreate_Captchacode.equals(captcha)){
			//step 2.生产环境下的手机号验证码验证
			if(!RuntimeConfiguration.SecretInnerTest){
				String accWithCountryCode = PhoneHelper.format(countrycode, acc);
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
					}
				}else{
					if(!BusinessRuntimeConfiguration.DefaultCaptchaCode.equals(captcha)){//和系统定义的缺省码进行匹配
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_CAPTCHA_DATA_NOTEXIST);
					}
				}
			}
		}
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		//System.out.println("1. userid:"+uid);
		User user = null;
		UserTokenDTO uToken = null;
		boolean reg = false;
		String old_uuid = null;
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
			user.setLastlogindevice_uuid(d_uuid);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(device);
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(device);
			user = this.userService.insert(user);
			UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
			}
			deliverMessageService.sendUserRegisteredActionMessage(user.getId(),acc, null, device,remoteIp);
		}else{//登录
			reg = false;
			
			user = this.userService.getById(uid);
			//System.out.println("2. user:"+user);
			if(user == null){//存在不干净的数据，需要清理数据
				cleanDirtyUserData(uid,countrycode,acc);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			
			if(StringUtils.isEmpty(user.getRegip())){
				user.setRegip(remoteIp);
			}
			old_uuid = user.getLastlogindevice_uuid();
			if(StringUtils.isNotEmpty(d_uuid)){
				user.setLastlogindevice_uuid(d_uuid);
			}
			if(!user.getLastlogindevice().equals(device)){
				user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
			}
			this.userService.update(user);
			
			uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, false);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
			}
			deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp, device);
		}
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user,
				uToken, reg,
				old_uuid,d_uuid);//,userWifiDeviceFacadeService.fetchBindDevices(user.getId()));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	
	/**
	 * 通用用户注册接口
	 * @param countrycode
	 * @param acc
	 * @param nick
	 * @param sex
	 * @param device
	 * @param regIp
	 * @param deviceuuid
	 * @param ut
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode, String acc, String nick, String pwd, 
			                                                 String captcha, String sex, String device, String regIp, 
			                                                 String deviceuuid, String ut,String org) {
		UserType userType = UserType.getBySName(ut);
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			// 內部验证码, 内部用户注册输入此验证码就直接通过
			final String innerCaptcha = "999999111111";
			if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode) &&
			   !innerCaptcha.equals(captcha)){
				ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
				}
			}
		}
		try{
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserCreate(countrycode, acc, nick, pwd, sex, device, regIp, deviceuuid, userType, org);
			deliverMessageService.sendUserRegisteredActionMessage(userExchange.getUser().getId(),acc, null, device,regIp);
			deliverMessageService.sendPortalUpdateUserChangedActionMessage(userExchange.getUser().getId(), nick, acc, userExchange.getUser().getAvatar());
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);//,userWifiDeviceFacadeService.fetchBindDevices(userExchange.getUser().getId()));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
		
	}
	
	/**
	 * 帐号密码登录，帐号指手机号或者昵称
	 * 帐号密码登录 需要重置token
	 * @param countrycode
	 * @param acc 手机号或者昵称
	 * @param pwd
	 * @param device
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd, String device,String remoteIp) {
		try{
			Integer uid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(uid == null || uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			User user = this.userService.getById(uid);
			if(user == null){//存在不干净的数据，需要清理数据
				cleanDirtyUserData(uid,countrycode,acc);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			if(!BCryptHelper.checkpw(pwd,user.getPassword())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_UNAME_OR_PWD_INVALID);
			}
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserLogin(user, device, remoteIp, null, null);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
					userExchange);//,userWifiDeviceFacadeService.fetchBindDevices(userExchange.getUser().getId()));
			deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp, device);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 运营商登录接口
	 * @param countrycode
	 * @param acc
	 * @param pwd
	 * @param device
	 * @param remoteIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> operatorLogin(int countrycode, String acc,String pwd, String device,String remoteIp) {
		try{
			Integer uid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(uid == null || uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			User user = this.userService.getById(uid);
			if(user == null){//存在不干净的数据，需要清理数据
				cleanDirtyUserData(uid,countrycode,acc);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			if(user.getUtype() != UserType.DistributorNormal.getIndex()&& user.getUtype() != UserType.URBANOPERATORS.getIndex() && uid != 2){//检查是否是运营商帐号或总帐号
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_NOT_OPERATOR);
			}
			if(!BCryptHelper.checkpw(pwd,user.getPassword())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_UNAME_OR_PWD_INVALID);
			}
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserLogin(user, device, remoteIp, null, null);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
					userExchange);
//			deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp, device);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	
	/**
	 * 根据验证码进行密码重置操作
	 * 此操作需要进行token重置
	 * @param countrycode
	 * @param acc
	 * @param pwd
	 * @param device
	 * @param resetIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userResetPwd(int countrycode, String acc,
			String pwd, String device,String resetIp, String captcha) {
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
				ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
				}
			}
		}
		try{
			Integer uid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(uid == null || uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			User user = this.userService.getById(uid);
			if(user == null){//存在不干净的数据，需要清理数据
				cleanDirtyUserData(uid,countrycode,acc);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserResetPwd(user, pwd,device, resetIp);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Map<String, Object>> userChangedPwd(int uid,String pwd,String npwd) {
		if(StringUtils.isEmpty(pwd) || StringUtils.isEmpty(npwd)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_PASSWORD_EMPTY);
		}
		try{
			User user = UserValidateServiceHelper.validateUser(uid,this.userService);
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserChangedPwd(user, pwd,npwd);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 用户升级成运营商
	 * @param org 公司名称
	 * @return
	 */

	public RpcResponseDTO<Map<String, Object>> upgradeOperator(int uid,String org,String userType) {
		try{
			User user = this.userService.getById(uid);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			//只有普通帐号可以升级
			if(user.getUtype() != UserType.Normal.getIndex() && user.getUtype() != UserType.DistributorNormal.getIndex() && user.getUtype() != UserType.URBANOPERATORS.getIndex()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_CAN_NOT_BE_UPGRADED_TO_OPERATORS);
			}
			if(userType.equals(DistributorType.Channel.getType())){
				user.setUtype(UserType.DistributorNormal.getIndex());
			}else{
				user.setUtype(UserType.URBANOPERATORS.getIndex());
			}
			if(org !=null && !org.isEmpty()){
				user.setOrg(org);
			}else{
				user.setOrg(user.getNick());
			}
			this.userService.update(user);
			
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserProfile(user);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 运营商查询用户（运营商，普通用户）基本信息
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> operatorfetchUser(Integer uid,int countrycode, String acc) {
		try{
			User user = UserValidateServiceHelper.validateUser(uid,this.userService);
			if(user.getUtype() != UserType.DistributorNormal.getIndex() &&user.getUtype() != UserType.URBANOPERATORS.getIndex() && uid != UserInnerExchangeDTO.opsAdminUid){//检查是否是运营商帐号或总帐号
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_NOT_OPERATOR);
			}
			Integer observedId = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(observedId == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_MOBILE_NOT_BE_REGISTER);
			}
			User observedUser = UserValidateServiceHelper.validateUser(observedId,this.userService);
			if(uid != UserInnerExchangeDTO.opsAdminUid){
				if(observedUser.getUtype() != UserType.DistributorNormal.getIndex() && observedUser.getUtype() != UserType.Normal.getIndex()){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_CAN_NOT_BE_VIEWED);
				}
			}
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserProfile(observedUser);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 更新用户信息接口
	 * @param nick 需要进行唯一性验证
	 * @return
	 */

	public RpcResponseDTO<Map<String, Object>> updateProfile(int uid,String nick, String avatar, String sex, String birthday,String org,String memo) {
		try{
			User user = this.userService.getById(uid);
			System.out.println("2. user:"+user);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			if(StringUtils.isNotEmpty(avatar)){
				user.setAvatar(avatar);
			}
			if(StringUtils.isNotEmpty(sex)){
				user.setSex(sex);
			}
			if(StringUtils.isNotEmpty(birthday)){
				user.setBirthday(birthday);
			}	
			
			if(StringUtils.isNotEmpty(org)){
				user.setOrg(org);
			}	
			if(StringUtils.isNotEmpty(memo)){
				user.setMemo(memo);
			}
			
			boolean isNickUpdated = false;
			String oldNick = user.getNick();
			if(StringUtils.isNotEmpty(nick)){
				if(!nick.equals(oldNick)){
					//判定nick是否已经存在
					if(UniqueFacadeService.checkNickExist(nick)){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
					}else{
						user.setNick(nick);
						isNickUpdated = true;
					}
				}
			}
			this.userService.update(user);
			if(isNickUpdated){
				UniqueFacadeService.uniqueNickChanged(user.getId(), nick,oldNick);
				deliverMessageService.sendPortalUpdateUserChangedActionMessage(uid, nick, user.getMobileno(), avatar);
			}
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserProfile(user);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 此接口返回
	 * 	用户信息
	 *  wallet
	 *  oauth列表
	 *  
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> profile(int uid) {
		try{
			User user = UserValidateServiceHelper.validateUser(uid,this.userService);
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserProfile(user);
			//UserWallet uwallet = userWalletFacadeService.userWallet(user.getId());
			//userExchange.setWallet(userWalletFacadeService.walletDetail(uid));
			//userExchange.setOauths(userOAuthFacadeService.fetchRegisterIdentifies(userExchange.getUser().getId(),false));
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			//add by fengshibo for push switch
			UserConfigsState userConfigsState = userConfigsStateService.getById(userExchange.getUser().getId());
			if (userConfigsState != null){
				UserConfigsStateDTO dto = JsonHelper.getDTO(userConfigsState.getExtension_content(), UserConfigsStateDTO.class);
				if (dto!= null) {
					rpcPayload.put("rn_on", dto.isRn_on());
				}
			}else{
				rpcPayload.put("rn_on", Boolean.TRUE);
			}
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 通过手机号或nick进行判定 返回简单用户数据
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> fetchUser(int countrycode, String acc) {
		try{
			Integer uid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(uid == null || uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			User user = UserValidateServiceHelper.validateUser(uid,this.userService);
			UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserProfile(user);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(userExchange);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 如果数据库中不存在用户id数据
	 * 1、清理redis中unique数据
	 * 2、清理用户绑定设备数据
	 * @param uid
	 * @param countrycode
	 * @param mobileno
	 */
	private void cleanDirtyUserData(int uid,int countrycode,String mobileno){
		UniqueFacadeService.removeByMobileno(countrycode, mobileno);
		//this.userDeviceService.clearBindedDevices(uid);
		userWifiDeviceFacadeService.clearUserWifiDevices(uid);
		System.out.println(String.format("acc[%s] 记录从redis被移除！", mobileno));
	}

	/**
	 * 对于oauth注册的用户提供手机号码认证绑定的过程
	 * 1、如果手机号码已经存在则提示错误码
	 * 2、如果此账户已经有绑定手机号，则抛出错误码
	 * 3、需要验证码验证
	 * 4、成功后，此手机号可以进行登录
	 * 
	 * @param countrycode
	 * @param acc
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Boolean> authentication(int uid,int countrycode, String acc,String captcha) {
		try{
			if(uid <= 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}
			Integer userid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
			if(userid != null){
				if(userid.intValue() == uid){
					return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
				}else{
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_MOBILE_ALREADY_BEUSED,new String[]{acc});
				}
			}
			//未被使用的手机号
			User user = this.userService.getById(uid);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}
			if(acc.equals(user.getMobileno())){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
			}
			if(StringUtils.isNotEmpty(user.getMobileno())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
			}
			if(!RuntimeConfiguration.SecretInnerTest){
				String accWithCountryCode = PhoneHelper.format(countrycode, acc);
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
					}
				}
			}
			//int oldcc = user.getCountrycode();
			String oldModileno = user.getMobileno();
			user.setCountrycode(countrycode);
			user.setMobileno(acc);
			user = this.userService.update(user);
			UniqueFacadeService.uniqueMobilenoChanged(user.getId(), user.getCountrycode(), user.getMobileno(),oldModileno);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	/**
	 * 用户bbs登录 通过发送push消息通知app
	 * 安卓设备推送静默发送
	 * ios设备推送通知发送
	 * @param countrycode
	 * @param acc
	 * @param secretkey
	 * @return
	 */
	public RpcResponseDTO<Boolean> userBBSsignedon(int countrycode, String acc, String secretkey) {
		//step 2.生产环境下的手机号验证码验证
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		if(uid == null || uid.intValue() == 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		
		UserMobileDevice entity = userMobileDeviceService.getById(uid);
		if(entity == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_MOBILE_TOKEN_NOT_EXIST);
		}

		deliverMessageService.sendUserBBSsignedonMessage(uid, entity.getDt(), entity.getD(), countrycode, acc, secretkey);
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	
	/**
	 * 用户列表
	 * @param uid
	 * @param keywords
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserDTO>> pageUsers(int uid,UserType ut,int pageno,int pagesize){
		try{
			//UserTypeValidateService.validConsoleUser(uid);
			User user  = userService.getById(uid);
			UserTypeValidateService.validConsoleUser(user);
			ModelCriteria mc = new ModelCriteria();
			Criteria cri = mc.createCriteria();
			if(ut != null)
				cri.andColumnEqualTo("utype", ut.getIndex());
			/*if(keywords!=null && StringUtils.isNotEmpty(keywords.trim())){
				cri.andColumnLike("org", "%"+keywords+"%");
			}*/
			cri.andSimpleCaulse(" 1=1 ");
			mc.setOrderByClause(" id desc ");
			mc.setPageNumber(pageno);
			mc.setPageSize(pagesize);
			TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);
			List<UserDTO> vtos = new ArrayList<>();
			for(User _user:tailusers.getItems()){
				vtos.add(RpcResponseDTOBuilder.builderUserDTOFromUser(_user, false));
			}
			TailPage<UserDTO> pages = new CommonPage<UserDTO>(tailusers.getPageNumber(), pagesize, tailusers.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}

	}
	
	/**
	 * 根据条件查询用户列表
	 * @param map
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserManageDTO>> pageUserQueryList(Map<String,Object> map){
		try {
			ModelCriteria mc = new ModelCriteria();
			Criteria cri = mc.createCriteria();
			//电话
			String mobileNo = StringUtils.EMPTY;
			mobileNo = (String) map.get("mobileNo");
			//用户类型
			String userType = StringUtils.EMPTY;
			userType = (String) map.get("userType");
			//终端类型
			String regdevice = StringUtils.EMPTY;
			regdevice = (String) map.get("regdevice");
			//开始时间
			String createStartTime = StringUtils.EMPTY;
			createStartTime = (String) map.get("createStartTime");
			//结束时间
			String createEndTime = StringUtils.EMPTY;
			createEndTime = (String) map.get("createEndTime");
			System.out.println("****createEndTime【"+createEndTime+"】****");
			
			// 绑定设备数
			int boundEquNum = NumberUtils.toInt(map.get("boundEquNum") + "");
			
			// 绑定设备数匹配符号
			String boundEquNumPattern = StringUtils.EMPTY;
			boundEquNumPattern = (String) map.get("boundEquNumPattern");
			
			//是否返现
			//String isCashBack = StringUtils.EMPTY;
			//当前页
			int pageNo = 1;
			pageNo =  (int) map.get("pageNo");
			//每页分页条数
			int pageSize = 10;
			pageSize = (int) map.get("pageSize");
			if(StringUtils.isNotBlank(mobileNo)){
				cri.andColumnEqualTo("mobileno", mobileNo);
			}
			if(StringUtils.isNotBlank(userType)){
				if(StringUtils.equals(userType, "other")){
					List<Integer> utypeList = new ArrayList<Integer>();
					utypeList.add(1);
					utypeList.add(43);
					cri.andColumnNotIn("utype", utypeList);
				}else{
					UserType ut = UserType.getBySName(userType);
					cri.andColumnEqualTo("utype", ut.getIndex());
				}
			}
			if(StringUtils.isNotBlank(regdevice)){
				cri.andColumnIn("regdevice", Arrays.asList(regdevice));
			}
			if(StringUtils.isNotBlank(createStartTime) && StringUtils.isNotBlank(createEndTime)){
				createStartTime = createStartTime+" 00:00:00";
				createEndTime = createEndTime+" 23:59:59";
				cri.andColumnBetween("created_at", createStartTime, createEndTime);
			}
			
			cri.andSimpleCaulse(" 1=1 ");
			mc.setOrderByClause(" id desc ");
			mc.setPageNumber(pageNo);
			mc.setPageSize(pageSize);
			TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);
			List<UserManageDTO> vtos = new ArrayList<UserManageDTO>();
			UserManageDTO userManageDTO = null;
			
			for(User _user:tailusers.getItems()){
				if(_user == null){
					continue;
				}
				
				//根据用户Id查询设备离线数量
				long deviceNum = wifiDeviceDataSearchService.searchCountByCommon(_user.getId(), "", "", "", OnlineEnum.Offline.getType(), "","");
				//根据用户Id查询在线设备数量
				long onLinedeviceNum = wifiDeviceDataSearchService.searchCountByCommon(_user.getId(), "", "", "", OnlineEnum.Online.getType(), "","");
				long deviceCount = onLinedeviceNum + deviceNum;
				System.out.println("***boundEquNum****" + boundEquNum);
				System.out.println("***deviceCount****" + deviceCount);
				if ("gt".equalsIgnoreCase(boundEquNumPattern)) {
					System.out.println("***大于号(gt)****" + (deviceCount < boundEquNum));
					if (deviceCount < boundEquNum) {
						continue;
					}
				} else if ("lt".equalsIgnoreCase(boundEquNumPattern)){
					System.out.println("***小于号(lt)****" + (deviceCount > boundEquNum));
					if (deviceCount > boundEquNum) {
						continue;
					}	
				}			
				
				userManageDTO = new UserManageDTO();
				userManageDTO.setUid(_user.getId());
				userManageDTO.setUserType(String.valueOf(_user.getUtype()));
				userManageDTO.setMobileNo(_user.getMobileno());
				userManageDTO.setNickName(_user.getNick());
				userManageDTO.setSex(_user.getSex());
				userManageDTO.setSignature(_user.getMemo());
				userManageDTO.setRegdevice(_user.getRegdevice());
				userManageDTO.setUserLabel("");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		        if(_user.getCreated_at() != null){
		        	String time =sdf.format(_user.getCreated_at()); 
					userManageDTO.setCreateTime(time);
		        }
				userManageDTO.setRewardStyle("");
				userManageDTO.setIsCashBack("");
				userManageDTO.setUserNum(tailusers.getTotalItemsCount());
				//根据uid查询用户钱包信息
				UserWalletDetailVTO userWallet = userWalletFacadeService.walletSimpleDetail(_user.getId());
				if(userWallet != null){
					userManageDTO.setVcurrency(userWallet.getVcurrency());
					userManageDTO.setWalletMoney(userWallet.getCash());
				}else{
					userManageDTO.setVcurrency(0);
					userManageDTO.setWalletMoney(0.00);
				}
				userManageDTO.setDc(deviceCount);
				userManageDTO.setDoc(onLinedeviceNum);
				vtos.add(userManageDTO);
			}
			
			TailPage<UserManageDTO> pages = new CommonPage<UserManageDTO>(tailusers.getPageNumber(), pageSize, tailusers.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
	public RpcResponseDTO<UserActivityVTO> activity(Integer uid) {
		try{
			UserActivityVTO userActivityVTO=new UserActivityVTO();
			UserActivity userActivity=userActivityService.getById(uid);
			userActivityVTO.setBind_num(userActivity.getBind_num());
			userActivityVTO.setIncome(userActivity.getIncome());
			userActivityVTO.setRate(userActivity.getRate());
			userActivityVTO.setStatus(userActivity.getStatus());
			Date date = new Date();  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(date);  
	        calendar.add(Calendar.DAY_OF_MONTH, -1);  
	        date = calendar.getTime();  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String time = sdf.format(date); 
			List<UserIncome> userIncome=userIncomeService.findListByUid(uid, time);
			if(userIncome!=null&&userIncome.size()>0){
				userActivityVTO.setUserIncome(Double.valueOf(userIncome.get(0).getIncome()));
			}else{
				userActivityVTO.setUserIncome(0);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userActivityVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 查询用户详细信息
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<UserManageDTO> queryUserDetail(int uid){
		try {
			User user = this.userService.getById(uid);
			//UserTypeValidateService.validConsoleUser(user);
			UserManageDTO userManageDTO = new UserManageDTO();
			userManageDTO.setUid(user.getId());
			userManageDTO.setUserType(String.valueOf(user.getUtype()));
			userManageDTO.setMobileNo(user.getMobileno());
			userManageDTO.setRegdevice(user.getRegdevice());
			userManageDTO.setUserLabel("");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	        if(user.getCreated_at() != null){
	        	String time =sdf.format(user.getCreated_at()); 
				userManageDTO.setCreateTime(time);
	        }
			//userManageDTO.setCreateTime(user.getCreated_at().toString());
			userManageDTO.setRewardStyle("");
			userManageDTO.setIsCashBack("");
			userManageDTO.setUserNum(1);
			userManageDTO.setNickName(user.getNick());
			userManageDTO.setSex(user.getSex());
			userManageDTO.setSignature(user.getMemo());
			//根据uid查询用户钱包信息
			UserWalletDetailVTO userWallet = userWalletFacadeService.walletSimpleDetail(user.getId());
			if(userWallet != null){
				userManageDTO.setVcurrency(userWallet.getVcurrency());
				userManageDTO.setWalletMoney(userWallet.getCash());
			}else{
				userManageDTO.setVcurrency(0);
				userManageDTO.setWalletMoney(0.00);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userManageDTO);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
	public RpcResponseDTO<Boolean> activitySet(Integer uid, Integer bind_num,
			Double income, String rate,Integer status) {
		UserActivity userActivity=new UserActivity();
		userActivity.setBind_num(bind_num);
		userActivity.setId(uid);
		userActivity.setIncome(income);
		userActivity.setRate(rate);
		userActivity.setStatus(status);
		UserActivity activity=userActivityService.getById(uid);
		if(activity!=null){
			userActivityService.update(userActivity);
		}else{
			userActivityService.insert(userActivity);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 用户连接情况
	 * @param gid
	 * @param timeStr
	 * @return
	 */
	public RpcResponseDTO<GroupUsersStatisticsVTO> UsersStatistics(int uid, long time) {
		
		try {
			String timeStr = DateTimeHelper.formatDate(new Date(time), DateTimeHelper.FormatPattern5);
			
			GroupUsersStatisticsVTO vto = new GroupUsersStatisticsVTO();
			Date date = DateTimeHelper.fromDateStr(timeStr);
			Date dateDaysAgo = DateTimeHelper.getDateDaysAgo(date, 1);
			String today = DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern7);
			String yesterday = DateTimeHelper.formatDate(dateDaysAgo, DateTimeHelper.FormatPattern7);
			Map<String, String> todayMap = HandsetGroupPresentHashService.getInstance().fetchUserConnDetail(uid, today);
			Map<String, String> yesterdayMap = HandsetGroupPresentHashService.getInstance().fetchUserConnDetail(uid, yesterday);
			vto.setToday_newly(todayMap.get("newly"));
			vto.setToday_total(todayMap.get("total"));
			vto.setYesterday_newly(yesterdayMap.get("newly"));
			vto.setYesterday_total(yesterdayMap.get("total"));
			vto.setCount(HandsetGroupPresentHashService.getInstance().fetchUserConnTotal(uid));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public static void main(String[] args) {
//		Date date = new Date();  
//        Calendar calendar = Calendar.getInstance();  
//        calendar.setTime(date);  
//        calendar.add(Calendar.DAY_OF_MONTH, 0);  
//        date = calendar.getTime();  
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
//        String time =sdf.format(date); 
//        System.out.println(time);
//        System.out.println(date.toString());
		  
		  String regdevice = "R,O";
		  System.out.println(Arrays.asList(regdevice));
	}
}
