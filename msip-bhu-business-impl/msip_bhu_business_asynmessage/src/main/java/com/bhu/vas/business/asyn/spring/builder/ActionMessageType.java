package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionMessageType {
	
	WifiDeviceOnline("Wifi设备上线","wdonline","WN"),
	WifiDeviceOffline("Wifi设备下线","wdoffline","WF"),
	HandsetDeviceOnline("Handset设备上线","hdonline","HN"),
	HandsetDeviceOffline("Handset设备上线","hdoffline","HF"),
	WifiDeviceLocation("wifi设备位置回报","wdlocation","WL"),
	CMUPWithWifiDeviceOnlines("CM上线的wifi设备在线信息","cmup","CW"),
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
