package com.bhu.vas.api.dto.ret.setting;


/**
 * 设备配置信息的rate_control
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRateControlDTO {
	//不限制速率
	public static final String Rate_Unlimited = "0";
	//终端mac
	private String mac;
	//发送速率(kbps)
	private String tx;
	//接收速率(kbps)
	private String rx;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getTx() {
		return tx;
	}
	public void setTx(String tx) {
		this.tx = tx;
	}
	public String getRx() {
		return rx;
	}
	public void setRx(String rx) {
		this.rx = rx;
	}
}
