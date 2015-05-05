package com.bhu.vas.api.dto.ret.setting;
/**
 * 设备配置信息的radio item
 * 多频设备会有多个dto
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRadioDTO implements DeviceSettingBuilderDTO{
	//名称
	private String name;
	//信号强度
	private String power;
	
	public WifiDeviceSettingRadioDTO(){
		
	}
	
	public WifiDeviceSettingRadioDTO(String name){
		this.name = name;
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingRadioDTO){
			WifiDeviceSettingRadioDTO oo = (WifiDeviceSettingRadioDTO)o;
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
		properties[0] = name;
		properties[1] = power;
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
}
