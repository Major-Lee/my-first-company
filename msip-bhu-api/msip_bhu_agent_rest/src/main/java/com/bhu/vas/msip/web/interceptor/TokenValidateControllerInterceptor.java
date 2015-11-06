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
import com.bhu.vas.api.rpc.agent.iservice.IAgentUserRpcService;
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
	private IAgentUserRpcService agentUserRpcService;

	private static final String ConsolePrefixUrl = "/console";
	private static Set<String> ignoreTokensValidateUrlSet = new HashSet<String>();
	static{
		ignoreTokensValidateUrlSet.add("/sessions/create");
		ignoreTokensValidateUrlSet.add("/sessions/validates");
		ignoreTokensValidateUrlSet.add("/account/create");
		//检测名称唯一性
		ignoreTokensValidateUrlSet.add("/account/check_mobileno");
	}
	
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
		
		String accessToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		if(StringUtils.isEmpty(accessToken)){
			accessToken = request.getParameter(RuntimeConfiguration.Param_ATokenRequest);
			if(StringUtils.isEmpty(accessToken)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				return false;
			}
		}
		
		RpcResponseDTO<Boolean> tokenValidate = agentUserRpcService.tokenValidate(UID, accessToken);
		if(tokenValidate.getErrorCode() == null){
			if(!tokenValidate.getPayload().booleanValue()){//验证不通过
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
				return false;
			}else{//验证通过的情况下，如果uri是以/console开头的,则需要进行uid<=100000区间才能访问
				if(uri.startsWith(ConsolePrefixUrl)){
					//if(StringUtils.isNotEmpty(UID) && Integer.parseInt(UID) <=100000){
					if(RuntimeConfiguration.isConsoleUser(new Integer(UID))){
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
