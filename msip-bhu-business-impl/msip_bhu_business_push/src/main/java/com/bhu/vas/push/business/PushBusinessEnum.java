package com.bhu.vas.push.business;

import java.util.HashMap;
import java.util.Map;


/**
 * 用于ios push 发送的证书类型
 * @author lawliet
 *
 */
public enum PushBusinessEnum {
	
	HandsetDeviceOnline("HDO", "handset [%s] online by [%s]"),//终端上线
	;
	String type;
	String template;
	
	static Map<String, PushBusinessEnum> allPushEnums;
	
	PushBusinessEnum(String type, String template){
		this.type = type;
		this.template = template;
	}
	
	static {
		allPushEnums = new HashMap<String,PushBusinessEnum>();
		PushBusinessEnum[] types = values();
		for (PushBusinessEnum type : types)
			allPushEnums.put(type.getType(), type);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public static PushBusinessEnum getPushBusinessEnumFromType(String type) {
		return allPushEnums.get(type);
	}
}
