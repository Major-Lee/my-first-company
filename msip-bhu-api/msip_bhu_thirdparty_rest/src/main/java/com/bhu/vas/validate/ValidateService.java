package com.bhu.vas.validate;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class ValidateService {

	private static final String GomeRequestParam_Timestamp = "timestamp";
	private static final String GomeRequestParam_Nonce = "nonce";
	private static final String GomeRequestParam_Sign = "sign";

	public static ResponseError validateMobilenoRegx(int countryCode,String mobileno, HttpServletRequest request){
		Locale locale = BusinessWebHelper.getLocale(request);
		int charlen = mobileno.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"}, locale);//renderHtml(response, html, headers)
		}
		
		if(!PhoneHelper.isValidPhoneCharacter(countryCode, mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT, locale);//renderHtml(response, html, headers)
		}
		return null;
	}
	
	public static void ValidateGomeRequest(HttpServletRequest request, String body, HttpServletResponse response) throws BusinessI18nCodeException{
		String timestamp = request.getParameter(GomeRequestParam_Timestamp);
		String nonce = request.getParameter(GomeRequestParam_Nonce);
		String sign = request.getParameter(GomeRequestParam_Sign);

		if(StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(sign))
			throw new BusinessI18nCodeException(ResponseErrorCode.REQUEST_403_ERROR);
		
		StringBuffer sb = new StringBuffer();
		sb.append(request.getRequestURI());
		sb.append(timestamp).append(nonce).append(BusinessRuntimeConfiguration.GomeToBhuAppKey);
		if(body != null)
			sb.append(body);
		String oraStr = sb.toString();
		String mysign = CryptoHelper.sha256(oraStr);
		if(!mysign.equals(sign))
			throw new BusinessI18nCodeException(ResponseErrorCode.REQUEST_401_ERROR);
	}
}
