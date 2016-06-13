package com.bhu.vas.api.helper;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentNotifyType;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class PaymentNotifyFactoryBuilder {
	
	public static PaymentNotifyType determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
		if(messagejsonHasPrefix.startsWith(StringHelper.LEFT_BRACE_STRING))
			return PaymentNotifyType.NormalPaymentNotify;
    	String prefix = messagejsonHasPrefix.substring(0,3);
    	PaymentNotifyType type = PaymentNotifyType.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
		if(messagejsonHasPrefix.startsWith(StringHelper.LEFT_BRACE_STRING))
			return messagejsonHasPrefix;
    	return messagejsonHasPrefix.substring(3);
	}
	public static <T extends ResponsePaymentNotifyType> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(ResponsePaymentNotifyType message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getPaymentNotifyType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(ResponsePaymentNotifyType message){
		return JsonHelper.getJSONString(message,false);
	}
}
