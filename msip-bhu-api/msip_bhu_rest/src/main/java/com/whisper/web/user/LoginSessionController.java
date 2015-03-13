package com.whisper.web.user;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.user.model.DeviceEnum;
import com.whisper.api.user.model.User;
import com.whisper.api.user.model.UserToken;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.bucache.redis.serviceimpl.present.token.IegalTokenHashService;
import com.whisper.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.whisper.business.captcha.service.CaptchaCodeService;
import com.whisper.business.helper.BusinessWebHelper;
import com.whisper.business.service.UserLoginDataService;
import com.whisper.business.ucenter.service.UserTokenService;
import com.whisper.exception.TokenValidateBusinessException;
import com.whisper.helper.SafetyBitMarkHelper;
import com.whisper.msip.cores.web.mvc.WebHelper;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.AsynMessageController;
import com.whisper.validate.ValidateService;

@Controller
@RequestMapping("/sessions")
public class LoginSessionController extends AsynMessageController{
	//@Resource
	//private UserService userService;
	
	@Resource
	private UserLoginDataService userLoginDataService;
	
	//@Resource
	//UserLoginIpStateService userLoginIpStateService;
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private UserTokenService userTokenService;
	
	@Resource
	private CaptchaCodeService captchaCodeService;
	//@Resource
	//private UserTopicFacadeService userTopicFacadeService;
	
	/*@Resource
	private IegalTokenHashService iegalTokenHashService;*/
	
	
	/**
	 * 帐号密码登录
	 * 帐号包括：email&mobileno
	 * @param request
	 * @param response
	 * @param acc email或者 mobileno
	 * @param pwd 登录密码
	 * @param lang 区域
	 * @param device 设备 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
	public void login(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String captcha,
			//@RequestParam(required = true) String pwd,
			@RequestParam(required = false,defaultValue="") String lang,
			@RequestParam(required = false, value="d",defaultValue="R") String device) {
		//System.out.println(WebHelper.getAddress());
		/*//TODO:增加验证码数 据库比对或者redis
		if(StringUtils.isEmpty(captcha)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_CAPTCHA_EMPTY));
			return;
		}
		if(!captchaCodeService.isValidCaptchaCode(acc, captcha)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_CAPTCHA_DATA_EXPIRED));
			return;
		}*/
		
		//step 1.手机号正则验证
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode, acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		//step 2.生产环境下的手机号验证码验证
		if(!RuntimeConfiguration.SecretInnerTest){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
				ResponseErrorCode errorCode = captchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
					return;
				}
			}
		}/*else{
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if("86 18612272825".equals(accWithCountryCode) || "86 13810048517".equals(accWithCountryCode)){
				ResponseErrorCode errorCode = captchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
				if(errorCode != null){
					SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
					return;
				}
			}
		}*/
		try{
			Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			System.out.println("1. userid:"+uid);
			if(uid == null || uid.intValue() == 0){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST));//renderHtml(response, html, headers)
				return;
			}
			User user = this.userService.getById(uid);
			
			System.out.println("2. user:"+user);
			if(user == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST));//renderHtml(response, html, headers)
				return;
			}
			user.setZombie(false);
			String remoteIp = WebHelper.getRemoteAddr(request);
			if(StringUtils.isEmpty(user.getRegip())){
				user.setRegip(remoteIp);
			}
			if(!user.getLastlogindevice().equals(device)){
				user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
			}
			if(StringUtils.isNotEmpty(lang)){
				if(!lang.equals(user.getLang())){
					user.setLang(lang);
				}
			}
			Date now = new Date();
			user.setLastlogin_at(now);
			user.setToday_firstlogin_at(now);
			this.userService.update(user);
			
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			deliverMessageService.sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp,device);
			Map<String,Object> map = userLoginDataService.buildLoginData(user);
	        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	

	@ResponseBody()
	@RequestMapping(value="/validates",method={RequestMethod.GET,RequestMethod.POST})
	public void validate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,defaultValue="") String lang,
			@RequestParam(required = false, value="d",defaultValue="R") String device/*,
			//模拟参数
			@RequestParam(required = false, value="s",defaultValue="false") boolean s*/) {
		//System.out.println(String.format("step 0 lang:%s device:%s", lang,device));
		/*
		 1、获取远端IP
		 2、获取cookie中ip token
		 3、判断两者是否一样，不一样的话清楚远端cookie，并抛出失败response json
		 */
		String remoteIp = WebHelper.getRemoteAddr(request);
		System.out.println(String.format("step 1 remoteIp:%s", remoteIp));
		String aToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		//System.out.println("~~~~~step2 token:"+aToken);
		if(StringUtils.isEmpty(aToken)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_EMPTY));
			return;
		}
		System.out.println("~~~~~step3 token:"+aToken);
		UserToken uToken = null;
		try{
			uToken = userTokenService.validateUserAccessToken(aToken);
			System.out.println("~~~~~step4 id:"+uToken.getId()+" token:"+uToken.getAccess_token());
			//write header to response header
			BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(uToken.getId().intValue(), uToken.getAccess_token());
		}catch(TokenValidateBusinessException ex){
			int validateCode = ex.getValidateCode();
			System.out.println("~~~~step5 failure~~~~~~token validatecode:"+validateCode);
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
			return;
		}
		try{
			//System.out.println("~~~~step6 ");
			User user  = userService.getById(uToken.getId());
			/*if(user.getNewbie() != User.STATUS_USER_REGISTER_FULL){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.LOGIN_COOKIE_VALIDATE_ACTION_EXCEPTION));
				return;
			}*/
			//System.out.println("~~~~step7 ");
			//boolean needUpdate = false;
			user.setZombie(false);
			if(StringUtils.isEmpty(user.getRegip())){
				user.setRegip(remoteIp);
				//needUpdate = true;
			}
			
			if(!user.getLastlogindevice().equals(device)){
				user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
				//needUpdate = true;
			}
			
			if(StringUtils.isNotEmpty(lang)){
				if(!lang.equals(user.getLang())){
					user.setLang(lang);
					//needUpdate = true;
				}
			}
			Date now = new Date();
			user.setLastlogin_at(now);
			user.setToday_firstlogin_at(now);
			//if(needUpdate)
			this.userService.update(user);
			//System.out.println("~~~~~step8");
			deliverMessageService.sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp,device);
			//validate sns last actionAt and send
			//super.sendUserSnsJoinMessageByLastActionAt(user.getId());
			//userTopicFacadeService.clearUserTopicWall(user.getId());
			//Map<String,Object> map = JsonHelper.filterObjectData(user,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, UserController.userRetFields);
			Map<String,Object> map = userLoginDataService.buildLoginData(user);
	        if(user.getSafety_mark() == SafetyBitMarkHelper.None){
	        	map.put("hint", "账号不安全，请完善安全措施绑定email和mobileno");
	        }
	        
	        //BusinessMUserTraceLogger.doUserValidateLoginLogger(user.getId(), device, uToken.getAccess_token());
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
			System.out.println("~~~~step8 validate ok! ");
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_VALIDATE_EXCEPTION));
			System.out.println("~~~~step9 validate failure! ");
		}
	}
}
