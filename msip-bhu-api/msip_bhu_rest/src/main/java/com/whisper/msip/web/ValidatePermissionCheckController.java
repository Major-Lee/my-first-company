package com.whisper.msip.web;

import javax.annotation.Resource;

import com.whisper.business.ucenter.service.UserInviteTokensFacadeService;
import com.whisper.business.ucenter.service.UserService;


public abstract class ValidatePermissionCheckController extends ValidateUserCheckController{
	
	@Resource
	protected UserService userService;
	
	@Resource
	public UserInviteTokensFacadeService userInviteTokensFacadeService;
	

	public boolean validateUserInviteTokenCanbeUsed(String token){
		return userInviteTokensFacadeService.validateUserInviteTokenCanbeUsed(token);
	}
	public Integer validateUserInviteToken(String token){
		return userInviteTokensFacadeService.validateUserInviteToken(token);
	}
	
	/*@Deprecated
	public void validateAndUpdateCookies(HttpServletRequest request,HttpServletResponse response,Integer uid){
		validateCookies(request,uid);
		this.writeValidateCookies(request, response, uid);
	}*/
	/*public User validateLoginCookies(HttpServletRequest request){
		//String remoteIp = WebHelper.getRemoteAddr(request);
		String cookiesJson = CookieUtils.getCookie(request, LoginTokenHelper.RemoteCookieName).getValue();
		if(StringUtils.isEmpty(cookiesJson)){
			throw new BusinessException(ResponseErrorCode.AUTH_REMOTECOOKIES_EMPTY);
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_REMOTECOOKIES_EMPTY));
			//return;
		}
		LoginToken loginToken = LoginTokenHelper.fromLoginTokenJson(cookiesJson);
        //boolean isSafeip = false;
        //if(!LoginTokenHelper.validateUid(loginToken.getId().toString(), loginToken.getLs())){
        //	SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_REMOTECOOKIES_INVALID));
		//	return;
        //}
		//if(LoginTokenHelper.validateIpAddress(remoteIp, loginToken.getIt())){
        	//用户机器remote ip 发生变更
        	//TODO:通知客户端强制登陆？
        	//或者往response json中写个账号不安全的提醒
        //	isSafeip = true;
        //}
		Integer tuid = LoginTokenHelper.parseUid(loginToken.getLs());
		User user = this.spliterUserService.getById(tuid);
		if(user == null){
			throw new BusinessException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));//renderHtml(response, html, headers)
			//return;
		}
		if(StringUtils.isEmpty(loginToken.getLe())){
			throw new BusinessException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
		}
		String temail = LoginTokenHelper.parseEmail(loginToken.getLe());
		if(!temail.equalsIgnoreCase(user.getEmail())){
			throw new BusinessException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
		}
		return user;
	}
	
	public void validateCookies(HttpServletRequest request,Integer uid){
		//String remoteIp = WebHelper.getRemoteAddr(request);
		String cookiesJson = CookieUtils.getCookie(request, LoginTokenHelper.RemoteCookieName).getValue();
		if(StringUtils.isEmpty(cookiesJson)){
			throw new BusinessException(ResponseErrorCode.AUTH_REMOTECOOKIES_EMPTY);
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_REMOTECOOKIES_EMPTY));
			//return;
		}
		LoginToken loginToken = LoginTokenHelper.fromLoginTokenJson(cookiesJson);
        //boolean isSafeip = false;
        //if(!LoginTokenHelper.validateUid(loginToken.getId().toString(), loginToken.getLs())){
        //	SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_REMOTECOOKIES_INVALID));
		//	return;
        //}
		//if(LoginTokenHelper.validateIpAddress(remoteIp, loginToken.getIt())){
        	//用户机器remote ip 发生变更
        	//TODO:通知客户端强制登陆？
        	//或者往response json中写个账号不安全的提醒
        //	isSafeip = true;
        //}
		if(StringUtils.isEmpty(loginToken.getLe())){
			throw new BusinessException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
		}
		Integer tuid = LoginTokenHelper.parseUid(loginToken.getLs());
		if(!tuid.equals(uid)) throw new BusinessException(ResponseErrorCode.AUTH_TOKEN_REMOTEUID_NOTMATCH,new String[]{String.valueOf(uid)});
	}*/
	/**
	 * web用户登录和注册的时候传递的密码为混淆+BASE64
	 * 解析为原始密码
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
/*	public String parseWebUserPwdConfusion(String password) throws UnsupportedEncodingException{
		String parseBase64 = new String(Base64Helper.decode(password));
		return StringHelper.parserConfusionPwd(parseBase64);
	}*/
}
