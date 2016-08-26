package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum PermissionThroughNotifyType {
	
	RewardPermissionNotify("打赏许可放行通知","reward permission","RDN"),
	SMSPermissionNotify("短信认证许可放行通知","sms permission","SSN"),
	VideoPermissionNotify("视频认证许可放行通知","video permission","ADN"),
	;
	
	static Map<String, PermissionThroughNotifyType> allActionMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private PermissionThroughNotifyType(String cname,String name, String prefix){
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

	public static PermissionThroughNotifyType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,PermissionThroughNotifyType>();
		PermissionThroughNotifyType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (PermissionThroughNotifyType type : types){
			allActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
