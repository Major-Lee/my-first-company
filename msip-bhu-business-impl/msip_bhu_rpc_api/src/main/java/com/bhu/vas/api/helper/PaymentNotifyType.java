package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum PaymentNotifyType {
	
	NormalPaymentNotify("支付系统支付成功通知","normal payment notify","NLN"),
	SMSPaymentNotify("短信认证成功通知","sms payment notify","SMN"),
	AdvertiseNotify("广告认证成功通知","advertise notify","ADN"),
	WhiteListNotify("白名单认证成功通知","white list notify","WLN"),
	;
	
	static Map<String, PaymentNotifyType> allActionMessageTypes;
	private String cname;
	private String name;
	private String prefix;
	private PaymentNotifyType(String cname,String name, String prefix){
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

	public static PaymentNotifyType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,PaymentNotifyType>();
		PaymentNotifyType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (PaymentNotifyType type : types){
			allActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
