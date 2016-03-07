package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionSocialMessageType {
	HandsetMeet("终端遇见","终端遇见","HM"),
	;

	static Map<String, ActionSocialMessageType> allActionMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private ActionSocialMessageType(String cname, String name, String prefix){
		this.cname = cname;
		this.name = name;
		this.prefix = prefix;
	}
	
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public static ActionSocialMessageType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,ActionSocialMessageType>();
		ActionSocialMessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (ActionSocialMessageType type : types){
			allActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
