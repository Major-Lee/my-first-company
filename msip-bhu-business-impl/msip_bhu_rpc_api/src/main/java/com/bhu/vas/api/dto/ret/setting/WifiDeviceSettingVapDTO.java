package com.bhu.vas.api.dto.ret.setting;
/**
 * 设备配置信息的vap item
 * @author tangzichao
 *
 */
public class WifiDeviceSettingVapDTO implements DeviceSettingBuilderDTO{
	//黑名单类型
	public static final String AclType_Deny = "deny";
	//白名单类型
	public static final String AclType_Allow = "allow";
	//VAP可用
	public static final String Vap_Enable = "enable";
	//VAP不可用
	public static final String Vap_Disable = "disable";
	
	//VAP名称
	private String name;
	//所属的频
	private String radio;
	
	private String ssid;
	//加密方式
	private String auth;
	//是否可用
	private String enable;
	//黑白名单类型 deny allow
	private String acl_type;
	//黑白名单名称
	private String acl_name;
	//是否开启访客网络
	private String guest_en;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	public String getGuest_en() {
		return guest_en;
	}
	public void setGuest_en(String guest_en) {
		this.guest_en = guest_en;
	}
	public String getRadio() {
		return radio;
	}
	public void setRadio(String radio) {
		this.radio = radio;
	}
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[8];
		properties[0] = name;
		properties[1] = radio;
		properties[2] = ssid;
		properties[3] = auth;
		properties[4] = enable;
		properties[5] = acl_type;
		properties[6] = acl_name;
		properties[7] = guest_en;
		return properties;
	}
}
