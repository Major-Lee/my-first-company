package com.bhu.vas.business.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bhu.vas.helper.ThirdPartyMVCHelper;
import com.bhu.vas.thirdparty.response.GomeResponse;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 验证请求是否合法
 * @author yetao
 *
 */
public class ThirdPartyValidateControllerInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyValidateControllerInterceptor.class);

//	private static final String GomePrefixUrl = "/gome";
	private static final String GomeRequestParam_Timestamp = "timestamp";
	private static final String GomeRequestParam_Nonce = "nonce";
	private static final String GomeRequestParam_Sign = "sign";

	
	private boolean validateGome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String method = request.getMethod();

		if(StringUtils.isEmpty(method)){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}

		
		if(!RuntimeConfiguration.isRequestMethodSupported(method)){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}

		String body = ThirdPartyMVCHelper.getRequestBody(request);
		String timestamp = request.getParameter(GomeRequestParam_Timestamp);
		String nonce = request.getParameter(GomeRequestParam_Nonce);
		String sign = request.getParameter(GomeRequestParam_Sign);

		if(StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(sign)){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(timestamp).append(nonce).append(BusinessRuntimeConfiguration.GomeToBhuAppKey);
		if(body != null)
			sb.append(body);
		String oraStr = sb.toString();
		String mysign = CryptoHelper.hmacSha256ToHex(oraStr, BusinessRuntimeConfiguration.GomeToBhuAppKey.getBytes());
		if(!mysign.equals(sign)){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_401_ERROR));
			return false;
		}
		logger.info(String.format("Req uri[%s] URL[%s] body[%s] ", request.getRequestURI(), request.getRequestURI(), body));
		return true;
	}
	
	
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String uri = request.getServletPath();
		
		if(StringUtils.isEmpty(uri)){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_403_ERROR));
			return false;
		}
		
//		if(uri.indexOf(GomePrefixUrl) >= 0)
		return validateGome(request, response);
		
//		SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_404_ERROR));
//		return false;
	}
}
