//package com.whisper.web.user;
//
//import java.util.Date;
//import java.util.Map;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.smartwork.msip.cores.plugins.filterhelper.StringHelper;
//import com.smartwork.msip.jdo.ResponseError;
//import com.smartwork.msip.jdo.ResponseErrorCode;
//import com.smartwork.msip.jdo.ResponseSuccess;
//import com.whisper.api.user.model.DeviceEnum;
//import com.whisper.api.user.model.User;
//import com.whisper.api.user.model.UserAvatarState;
//import com.whisper.api.user.model.UserToken;
//import com.whisper.business.asyn.web.builder.DeliverMessageType;
//import com.whisper.business.asyn.web.service.DeliverMessageService;
//import com.whisper.business.bucache.redis.serviceimpl.present.token.IegalTokenHashService;
//import com.whisper.business.facade.ucenter.UserAvatarStateFacadeService;
//import com.whisper.business.facade.ucenter.UserSnsTokenFacadeService;
//import com.whisper.business.helper.BusinessWebHelper;
//import com.whisper.business.service.UserLoginDataService;
//import com.whisper.business.ucenter.service.UserAvatarStateService;
//import com.whisper.business.ucenter.service.UserService;
//import com.whisper.business.ucenter.service.UserTokenService;
//import com.whisper.common.BusinessEnumType;
//import com.whisper.common.BusinessEnumType.FrdType;
//import com.whisper.msip.cores.web.mvc.WebHelper;
//import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
//import com.whisper.msip.exception.BusinessException;
//import com.whisper.msip.web.AsynMessageController;
//import com.whisper.validate.ValidateService;
///**
// * 用户 Sns Controller
// * @author lawliet
// *
// */
//@Controller
//@RequestMapping("/sns")
//public class UserSnsController extends AsynMessageController{
//
//	@Resource
//	private UserService userService;
//	
//	@Resource
//	private UserSnsTokenFacadeService userSnsTokenFacadeService;
//	
//	@Resource
//	private UserTokenService userTokenService;
//	
//	@Resource
//	private DeliverMessageService deliverMessageService;
//	
//	@Resource
//	private UserLoginDataService userLoginDataService;
//	
//	@Resource
//	private UserAvatarStateFacadeService userAvatarStateFacadeService;
//	
//	@Resource
//	private UserAvatarStateService userAvatarStateService;
//	
//	/**
//	 * 以第三方账号方式登陆或注册
//	 * @param request
//	 * @param response
//	 * @param snsType
//	 * @param name 腾讯需要传账户名, 其他可以不传
//	 * @param auid
//	 * @param access_token
//	 * @param lang
//	 * @param region
//	 * @param itoken
//	 * @param device
//	 * @param token
//	 */
//	@ResponseBody()
//	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
//	public void account_create(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true, value="du") String deviceuuid,
//			@RequestParam(required = true, value = "t") String snsType,
//			@RequestParam(required = false) String name,
//			@RequestParam(required = true) String nick,
//			@RequestParam(required = true) String auid,
//			@RequestParam(required = true) String access_token,
//			@RequestParam(required = true) String avatar,
//			@RequestParam(required = false,defaultValue="") String lang,
//			@RequestParam(required = false,defaultValue="") String region,
//			@RequestParam(required = false, value="i") String itoken,
//			@RequestParam(required = false, value="d",defaultValue="R") String device,
//			@RequestParam(required = false) String token
//			) {
//		
//		validateSupportType(snsType);
//		validateQQType4name(snsType, name);
//		
//		try{
//			ResponseError validateError = ValidateService.validateDeviceUUID(deviceuuid);//, userService);
//			if(validateError != null){
//				SpringMVCHelper.renderJson(response, validateError);
//				return;
//			}
//			
//			Integer uid = userSnsTokenFacadeService.getUserId(auid, snsType);
//			String remoteIp = null;
//			String from_device = null;
//			User user = null;
//			UserToken uToken = null;
//			//如果不存在用户id, 说明注册操作
//			if(uid == null){
//				System.out.println("**************************************sns create user register");
//				Integer inviteuid =null;
//				if(StringUtils.isNotEmpty(itoken))
//					inviteuid = this.validateUserInviteToken(itoken);
//				
//				user = new User();
//				
//				if(StringUtils.isNotEmpty(nick)){
//					validateError = ValidateService.validateNick(nick);//, userService);
//					if(validateError != null){
//						SpringMVCHelper.renderJson(response, validateError);
//						return;
//					}
//					user.setNick(nick);
//				}
//				
//				//user.setLastlogindevice_uuid(deviceuuid);
//				user.setLastlogindevice_uuid(deviceuuid);
//				user.setRegion(region);
//				user.setLang(lang);
//				//user.setType(User.TYPE_USER_Normal);
//				user.setInviteuid(inviteuid);
//				remoteIp = WebHelper.getRemoteAddr(request);
//				user.setRegip(remoteIp);
//				from_device = DeviceEnum.getBySName(device).getSname();
//				//标记用户注册时使用的设备，缺省为DeviceEnum.Android
//				user.setRegdevice(from_device);
//				//标记用户最后登录设备，缺省为DeviceEnum.PC
//				user.setLastlogindevice(from_device);
//				//没有头像,设置sns头像为当前头像
//				user.setAvatar(avatar);
//				user.setLastlogin_at(new Date());
//				user = this.userService.insert(user);
//				uid = user.getId();
//				//用户第三方账号关联数据建立
//				userSnsTokenFacadeService.addSnsToken(uid, snsType, name, auid, access_token);
//				// token validate code
//				uToken = userTokenService.generateUserAccessToken(uid, true, true);
//				//新注册用户不用进行执行时间的验证, 直接发送队列执行入驻
//				super.sendUserSnsJoinMessage(uid, snsType, auid, access_token);
//				//TODO: 新增用户数据, 需要增量索引
//			}
//			//如果存在用户id, 说明登陆操作
//			else{
//				System.out.println("**************************************sns create user login");
//				user = userService.getById(uid);
//				if(user == null){
//					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST));//renderHtml(response, html, headers)
//					return;
//				}
//				
//				remoteIp = WebHelper.getRemoteAddr(request);
//				boolean needUpdate = false;
//				if(StringUtils.isEmpty(user.getRegip())){
//					user.setRegip(remoteIp);
//					needUpdate = true;
//				}
//				
//				if(!user.getLastlogindevice().equals(device)){
//					user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
//					needUpdate = true;
//				}
//				if(StringUtils.isNotEmpty(lang)){
//					if(!lang.equals(user.getLang())){
//						user.setLang(lang);
//						needUpdate = true;
//					}
//				}
//				
//				if(needUpdate)
//					this.userService.update(user);
//				
//				//检查客户端参数传递的数据和服务端的数据如果不一样, 则更新数据
//				userSnsTokenFacadeService.validateUpdateSnsToken(uid, snsType, name, auid, access_token);
//				// token validate code
//				uToken = userTokenService.generateUserAccessToken(uid, true, false);
//				//validate sns last actionAt and send
//				super.sendUserSnsJoinMessageByLastActionAt(uid);
//			}
//			//新增或更新用户头像管理
//			userAvatarStateFacadeService.addUserAvatar(uid, snsType, avatar);
//			
//			//UniqueFacadeService.uniqueRegister(user.getId(), user.getEmail(), user.getPassword(), user.getNick(), user.getMobileno());
//			{//write header to response header
//				BusinessWebHelper.setCustomizeHeader(response, uToken);
//				IegalTokenHashService.getInstance().userTokenRegister(uid, uToken.getAccess_token());
//			}
//			//this.userSettingStateService.addDefaultSetting(user.getId());
//			deliverMessageService.sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), uid, remoteIp,from_device);
//			//validate
//			Map<String,Object> map = userLoginDataService.buildLoginData(user);
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}finally{
//			
//		}
//	}
//	
//	/**
//	 * 用户绑定第三方sns账号
//	 * 1: 关联第三方数据
//	 * 2: 用户的头像修改
//	 * 3: 发送sns入驻消息队列
//	 * @param request
//	 * @param response
//	 * @param uid
//	 * @param snsType
//	 * @param name 如果是腾讯, 必须提供name(腾讯账户名)
//	 * @param auid
//	 * @param access_token
//	 */
//	@ResponseBody()
//	@RequestMapping(value="/bind",method={RequestMethod.GET,RequestMethod.POST})
//	public void bind(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid,
//			@RequestParam(required = true, value = "t") String snsType,
//			@RequestParam(required = false) String name,
//			@RequestParam(required = true) String auid,
//			@RequestParam(required = true) String avatar,
//			@RequestParam(required = true) String access_token) {
//		
//		User user = this.userService.getById(uid);
//		validateUserNotNull(user);
//		validateSupportType(snsType);
//		validateQQType4name(snsType, name);
//		
//		try{
//			Integer existUid = userSnsTokenFacadeService.getUserId(auid, snsType);
//			if(existUid != null){
//				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.SNS_DUPLICATE_BIND));
//				return;
//			}
//			
//			userSnsTokenFacadeService.addSnsToken(uid, snsType, name, auid, access_token);
//			//新增或更新用户头像管理
//			UserAvatarState userAvatarEntity = userAvatarStateService.getOrCreateById(uid);
//			boolean added = userAvatarStateFacadeService.addUserAvatar(userAvatarEntity, snsType, avatar);
//			if(added){
//				//如果增加头像类型成功, 并且用户没有上传本地头像, 则更新当前sns头像为用户头像
//				if(StringHelper.isEmpty(userAvatarEntity.getLocalAvatarTag())){
//					user.setAvatar(avatar);
//					userService.update(user);
//				}
//			}
//			//用户绑定操作不用进行执行时间验证,直接发送消息列队
//			super.sendUserSnsJoinMessage(uid, snsType, auid, access_token);
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(snsType));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}
//	/**
//	 * 用户解除绑定第三方sns账号
//	 * @param request
//	 * @param response
//	 * @param uid
//	 * @param snsType
//	 */
//	@ResponseBody()
//	@RequestMapping(value="/unbind",method={RequestMethod.GET,RequestMethod.POST})
//	public void unbind(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid,
//			@RequestParam(required = true, value = "t") String snsType) {
//		
//		User user = this.userService.getById(uid);
//		validateUserNotNull(user);
//		validateSupportType(snsType);
//		
//		try{
//			userSnsTokenFacadeService.removeSnsToken(uid, snsType);
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(snsType));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}
//	/**
//	 * 更新用户已经过期的AccessToken
//	 * 1:更新accessToken数据
//	 * 2:如果数据刷新了, 发送sns入驻消息队列执行
//	 * @param request
//	 * @param response
//	 * @param uid
//	 * @param snsType
//	 * @param name 如果是腾讯, 必须提供name(腾讯账户名)
//	 * @param auid
//	 * @param access_token
//	 */
//	@ResponseBody()
//	@RequestMapping(value="/refresh",method={RequestMethod.GET,RequestMethod.POST})
//	public void refresh(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid,
//			@RequestParam(required = true, value = "t") String snsType,
//			@RequestParam(required = false) String name,
//			@RequestParam(required = true) String auid,
//			@RequestParam(required = true) String access_token){
//		
//		User user = this.userService.getById(uid);
//		validateUserNotNull(user);
//		validateSupportType(snsType);
//		try{
//			boolean isRefresh = userSnsTokenFacadeService.validateUpdateSnsToken(uid, snsType, name, auid, access_token);
//			//AccessToken数据确实更新
//			if(isRefresh){
//				//直接发送消息列队
//				super.sendUserSnsJoinMessage(uid, snsType, auid, access_token);
//			}
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(isRefresh));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}
//	
//	/**
//	 * 验证sns type 是否支持
//	 * @param snsType
//	 */
//	protected void validateSupportType(String snsType){
//		FrdType type = BusinessEnumType.FrdType.fromType(snsType);
//		if(type == null){
//			throw new BusinessException(ResponseErrorCode.SNS_TYPE_NOT_SUPPORT, new String[]{snsType});
//		}
//    }
//	/**
//	 * 如果是类型是腾讯, name参数必须不能为空, name是腾讯账户名
//	 * @param snsType
//	 * @param name
//	 */
//	protected void validateQQType4name(String snsType, String name){
//		if(BusinessEnumType.FrdType.qq.getType().equals(snsType)){
//			if(StringHelper.isEmpty(name)){
//				throw new BusinessException(ResponseErrorCode.SNS_TYPE_QQ_NAME_NOTEXIST);
//			}
//		}
//	}
//}
