package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionMessageType {
	
	
	REGISTERED("用户注册成功","registered","RS"),//用户注册成功
	USERSUBJECTCLICK("用户点击文章","usersubjectclick","SC"),
	USERSUBJECTTAGGING("文章标签变更","subjecttagchanged","ST"),
	USERSUBJECTABSTRACTCLICK("用户点击文章摘要","usersubjectabstractclick","AC"),
	USERSUBJECTESTIMATE("用户点击文章摘要","usersubjectabstractclick","UE"),
	USERSUBJECTSHARE("用户点击文章摘要","usersubjectshare","US"),
	USERBLACKDOMAIN("用户点击文章摘要","userblackdomain","BD"),
	SUBJECTWEIXINSHARE("文章分享微信","subjectweixinshare","WS"),
	;
	
	static Map<String, ActionMessageType> allActionMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private ActionMessageType(String cname,String name, String prefix){
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

	public static ActionMessageType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,ActionMessageType>();
		ActionMessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (ActionMessageType type : types){
			allActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
