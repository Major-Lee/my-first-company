package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
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
	@Resource
	private WifiDeviceService wifiDeviceService;
	@Resource
	private UserMobileDeviceService userMobileDeviceService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;


	public final static int WIFI_DEVICE_BIND_LIMIT_NUM = 10;

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
			
	public RpcResponseDTO<Map<String, Object>> userConsoleLogin(int countrycode, String acc,String pwd,String device,String remoteIp) {
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
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
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
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
		//deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		/*Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderSimpleUserRpcPayload(
				user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), user.getUtype(),
				uToken.getAtoken(), uToken.getRtoken(), false);*/
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderSimpleUserRpcPayload(
				user,
				uToken, false);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		/*UserDTO payload = new UserDTO();
		payload.setId(user.getId());
		payload.setCountrycode(countrycode);
		payload.setMobileno(acc);
		payload.setNick(user.getNick());
		payload.setAtoken(uToken.getAccess_token());
		payload.setRtoken(uToken.getRefresh_token());
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);*/
		//Map<String,Object> map = userLoginDataService.buildLoginData(user);
        //SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
	}
	
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String device,String remoteIp) {
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
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
			//return;
		}
		
		User user  = userService.getById(uToken.getId());
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
		deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		/*Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), user.getUtype(),
				uToken.getAtoken(), uToken.getRtoken(), false,
				fetchBindDevices(user.getId()));*/
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user,
				uToken, false,
				fetchBindDevices(user.getId()));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	/**
	 * uRouter APP 登录或注册接口
	 * 验证码登录和注册都会进行token重置
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
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		//System.out.println("1. userid:"+uid);
		User user = null;
		UserTokenDTO uToken = null;
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
				uToken, reg,fetchBindDevices(user.getId()));
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
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode, String acc,
			String nick,String pwd, String sex, String device,String regIp,String deviceuuid, String ut,String org, String captcha) {
		UserType userType = UserType.getBySName(ut);
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){//userService.isPermalinkExist(permalink)){
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
		UserTokenDTO uToken = null;
		User user = new User();
		user.setCountrycode(countrycode);
		user.setMobileno(acc);
		if(StringUtils.isNotEmpty(pwd)){
			user.setPlainpwd(pwd);
		}
		if(StringUtils.isNotEmpty(nick)){
			//判定nick是否已经存在
			if(UniqueFacadeService.checkNickExist(nick)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
			}else{
				user.setNick(nick);
			}
		}
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
		deliverMessageService.sendUserRegisteredActionMessage(user.getId(),acc, null, device,regIp);
		
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user,
				uToken, true,fetchBindDevices(user.getId()));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	/**
	 * 帐号密码登录，帐号指手机号或者昵称
	 * 帐号密码登录 需要重置token
	 * @param countrycode
	 * @param acc 手机号或者昵称
	 * @param nick
	 * @param pwd
	 * @param device
	 * @param regIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd, String device,String remoteIp) {
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
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
		}
		deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp, device);
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderSimpleUserRpcPayload(
				user,
				uToken, false);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
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
		UserTokenDTO uToken = null;
		
		Integer uid = UniqueFacadeService.fetchUidByAcc(countrycode,acc);
		if(uid == null || uid.intValue() == 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		User user = this.userService.getById(uid);
		if(user == null){//存在不干净的数据，需要清理数据
			cleanDirtyUserData(uid,countrycode,acc);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		user.setCountrycode(countrycode);
		user.setMobileno(acc);
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
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderSimpleUserRpcPayload(
				user,
				uToken, false);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	
	/**
	 * 更新用户信息接口
	 * @param countrycode
	 * @param nick 需要进行唯一性验证
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> updateProfile(int uid,String nick, String avatar, String sex, String birthday,String org) {
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
		}
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user,
				null, false,null);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	
	public RpcResponseDTO<Map<String, Object>> profile(int uid) {
		User user = this.userService.getById(uid);
		if(user == null){//存在不干净的数据，需要清理数据
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
				user,
				null, false,null);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
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
		this.userDeviceService.clearBindedDevices(uid);
		System.out.println(String.format("acc[%s] 记录从redis被移除！", mobileno));
	}


	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线
	 * @param uid
	 * @return
	 */
	public List<UserDeviceDTO>  fetchBindDevices(int uid) {
		List<UserDevice> userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		List<UserDeviceDTO> bindDevicesDTO = new ArrayList<UserDeviceDTO>();
		if(userDeviceList != null && !userDeviceList.isEmpty()){
			for (UserDevice userDevice : userDeviceList) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(userDevice.getMac());
				userDeviceDTO.setUid(userDevice.getUid());
				userDeviceDTO.setDevice_name(userDevice.getDevice_name());
				WifiDevice wifiDevice = wifiDeviceService.getById(userDevice.getMac());
				if (wifiDevice != null) {
					userDeviceDTO.setOnline(wifiDevice.isOnline());
					if (wifiDevice.isOnline()) { //防止有些设备已经离线了，没有更新到后台
						userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
								.presentOnlineSize(userDevice.getMac()));
					}
					userDeviceDTO.setVer(wifiDevice.getOrig_swver());
				}
				bindDevicesDTO.add(userDeviceDTO);
			}
		}
		return bindDevicesDTO;
	}


	/**
	 * 通过搜索引擎获取用户绑定的设备
	 * @param uid
	 * @param dut
	 * @param pageNo
	 * @param pageSize
	 * @return
     */
	public TailPage<UserDeviceDTO> fetchBindDevicesFromIndex(int uid, String dut, int pageNo, int pageSize) {
		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageByUidAndDut(uid, dut, pageNo, pageSize);

		List<UserDeviceDTO> vtos = new ArrayList<UserDeviceDTO>();
		int total = (int)search_result.getTotalElements();//.getTotal();
		if(total == 0){
			vtos = Collections.emptyList();
		}else{
			List<WifiDeviceDocument> searchDocuments = search_result.getContent();//.getResult();
			if(searchDocuments.isEmpty()) {
				vtos = Collections.emptyList();
			}else{
				vtos = new ArrayList<UserDeviceDTO>();
				WifiDeviceVTO1 vto = null;
				int startIndex = PageHelper.getStartIndexOfPage(pageNo, pageSize);
				for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
					UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
					userDeviceDTO.setMac(wifiDeviceDocument.getD_mac());
					userDeviceDTO.setUid(uid);
					userDeviceDTO.setDevice_name(wifiDeviceDocument.getU_dnick());
					WifiDevice wifiDevice = wifiDeviceService.getById(wifiDeviceDocument.getD_mac());
					if (wifiDevice != null) {
						userDeviceDTO.setOnline(wifiDevice.isOnline());
						if (wifiDevice.isOnline()) { //防止有些设备已经离线了，没有更新到后台
							userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
									.presentOnlineSize(wifiDeviceDocument.getD_mac()));
						}
						userDeviceDTO.setVer(wifiDevice.getOrig_swver());
					}
					vtos.add(userDeviceDTO);
				}
			}
		}

		TailPage<UserDeviceDTO> returnRet = new CommonPage<UserDeviceDTO>(pageNo, pageSize, total, vtos);

		return returnRet;
	}



	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<List<UserDeviceDTO>>  fetchBindDevicesByAccOrUid(int countrycode,String acc,int uid) {
		if(uid <=0 && StringUtils.isEmpty(acc))
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		if(uid <=0){
			Integer ret_uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(ret_uid == null || ret_uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			uid = ret_uid.intValue();
		}
		List<UserDeviceDTO> fetchBindDevices = fetchBindDevices(uid);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(fetchBindDevices);
	}
	
	
	
	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean>  unBindDevicesByAccOrUid(int countrycode,String acc,int uid) {
		if(uid <=0 && StringUtils.isEmpty(acc))
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		if(uid <=0){
			Integer ret_uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(ret_uid == null || ret_uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			uid = ret_uid.intValue();
		}
		List<UserDevice> userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		for(UserDevice ud:userDeviceList){
			UserDevicePK userDevicePK = ud.getId();
	        if (userDeviceService.deleteById(userDevicePK) > 0)  {
	        	deliverMessageService.sendUserDeviceDestoryActionMessage(uid, userDevicePK.getMac());
	        }
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
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
}
