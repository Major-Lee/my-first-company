package com.bhu.vas.business.asyn.spring.builder.async;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;

public class AsyncMessageFactoryBuilder {
	
	public static AsyncMessageType determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	String prefix = messagejsonHasPrefix.substring(0,AsyncMessageType.prefix_length);
    	AsyncMessageType type = AsyncMessageType.fromPrefix(prefix);
    	return type;
	}
	
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(AsyncMessageType.prefix_length);
	}
	
	public static <T extends AsyncDTO> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(AsyncDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getAsyncType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(AsyncDTO message){
		return JsonHelper.getJSONString(message,false);
	}
}
