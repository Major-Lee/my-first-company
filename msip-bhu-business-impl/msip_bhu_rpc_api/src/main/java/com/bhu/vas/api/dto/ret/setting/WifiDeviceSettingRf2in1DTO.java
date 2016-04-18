package com.bhu.vas.api.dto.ret.setting;



/**
 * 设备配置信息的rf_2in1
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRf2in1DTO implements DeviceSettingBuilderDTO{
	//用户名称
	private String rf_2in1;

	public String getRf_2in1() {
		return rf_2in1;
	}

	public void setRf_2in1(String rf_2in1) {
		this.rf_2in1 = rf_2in1;
	}

	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingRf2in1DTO){
			WifiDeviceSettingRf2in1DTO oo = (WifiDeviceSettingRf2in1DTO)o;
			return this.getRf_2in1().equals(oo.getRf_2in1());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getRf_2in1().toString().hashCode();
	}
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[1];
		//properties[0] = oldpassword;
		properties[0] = rf_2in1;
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
