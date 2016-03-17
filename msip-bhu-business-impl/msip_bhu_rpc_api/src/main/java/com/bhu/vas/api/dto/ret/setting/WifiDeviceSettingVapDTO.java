package com.bhu.vas.api.dto.ret.setting;

import com.bhu.vas.api.helper.WifiDeviceHelper;


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
	
	public static final String VapMode_AP = "ap";
	public static final String VapMode_STA = "sta";
	
	private String mode;
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
	
	private String auth_key;

	private String auth_key_rsa;
	//是否隐藏ssid 默认为关 不隐藏
	private String hide_ssid = WifiDeviceHelper.Disable;

	public WifiDeviceSettingVapDTO(){
		
	}
	
	public WifiDeviceSettingVapDTO(String name){
		this.name = name;
	}
	
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
	public String getAuth_key() {
		return auth_key;
	}
	public void setAuth_key(String auth_key) {
		this.auth_key = auth_key;
	}
	public String getAuth_key_rsa() {
		return auth_key_rsa;
	}
	public void setAuth_key_rsa(String auth_key_rsa) {
		this.auth_key_rsa = auth_key_rsa;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getHide_ssid() {
		return hide_ssid;
	}
	public void setHide_ssid(String hide_ssid) {
		this.hide_ssid = hide_ssid;
	}

	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingVapDTO){
			WifiDeviceSettingVapDTO oo = (WifiDeviceSettingVapDTO)o;
			return this.getName().equals(oo.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getName().toString().hashCode();
	}
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[8];
		properties[0] = name;
		properties[1] = radio;
		properties[2] = ssid;//StringEscapeUtils.escapeXml(ssid);
		properties[3] = auth;
		properties[4] = enable;
		properties[5] = acl_type;
		properties[6] = acl_name;
		properties[7] = guest_en;
		return properties;
	}
	//修改vap的密码
	public static final int BuilderType_VapPassword = 1;
	public static final int BuilderType_WorkModeChanged = 2;
	
	@Override
	public Object[] builderProperties(int type) {
		Object[] properties = null;
		switch(type){
			case BuilderType_VapPassword:
				properties = new Object[6];
				properties[0] = name;
				properties[1] = WifiDeviceHelper.xmlContentEncoder(ssid);//StringEscapeUtils.escapeXml(ssid);
				properties[2] = auth;
				properties[3] = auth_key;
				properties[4] = auth_key_rsa;
				properties[5] = hide_ssid;
				break;
			case BuilderType_WorkModeChanged:
				properties = new Object[9];
				properties[0] = name;
				properties[1] = radio;
				properties[2] = WifiDeviceHelper.xmlContentEncoder(ssid);//StringEscapeUtils.escapeXml(ssid);
				properties[3] = auth;
				properties[4] = enable;
				properties[5] = acl_type;
				properties[6] = acl_name;
				properties[7] = guest_en;
				properties[8] = auth_key_rsa;
				break;
			default:
				properties = builderProperties();
				break;
		}
		return properties;
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
}
