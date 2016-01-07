package com.bhu.vas.api.dto.ret.setting;




/**
 * 设备配置信息的mode信息
 * @author tangzichao
 *
 */
public class WifiDeviceSettingModeDTO implements DeviceSettingBuilderDTO{
	//设备工作模式
	private String mode;

	public WifiDeviceSettingModeDTO(){
		
	}
	
	public WifiDeviceSettingModeDTO(String mode){
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingModeDTO){
			WifiDeviceSettingModeDTO oo = (WifiDeviceSettingModeDTO)o;
			return this.getMode().equals(oo.getMode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getMode().toString().hashCode();
	}
	
	@Override
	public Object[] builderProperties() {
		return null;
	}
	
	//删除mm
	public static final int BuilderType_RemoveMM = 1;
	
	@Override
	public Object[] builderProperties(int type) {
		return null;
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
}
