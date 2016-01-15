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
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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

	private static final String ConsolePrefixUrl = "/console";
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
		ignoreTokensValidateUrlSet.add("/sessions/create");
		ignoreTokensValidateUrlSet.add("/sessions/create_traditional");
		ignoreTokensValidateUrlSet.add("/sessions/validates");
		ignoreTokensValidateUrlSet.add("/sessions/bbs_login");
		ignoreTokensValidateUrlSet.add("/account/create");
		//ignoreTokensValidateUrlSet.add("/account/post_invitation");
		//ignoreTokensValidateUrlSet.add("/account/verify_invitation");
		//检测名称唯一性
		ignoreTokensValidateUrlSet.add("/account/check_mobileno");
		ignoreTokensValidateUrlSet.add("/account/check_nick");
		ignoreTokensValidateUrlSet.add("/account/check_device_binded");
		//请求验证码
		ignoreTokensValidateUrlSet.add("/user/captcha/fetch_captcha");
		
		ignoreTokensValidateUrlSet.add("/console/sessions/create");
		ignoreTokensValidateUrlSet.add("/console/sessions/validates");
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
		String uri = request.getServletPath();
		String UID = request.getParameter(RuntimeConfiguration.Param_UidRequest);
		logger.info(String.format("Req uri[%s] URL[%s] uid [%s]",uri, request.getRequestURI(), UID));
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
		if(isIgnoreURL(uri)){
			return true;
		}
		
		String accessToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		if(StringUtils.isEmpty(accessToken)){
			accessToken = request.getParameter(RuntimeConfiguration.Param_ATokenRequest);
			if(StringUtils.isEmpty(accessToken)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				return false;
			}
		}
		
		RpcResponseDTO<Boolean> tokenValidate = userRpcService.tokenValidate(UID, accessToken);
		if(tokenValidate.getErrorCode() == null){
			if(!tokenValidate.getPayload().booleanValue()){//验证不通过
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
				return false;
			}else{//验证通过的情况下，如果uri是以/console开头的,则需要进行uid<=100000区间才能访问
				if(uri.startsWith(ConsolePrefixUrl)){
					//if(StringUtils.isNotEmpty(UID) && Integer.parseInt(UID) <=100000){
					if(BusinessRuntimeConfiguration.isConsoleUser(new Integer(UID))){
						System.out.println(UID+"~~~~~~~~~~~~~~能访问管理页面啦！！！！！！！！");
						return true; 
					}else{
						System.out.println(UID+"~~~~~~~~~~~~~~不能访问管理页面啦！！！！！！！！");
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
						return false;
					}
				}
			}
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(tokenValidate.getErrorCode()));
			return false;
		}
		return true;
	}
	
	private static boolean isIgnoreURL(String requestUrl){
		for(String igurl:ignoreTokensValidateUrlSet){
			if(requestUrl.endsWith(igurl)) return true;
		}
		return false;
	}
	//private static final String patternRegx = "^/((noauth)|(statistics)|(device)|(cmd)|(ping)|(common)|(api-docs))";//"^/(noauth)|(statistics)|(device)|(ping)|(common)|(api-docs)";
	//private static final String patternRegx = "^/((noauth)|(cmd)|(ping)|(common)|(api-docs))";//"^/(noauth)|(statistics)|(device)|(ping)|(common)|(api-docs)";
	private static final String patternRegx = "^/((noauth)|(ping)|(common)|(api-docs))";//"^/(noauth)|(statistics)|(device)|(ping)|(common)|(api-docs)";
	/**
	 * 以定义好的字符串前缀
	 * @param url
	 * @return
	 */
	private static boolean uriStartWithThenSkip(String uri){
		Pattern pattern = Pattern.compile(patternRegx);
        Matcher matcher = pattern.matcher(uri);
        return matcher.find();
	}
}
