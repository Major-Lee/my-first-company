package com.bhu.vas.business.helper;

import java.util.HashMap;
import java.util.Map;

public enum PaymentChannelCode {    
	/*
	validate.Code.notEquals = 验证码不匹配
	validate.userOrpwd.error = 用户名密码错误
	validate.request.post.method = 只支持post请求*/
	BHU_WAP_WEIXIN("WapWeixin", "WPWX"),
	BHU_APP_WEIXIN("AppWeixin", "APWX"),
	BHU_PC_WEIXIN("PcWeixin", "PCWX"),
	BHU_WAP_ALIPAY("WapAlipay", "WPAL"),
	BHU_APP_ALIPAY("AppAlipay", "APAL"),
	BHU_PC_ALIPAY("PcAlipay", "PCAL"),
	BHU_HEEPAY_WEIXIN("Hee", "HEWX"),
	BHU_MIDAS_WEIXIN("Midas", "MDWX");
	
	
	
	
    private String code;
    private String i18n;

    private static Map<String, PaymentChannelCode> mapKeySuccessCodes;
    
    static {
    	mapKeySuccessCodes = new HashMap<String, PaymentChannelCode>();
    	PaymentChannelCode[] codes = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (PaymentChannelCode code : codes){
			mapKeySuccessCodes.put(code.code, code);
		}
	}
    
    PaymentChannelCode(String code, String i18n) {
        this.code = code;
        this.i18n = i18n;
    }

    public String code() {
        return code;
    }

    public String i18n() {
        return this.i18n;
    }

    public static PaymentChannelCode getPaymentChannelCodeByCode(String code){
    	PaymentChannelCode errorcode = mapKeySuccessCodes.get(code);
    	if(errorcode == null){
    		return BHU_WAP_WEIXIN;
    	}	
    	return errorcode;
    }
    
    public static void main(String[] args) {
		System.out.println(BHU_WAP_WEIXIN.code  + ":::" + BHU_WAP_WEIXIN.i18n());
	}
}
