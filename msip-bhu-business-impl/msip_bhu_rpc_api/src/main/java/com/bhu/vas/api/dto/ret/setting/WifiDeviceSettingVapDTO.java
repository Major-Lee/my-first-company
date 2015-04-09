package com.bhu.vas.api.dto.ret.setting;
/**
 * 设备配置信息的vap item
 * @author tangzichao
 *
 */
public class WifiDeviceSettingVapDTO {
	//黑名单类型
	public static final String AclType_Deny = "deny";
	//白名单类型
	public static final String AclType_Allow = "allow";
	//VAP可用
	public static final String Vap_Enable = "enable";
	//VAP不可用
	public static final String Vap_Disable = "disable";
	
	//信号强度
	private String ssid;
	//加密方式
	private String auth;
	//是否可用
	private String enable;
	//名单类型 deny allow
	private String acl_type;
	//名单名称
	private String acl_name;
	
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getAcl_type() {
		return acl_type;
	}
	public void setAcl_type(String acl_type) {
		this.acl_type = acl_type;
	}
	public String getAcl_name() {
		return acl_name;
	}
	public void setAcl_name(String acl_name) {
		this.acl_name = acl_name;
	}
}
