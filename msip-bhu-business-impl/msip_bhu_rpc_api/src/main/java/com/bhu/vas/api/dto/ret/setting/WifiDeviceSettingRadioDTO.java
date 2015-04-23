package com.bhu.vas.api.dto.ret.setting;
/**
 * 设备配置信息的radio item
 * 多频设备会有多个dto
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRadioDTO {
	//名称
	private String name;
	//信号强度
	private String power;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
}
