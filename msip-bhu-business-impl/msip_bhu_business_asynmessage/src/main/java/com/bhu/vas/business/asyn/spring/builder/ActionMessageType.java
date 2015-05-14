package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionMessageType {
	
	WifiDeviceOnline("Wifi设备上线","wdonline","WN"),
	WifiDeviceOffline("Wifi设备下线","wdoffline","WF"),
	HandsetDeviceOnline("Handset设备上线","hdonline","HN"),
	HandsetDeviceOffline("Handset设备上线","hdoffline","HF"),
	HandsetDeviceSync("Handset设备sync","hdsync","HS"),
	WifiDeviceLocation("wifi设备位置回报","wdlocation","WL"),
	WifiDeviceSettingModify("wifi设备配置下发指令修改","wdsmodify","WM"),
	WifiDeviceTerminalNotify("获取终端列表响应","vapterminal","VT"),
	WifiDeviceRealtimeRateFetch("下发设备实时速率","wdrealtime","WR"),
	WifiDeviceHDRateFetch("下发终端实时速率","wdrealtime","WH"),
	WifiDeviceSpeedFetch("下发设备的网速","wdspeed","WS"),
	CMUPWithWifiDeviceOnlines("CM上线的wifi设备在线信息","cmup","CW"),
	
	WifiCmdDownNotify("wifi指令下发","wcdn","WD"),
	
	USERREGISTERED("用户注册成功","registered","UR"),//用户注册成功
	USERSIGNEDON("用户登陆","signedon","US"),//用户登陆成功动作
	USERRESETPWD("用户重置密码","userresetpwd","UR"),
	USERFETCHCAPTCHACODE("请求验证码","fetchcaptchacode","FC"),//请求验证码动作
	USERDEVICEREGISTER("用户设备注册","user device register","DR"),
	USERDEVICEDESTORY("用户设备注销","user device destory","DD"),
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
