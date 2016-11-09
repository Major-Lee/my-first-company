package com.bhu.vas.validate;

import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class ValidateService {
	public static ResponseError validateMobilenoRegx(int countryCode,String mobileno){
		int charlen = mobileno.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"});//renderHtml(response, html, headers)
		}
		
		if(!PhoneHelper.isValidPhoneCharacter(countryCode, mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);//renderHtml(response, html, headers)
		}
		return null;
	}
}
