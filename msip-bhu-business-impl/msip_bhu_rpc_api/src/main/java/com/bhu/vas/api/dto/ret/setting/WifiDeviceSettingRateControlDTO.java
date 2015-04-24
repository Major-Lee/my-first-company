package com.bhu.vas.api.dto.ret.setting;


/**
 * 设备配置信息的rate_control
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRateControlDTO implements DeviceSettingBuilderDTO{
	//不限制速率
	public static final String Rate_Unlimited = "0";
	//终端mac
	private String mac;
	//设备发送终端速率(kbps)
	private String tx;
	//设备接收终端速率(kbps)
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
	@Override
	public Object[] builderProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
