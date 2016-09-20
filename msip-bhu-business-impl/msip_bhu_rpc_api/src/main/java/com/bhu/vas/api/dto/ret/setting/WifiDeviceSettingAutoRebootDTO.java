package com.bhu.vas.api.dto.ret.setting;

/**
 * 设备配置信息的自动重启
 * @author Yetao
 *
 */
public class WifiDeviceSettingAutoRebootDTO implements DeviceSettingBuilderDTO{
	public final static String DefaultTime = "00:00";
	
	private String enable;
	private String time;


	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = enable;
		properties[1] = time;
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
