package com.bhu.vas.business.asyn.spring.builder.async;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
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
	
	public static <T extends ActionDTO> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(ActionDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getActionType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(ActionDTO message){
		return JsonHelper.getJSONString(message,false);
	}
}
