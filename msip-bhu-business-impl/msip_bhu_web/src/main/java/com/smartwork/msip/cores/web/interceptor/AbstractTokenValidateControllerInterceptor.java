package com.smartwork.msip.cores.web.interceptor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
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
public abstract class AbstractTokenValidateControllerInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(super.getClass());
	//private static final String ConsolePrefixUrl = "/console";
	public static Set<String> ignoreTokensValidateUrlSet = new HashSet<String>();
	/*	static{
		ignoreTokensValidateUrlSet.add("/sessions/create");
		ignoreTokensValidateUrlSet.add("/sessions/validates");
		ignoreTokensValidateUrlSet.add("/account/create");
		//检测名称唯一性
		ignoreTokensValidateUrlSet.add("/account/check_mobileno");
	}*/
	
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
		if(!RuntimeConfiguration.isRequestMethodSupported(method)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}
		if(isIgnoreURL(uri)){
			return true;
		}
		ResponseErrorCode errorCode = this.validate(request, response);
		if(errorCode != null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
			return false;
		}
		return true;
	}
	public abstract ResponseErrorCode validate(HttpServletRequest request,
			HttpServletResponse response);
	
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
	public Logger getLogger() {
		return logger;
	}
	
	
}
