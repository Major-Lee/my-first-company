package com.smartwork.msip.jdo;

import java.util.HashMap;
import java.util.Map;

public enum ResponseSuccessCode {    
	/*
	validate.Code.notEquals = 验证码不匹配
	validate.userOrpwd.error = 用户名密码错误
	validate.request.post.method = 只支持post请求*/
	COMMON_BUSINESS_SUCCESS("200", "common.business.success");
	
	
	
    private String code;
    private String i18n;

    private static Map<String, ResponseSuccessCode> mapKeySuccessCodes;
    
    static {
    	mapKeySuccessCodes = new HashMap<String, ResponseSuccessCode>();
    	ResponseSuccessCode[] codes = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (ResponseSuccessCode code : codes){
			mapKeySuccessCodes.put(code.code, code);
		}
	}
    
    ResponseSuccessCode(String code, String i18n) {
        this.code = code;
        this.i18n = i18n;
    }

    public String code() {
        return code;
    }

    public String i18n() {
        return this.i18n;
    }

    public static ResponseSuccessCode getResponseSuccessCodeByCode(String code){
    	ResponseSuccessCode errorcode = mapKeySuccessCodes.get(code);
    	if(errorcode == null){
    		return COMMON_BUSINESS_SUCCESS;
    	}	
    	return errorcode;
    }
}
