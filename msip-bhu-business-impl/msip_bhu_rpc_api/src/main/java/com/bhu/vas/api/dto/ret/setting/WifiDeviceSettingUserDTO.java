package com.bhu.vas.api.dto.ret.setting;


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
	@Override
	public Object[] builderProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
}
