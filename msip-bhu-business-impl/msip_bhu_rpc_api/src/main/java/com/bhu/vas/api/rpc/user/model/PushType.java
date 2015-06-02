package com.bhu.vas.api.rpc.user.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;


/**
 * 针对业务的push类型
 * @author lawliet
 *
 */
public enum PushType {
	
	HandsetDeviceOnline("HDO", "新终端连接", "终端 [%s] 与设备 [%s] 连接成功 "),//终端上线
	;
	String type;
	String title;
	String text;
	
	static Map<String, PushType> allPushEnums;
	
	PushType(String type, String title, String text){
		this.type = type;
		this.title = title;
		this.text = text;
	}
	
	static {
		allPushEnums = new HashMap<String,PushType>();
		PushType[] types = values();
		for (PushType type : types)
			allPushEnums.put(type.getType(), type);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static PushType getPushTypeFromType(String type) {
		if(StringUtils.isEmpty(type)) return null;
		return allPushEnums.get(type);
	}
}
