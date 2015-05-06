package com.bhu.vas.api.dto.ret.setting;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 设备配置信息的users
 * @author tangzichao
 *
 */
public class WifiDeviceSettingUserDTO implements DeviceSettingBuilderDTO{
	//用户名称
	private String name;
	//加密密码
	private String password_enc;
	//未知
	private String auth;
	
	@JsonIgnore
	private String oldpassword;
	@JsonIgnore
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword_enc() {
		return password_enc;
	}
	public void setPassword_enc(String password_enc) {
		this.password_enc = password_enc;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getOldpassword() {
		return oldpassword;
	}
	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingUserDTO){
			WifiDeviceSettingUserDTO oo = (WifiDeviceSettingUserDTO)o;
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
		Object[] properties = new Object[2];
		properties[0] = oldpassword;
		properties[1] = password;
		return properties;
	}
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
}
