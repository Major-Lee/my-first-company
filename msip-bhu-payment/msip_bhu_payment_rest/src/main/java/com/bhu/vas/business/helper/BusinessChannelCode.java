package com.bhu.vas.business.helper;

import java.util.HashMap;
import java.util.Map;

public enum BusinessChannelCode {    
	/*
	validate.Code.notEquals = 验证码不匹配
	validate.userOrpwd.error = 用户名密码错误
	validate.request.post.method = 只支持post请求*/
	BHU_TIP_BUSINESS("1000", "1F915A8DA370422582CBAC1DB6A806DD"),
	BHU_PREPAID_BUSINESS("1001", "1F915A8DA370422582CBAC1DB6A806UU");
	
	
    private String code;
    private String i18n;

    private static Map<String, BusinessChannelCode> mapKeySuccessCodes;
    
    static {
    	mapKeySuccessCodes = new HashMap<String, BusinessChannelCode>();
    	BusinessChannelCode[] codes = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (BusinessChannelCode code : codes){
			mapKeySuccessCodes.put(code.code, code);
		}
	}
    
    BusinessChannelCode(String code, String i18n) {
        this.code = code;
        this.i18n = i18n;
    }

    public String code() {
        return code;
    }

    public String i18n() {
        return this.i18n;
    }

    public static BusinessChannelCode getResponseSuccessCodeByCode(String code){
    	BusinessChannelCode errorcode = mapKeySuccessCodes.get(code);
    	if(errorcode == null){
    		return BHU_TIP_BUSINESS;
    	}	
    	return errorcode;
    }
    
    public static void main(String[] args) {
		System.out.println(BHU_TIP_BUSINESS.code  + ":::" + BHU_TIP_BUSINESS.i18n());
	}
}
