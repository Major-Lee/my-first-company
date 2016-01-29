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
	
	HandsetDeviceOnline("HDO", "%s上线", "%s %s %s %s上线", "%s", "%s %s %s %s"),//终端上线
	HandsetDeviceVisitorAuthorizeOnline("HDVO", "%s访客上线", "%s %s %s %s访客上线", "%s", "%s %s %s %s"), //访客网络认证
	HandsetDeviceWSOnline("HDWSO", "周边探测%s", "%s在附近出现", null, null),//终端探测上线
	WifiDeviceReboot("WDR", null, null, null, null),//设备重启成功后
	WifiDeviceSettingChanged("WDC", null, null, null, null),//设备配置变更
	UserBBSsignedon("UBS", "论坛登录", "论坛登录", null, null),//用户bbs登录
	WifiDeviceWorkModeChanged("WMC", null, null, null, null),//设备切换工作模式上线
	;
	String type;
	String title;
	String text;
	//payload info
	String p_title;
	String p_text;
	
	static Map<String, PushType> allPushEnums;
	
	PushType(String type, String title, String text, String p_title, String p_text){
		this.type = type;
		this.title = title;
		this.text = text;
		this.p_title = p_title;
		this.p_text = p_text;
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
	
	public String getP_title() {
		return p_title;
	}

	public void setP_title(String p_title) {
		this.p_title = p_title;
	}

	public String getP_text() {
		return p_text;
	}

	public void setP_text(String p_text) {
		this.p_text = p_text;
	}

	public static PushType getPushTypeFromType(String type) {
		if(StringUtils.isEmpty(type)) return null;
		return allPushEnums.get(type);
	}
}
