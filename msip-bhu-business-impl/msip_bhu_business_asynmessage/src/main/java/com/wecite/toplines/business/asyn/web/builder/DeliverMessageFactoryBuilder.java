package com.wecite.toplines.business.asyn.web.builder;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;

public class DeliverMessageFactoryBuilder {
	
	public static DeliverMessage buildDeliverMessage(char type, int uid,String messagedata){
		DeliverMessage message = new DeliverMessage();
		message.setType(type);
		message.setUid(uid);
		message.setMessagedata(messagedata);
		return message;
	}
	
	public static DeliverMessage fromJsonHasPrefix(String messagejson){
		if(StringUtils.isEmpty(messagejson)) return null;
		char prefix = messagejson.charAt(0);
		DeliverMessageType type = DeliverMessageType.fromPrefix(prefix);
		if(type == null) return null;
		return JsonHelper.getDTO(messagejson.substring(1), DeliverMessage.class);
	}
	
	public static DeliverMessage fromJson(String messagejson){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, DeliverMessage.class);
	}	
	
	public static String toJsonHasPrefix(DeliverMessage message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getType()).append(JsonHelper.getJSONString(message));
		return sb.toString();
	}
	public static String toJson(DeliverMessage message){
		return JsonHelper.getJSONString(message);
	}
	
}
