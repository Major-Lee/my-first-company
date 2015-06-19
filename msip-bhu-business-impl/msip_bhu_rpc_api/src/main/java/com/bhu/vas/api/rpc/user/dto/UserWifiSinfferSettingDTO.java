package com.bhu.vas.api.rpc.user.dto;




/**
 * Wifi 设置定时开关
 * 存储的数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserWifiSinfferSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "uws";
	//Wifi Timer开关
	private boolean on = false;
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	
	
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
}
