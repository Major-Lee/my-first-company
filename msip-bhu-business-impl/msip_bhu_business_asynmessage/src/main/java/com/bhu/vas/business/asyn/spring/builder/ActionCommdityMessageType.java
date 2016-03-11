package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionCommdityMessageType {
	OrderPaySuccessed("订单支付成功","订单支付成功","PS"),
	;
	
	static Map<String, ActionCommdityMessageType> allActionMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private ActionCommdityMessageType(String cname,String name, String prefix){
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

	public static ActionCommdityMessageType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,ActionCommdityMessageType>();
		ActionCommdityMessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (ActionCommdityMessageType type : types){
			allActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
