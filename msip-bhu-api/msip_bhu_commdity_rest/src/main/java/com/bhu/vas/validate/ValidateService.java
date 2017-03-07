package com.bhu.vas.validate;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class ValidateService {
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
	
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
