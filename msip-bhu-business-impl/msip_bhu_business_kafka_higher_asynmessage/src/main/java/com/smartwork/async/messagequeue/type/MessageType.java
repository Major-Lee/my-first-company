package com.smartwork.async.messagequeue.type;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
	UserCreate("用户创建","wdonline","UCC"),
	UserOnline("用户上线","wdonline","UOO"),
	UserOffline("用户下线","wdoffline","UOF"),
	//DeviceOnline("设备上线","hdonline","DOO"),
	//DeviceOffline("设备上线","hdoffline","DOF"),
	;
	
	static Map<String, MessageType> allMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private MessageType(String cname,String name, String prefix){
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

	public static MessageType fromPrefix(String prefix){
		return allMessageTypes.get(prefix);
	}
	
	static {
		allMessageTypes = new HashMap<String,MessageType>();
		MessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (MessageType type : types){
			allMessageTypes.put(type.getPrefix(), type);
		}
	}
}
