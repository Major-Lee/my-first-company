package com.bhu.vas.business.asyn.spring.builder;

import com.bhu.vas.business.asyn.spring.model.topic.NotifyDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.apache.commons.lang.StringUtils;

public class ActionSocialMessageFactoryBuilder {
	
	public static ActionSocialMessageType determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	String prefix = messagejsonHasPrefix.substring(0,2);
    	ActionSocialMessageType type = ActionSocialMessageType.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(2);
	}
	public static <T extends ActionSocialDTO> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(ActionSocialDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getActionType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(ActionSocialDTO message){
		return JsonHelper.getJSONString(message,false);
	}
	
	
	public static String toJsonHasPrefix(NotifyDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getNotifyType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(NotifyDTO message){
		return JsonHelper.getJSONString(message,false);
	}
	/*public static ActionDTO fromJsonHasPrefix(String messagejson){
		if(StringUtils.isEmpty(messagejson)) return null;
		char prefix = messagejson.charAt(0);
		DeliverMessageType type = DeliverMessageType.fromPrefix(prefix);
		if(type == null) return null;
		return JsonHelper.getDTO(messagejson.substring(1), DeliverMessage.class);
	}*/
}
