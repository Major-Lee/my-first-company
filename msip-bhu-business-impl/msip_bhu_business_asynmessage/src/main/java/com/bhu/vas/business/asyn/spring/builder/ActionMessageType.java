package com.bhu.vas.business.asyn.spring.builder;

import java.util.HashMap;
import java.util.Map;

public enum ActionMessageType {
	
	WifiDeviceOnline("Wifi设备上线","wdonline","WN"),
	WifiDeviceOffline("Wifi设备下线","wdoffline","WF"),
	WifiDeviceModuleOnline("Wifi设备模块组件上线","wdmonline","MO"),
	
	HandsetDeviceOnline("Handset设备上线","hdonline","HN"),
	HandsetDeviceOffline("Handset设备上线","hdoffline","HF"),
	HandsetDeviceSync("Handset设备sync","hdsync","HS"),
	HandsetDeviceVisitorAuthorizeOnline("访客网络设备认证通过","hdvonline", "HV"),
	WifiDeviceLocation("wifi设备位置回报","wdlocation","WL"),
	WifiDeviceSettingModify("wifi设备配置下发指令修改","wdsmodify","WM"),
	WifiDeviceSettingChanged("wifi设备配置变更","wdschanged","WC"),
	WifiDeviceSettingQuery("wifi设备配置查询","wdschanged","WQ"),
	WifiDeviceTerminalNotify("获取终端列表响应","vapterminal","VT"),
	WifiDeviceRealtimeRateFetch("下发设备实时速率","wdrealtime","WR"),
	WifiDeviceHDRateFetch("下发终端实时速率","wdrealtime","WH"),
	WifiDeviceSpeedFetch("下发设备的网速","wdspeed","WS"),
	WifiDeviceUsedStatus("下发设备的网速","wdspeed","WU"),
	WifiDevicesGrayChanged("设备灰度变更","wdgraychanged","WG"),
	WifiDevicesModuleStyleChanged("设备增值模板变更","wdmodulestylechanged","WD"),
	
	DeviceModifySettingAclMacs("修改黑名单内容","dsaclmacs","DA"),
	DeviceModifySettingAalias("修改终端别名","dsalias","DI"),
	
	CMUPWithWifiDeviceOnlines("CM上线的wifi设备在线信息","cmup","CW"),
	
	WifiCmdsDownNotify("wifi多指令下发","wcsdn","SM"),
	WifiMultiCmdsDownNotify("wifi多指令下发","wmcsdn","SS"),
	WifiDeviceAsyncCMDGen("给设备组下发指令","wdgcmd","WA"),
	WifiDeviceGroupCreateIndex("设备群组添加设备时建立搜索索引", "wdgci", "WI"),
	
	USERREGISTERED("用户注册成功","registered","UR"),//用户注册成功
	USERSIGNEDON("用户登陆","signedon","US"),//用户登陆成功动作
	USERRESETPWD("用户重置密码","userresetpwd","UP"),
	USERFETCHCAPTCHACODE("请求验证码","fetchcaptchacode","FC"),//请求验证码动作
	USERDEVICEREGISTER("用户设备注册","user device register","DR"),
	USERDEVICEDESTORY("用户设备注销","user device destory","DD"),
	USERBBSSIGNEDON("用户bbs登陆","user bbs signedon","UB"),//用户登陆
	
	TOPICCMJoinNotify("topic消息CM加入通知","topic cm join","TJ"),
	TOPICCMLeaveNotify("topic消息CM离开通知","topic cm leave","TL"),
	TOPICDeviceOnlineNotify("topic消息Device上线","topic device online","TO"),
	TOPICDevicesOnlineNotify("topic消息Devices上线","topic cm devices online","TS"),
	TOPICDeviceOfflineNotify("topic消息Device下线","topic cm offline","TF"),


	AgentDeviceClaimImport("代理商设备导入","agent device claim import","AI"),
	AgentDeviceClaimUpdate("代理商导入更新", "agent device update ", "AU"),
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
