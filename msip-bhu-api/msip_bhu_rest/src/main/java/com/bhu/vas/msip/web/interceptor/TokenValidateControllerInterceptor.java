package com.bhu.vas.msip.web.interceptor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * token validate support
 * 1、LoginController create 和validate api中增加token 数据库验证 验证通过后有效token注册到redis机制
 * 2、UserController中 register_completed 移除写cookie操作
 * 3、UserController中 create 增加token生成机制 及有效token注册到redis机制
 * 4、weibo_rest token机制
 * @author edmond
 *
 */
public class TokenValidateControllerInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(TokenValidateControllerInterceptor.class);
	
	@Resource
	private IUserRpcService userRpcService;

	
	/*private static final String NoAuthPrefixUrl = "/noauth";
	private static final String pingurl = "/ping";
	private static final String commonurl = "/common";
	//private static final String dashboardurl = "/dashboard";
	private static final String statisticsurl = "/statistics";
	private static final String deviceurl = "/device";*/
	//private static final String historyurl = "/history";
	//private static final String guesturl = "/guest";
	//private static final String visiturl = "/visit";
	//private static Set<String> ignoreTokensValidateUrlPrefixSet = new HashSet<String>();
	private static Set<String> ignoreTokensValidateUrlSet = new HashSet<String>();
	static{
		/*ignoreTokensValidateUrlPrefixSet.add("/noauth");
		ignoreTokensValidateUrlPrefixSet.add("/ping");
		ignoreTokensValidateUrlPrefixSet.add("/common");
		ignoreTokensValidateUrlPrefixSet.add("/dashboard");*/
		
		ignoreTokensValidateUrlSet.add("/sessions/create");
		ignoreTokensValidateUrlSet.add("/sessions/validates");
		//ignoreTokensValidateUrlSet.add("/account/create");
		//ignoreTokensValidateUrlSet.add("/account/post_invitation");
		//ignoreTokensValidateUrlSet.add("/account/verify_invitation");
		//检测名称唯一性
		ignoreTokensValidateUrlSet.add("/account/check_mobileno");
		ignoreTokensValidateUrlSet.add("/account/check_device_binded");
		
		//请求验证码
		ignoreTokensValidateUrlSet.add("/user/captcha/fetch_captcha");
		//ignoreTokensValidateUrlSet.add("/account/check_nick");
		//ignoreTokensValidateUrlSet.add("/account/check_email");
		//ignoreTokensValidateUrlSet.add("/account/check_mobileno");
		
		//忘记密码
		//ignoreTokensValidateUrlSet.add("/account/forgot_password");
		//ignoreTokensValidateUrlSet.add("/account/verify_forgot_token");
		//ignoreTokensValidateUrlSet.add("/account/reset_password");
		
		//ignoreTokensValidateUrlSet.add("/personal/fetch");
		
		/*ignoreTokensValidateUrlSet.add("/config/ios");
		ignoreTokensValidateUrlSet.add("/config/android");
		
		//ignoreTokensValidateUrlSet.add("/user/tmp/upload");
		
		ignoreTokensValidateUrlSet.add("/handset/release/ios");
		ignoreTokensValidateUrlSet.add("/handset/release/android");
		//ignoreTokensValidateUrlSet.add("/user/topic/fetch_topics_wall");
		//ignoreTokensValidateUrlSet.add("/tag/fetch_tags");
		//ignoreTokensValidateUrlSet.add("/wallpaper/fetch_categories");
		ignoreTokensValidateUrlSet.add("/handset/feedback/post");

		ignoreTokensValidateUrlSet.add("/user/device/validate");*/
	}
	
	
	/*@Resource
	private IegalTokenHashService iegalTokenHashService;*/
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		//System.out.println("---ControllerInterceptor.preHandle() URI:"+request.getRequestURI()+" uid:"+request.getParameter("uid"));
		//String uri = request.getRequestURI();
		String uri = request.getServletPath();
		//boolean output = false;
		/*if(uri.indexOf("config") != -1){
			output = true;
		}*/
		logger.info(String.format("Rest Request URL [%s] Params [%s]", request.getRequestURI(), request.getParameterMap()));
		//System.out.println("~~~~~~~~~~~~~"+request.getRequestURI()+"  params:"+request.getParameterMap());
		//if(output)
			//System.out.println("~~~~~~~~~~~~~"+uri+"  params:"+request.getParameterMap());
		/*if(uri.startsWith(NoAuthPrefixUrl) || uri.startsWith(statisticsurl) || uri.startsWith(deviceurl)|| uri.startsWith(commonurl) || uri.startsWith(pingurl))
	        return true; */ 
		if(uriStartWithThenSkip(uri)){
			return true;
		}
		String method = request.getMethod();
		if(StringUtils.isEmpty(method)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}
		//if(!method.equalsIgnoreCase("POST")){
		if(!RuntimeConfiguration.isRequestMethodSupported(method)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}
		//System.out.println("uri " + uri);
		/*if((uri.startsWith(guesturl) || uri.startsWith(visiturl)) && !uri.endsWith("create")){
			//System.out.println("in guest");
			//validate authorize code
			String guesttoken = request.getParameter("token");
			//System.out.println("token:" + guesttoken);
			String validate = TokenServiceHelper.parserToken4Guest(guesttoken);
			//System.out.println("guest validate " + validate);
			if(TokenServiceHelper.GuestToken.equals(validate)) return true;
			
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID,new String[]{guesttoken}));
			return false;
		}*/
		
		/*if(uri.endsWith("/sessions/create") || uri.endsWith("/sessions/validates") 
				|| uri.endsWith("/account/create")
				|| uri.endsWith("/account/post_invitation") || uri.endsWith("/account/verify_invitation")
				|| uri.endsWith("/account/check_nick") || uri.endsWith("/account/check_email")
				|| uri.endsWith("/user/topic/fetch_topics_wall") || uri.endsWith("/tag/fetch_tags")
				|| uri.endsWith("/handset/release/ios") || uri.endsWith("/handset/release/android")
				){
			return true;
		}*/
		//if(output) System.out.println("~~~~~~~~~~~~~verify_invitation1");
		if(isIgnoreURL(uri)){
			//if(output) System.out.println("~~~~~~~~~~~~~verify_invitation3");
			return true;
		}
		//if(output) System.out.println("~~~~~~~~~~~~~verify_invitation2");
		
		String accessToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		if(StringUtils.isEmpty(accessToken)){
			accessToken = request.getParameter(RuntimeConfiguration.Param_ATokenRequest);
			if(StringUtils.isEmpty(accessToken)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				return false;
			}
		}
		
		RpcResponseDTO<Boolean> tokenValidate = userRpcService.tokenValidate(request.getParameter(RuntimeConfiguration.Param_UidRequest), accessToken);
		if(tokenValidate.getErrorCode() == null){
			if(!tokenValidate.getPayload().booleanValue()){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
				return false;
			}
		}
		//else
		//	SpringMVCHelper.renderJson(response, ResponseError.embed(tokenValidate.getErrorCode()));
		//System.out.println("before IegalTokenHashService validate!");
		/*boolean isReg = IegalTokenHashService.getInstance().validateUserToken(naolaToken,request.getParameter(RuntimeConfiguration.Param_UidRequest));
		if(!isReg){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
			return false;
		}*/
		
		return true;
	}
	
	
	
	private static boolean isIgnoreURL(String requestUrl){
		for(String igurl:ignoreTokensValidateUrlSet){
			if(requestUrl.endsWith(igurl)) return true;
		}
		return false;
	}
	private static final String patternRegx = "^/(noauth)|(statistics)|(device)|(ping)|(common)";
	/**
	 * 以定义好的字符串前缀
	 * @param url
	 * @return
	 */
	private static boolean uriStartWithThenSkip(String url){
		Pattern pattern = Pattern.compile(patternRegx);
        Matcher matcher = pattern.matcher("/statistic/ddss/ssf");
        return matcher.find();
	}
}
