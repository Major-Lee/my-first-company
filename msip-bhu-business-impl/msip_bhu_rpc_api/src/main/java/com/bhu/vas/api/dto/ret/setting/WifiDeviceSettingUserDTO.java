package com.bhu.vas.api.dto.ret.setting;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 设备配置信息的users
 * @author tangzichao
 *
 */
public class WifiDeviceSettingUserDTO implements DeviceSettingBuilderDTO{
	//管理用户的密码rsa加密的公钥私钥
	public static final String Password_PrivateKey = "3b56375e784564dd4a9b7aff6de7a8864660f33d7e8dab20fea969b2ff8c38c482ba7137808a2c5204d6f2737e590db4885ae322f6fb76bb106267285142840d";
	public static final String Password_PublicKey = "c47db6901e79379dff0d831c180c30c3c5c81d78d9c40747349635387a688bd1d61d8a7475181f8293da18f10dc903f94980ee96492707667bc7d621f358df11";
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
