package com.bhu.vas.api.dto.ret.setting;



/**
 * 设备配置信息的mac_management信息
 * 终端的别名
 * @author tangzichao
 *
 */
public class WifiDeviceSettingMMDTO implements DeviceSettingBuilderDTO{
	//终端的mac
	private String mac;
	//终端的别名
	private String name;

	public WifiDeviceSettingMMDTO(){
		
	}
	
	public WifiDeviceSettingMMDTO(String mac){
		this.mac = mac;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingMMDTO){
			WifiDeviceSettingMMDTO oo = (WifiDeviceSettingMMDTO)o;
			return this.getMac().equals(oo.getMac());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getMac().toString().hashCode();
	}
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = mac;
		properties[1] = name;
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
}
