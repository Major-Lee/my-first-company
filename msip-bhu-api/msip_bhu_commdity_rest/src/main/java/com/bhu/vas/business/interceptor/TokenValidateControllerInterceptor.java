package com.bhu.vas.business.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.web.interceptor.AbstractTokenValidateControllerInterceptor;
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
public abstract class TokenValidateControllerInterceptor extends AbstractTokenValidateControllerInterceptor {
	@Resource
	private IUserRpcService userRpcService;
	private static final String ConsolePrefixUrl = "/console";
	
	static{
		ignoreTokensValidateUrlSet.add("/sessions/create");
		ignoreTokensValidateUrlSet.add("/sessions/validates");
		ignoreTokensValidateUrlSet.add("/account/create");
		//检测名称唯一性
		ignoreTokensValidateUrlSet.add("/account/check_mobileno");
	}
	
	@Override
	public ResponseErrorCode validate(HttpServletRequest request,
			HttpServletResponse response) {
		String uri = request.getServletPath();
		String UID = request.getParameter(RuntimeConfiguration.Param_UidRequest);
		String accessToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		if(StringUtils.isEmpty(accessToken)){
			accessToken = request.getParameter(RuntimeConfiguration.Param_ATokenRequest);
			if(StringUtils.isEmpty(accessToken)){
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				//return false;
				return ResponseErrorCode.REQUEST_403_ERROR;
			}
		}
		String udid = request.getHeader(RuntimeConfiguration.Param_UDIDHeader);
		if(StringUtils.isEmpty(udid)){
			udid = request.getParameter(RuntimeConfiguration.Param_UDIDRequest);
		}
		
		RpcResponseDTO<Boolean> tokenValidate = userRpcService.tokenValidate(UID, accessToken, udid);//.tokenValidate(UID, accessToken);
		if(tokenValidate.getErrorCode() == null){
			if(!tokenValidate.getPayload().booleanValue()){//验证不通过
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID));
				return ResponseErrorCode.AUTH_TOKEN_INVALID;
			}else{//验证通过的情况下，如果uri是以/console开头的,则需要进行uid<=100000区间才能访问
				if(uri.startsWith(ConsolePrefixUrl)){
					//if(StringUtils.isNotEmpty(UID) && Integer.parseInt(UID) <=100000){
					if(BusinessRuntimeConfiguration.isConsoleUser(new Integer(UID))){
						System.out.println(UID+"~~~~~~~~~~~~~~能访问管理页面啦！！！！！！！！");
						return null; 
					}else{
						System.out.println(UID+"~~~~~~~~~~~~~~不能访问管理页面啦！！！！！！！！");
						//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
						return ResponseErrorCode.REQUEST_403_ERROR;
					}
				}
			}
		}else{
			//SpringMVCHelper.renderJson(response, ResponseError.embed(tokenValidate.getErrorCode()));
			return tokenValidate.getErrorCode();
		}
		return null;
	}

}
