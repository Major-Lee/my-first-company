package com.smartwork.async.messagequeue.builder;

import org.apache.commons.lang.StringUtils;

import com.smartwork.async.messagequeue.kafka.model.CommonMessage;
import com.smartwork.async.messagequeue.type.MessageType;
import com.smartwork.msip.cores.helper.JsonHelper;

public class MessageFactoryBuilder {
	public static MessageType determineMessageType(String prefix){
		if(StringUtils.isEmpty(prefix) || prefix.length() != 3) return null;
    	MessageType type = MessageType.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(2);
	}
	public static <T extends PayloadDTO> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(PayloadDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.payloadType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(PayloadDTO message){
		return JsonHelper.getJSONString(message,false);
	}
	
	public static CommonMessage toCommonMessage(PayloadDTO pdto){
		CommonMessage cmessage = new CommonMessage();
		cmessage.setType(pdto.payloadType());
		cmessage.setPayload(toJson(pdto));
		return cmessage;
	}
}
