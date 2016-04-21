package com.bhu.vas.api.dto.ret.setting;
/**
 * 设备配置信息的radio item
 * 多频设备会有多个dto
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRadioDTO implements DeviceSettingBuilderDTO{
	
    public static final int MODEL_Power_Radio = 0;
    public static final int MODEL_RealChannel_Radio = 1;
	
	//名称
	private String name;
	//信号强度
	private String power;
	
	//信道
	private String real_channel;
	
	//rf 射频类型
	private String rf;
	//国家码
	private String country;
	//频宽
	private String channel_bandwidth;
	
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
		//properties[2] = real_channel;
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		Object[] properties = null;
		if (MODEL_Power_Radio == type) {
			properties = new Object[2];
			properties[0] = name;
			properties[1] = power;
        }else if (MODEL_RealChannel_Radio == type) {
        	properties = new Object[3];
        	properties[0] = name;
			properties[1] = real_channel;
			properties[2] = real_channel;
        }
		return properties;
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}

	public String getReal_channel() {
		return real_channel;
	}

	public void setReal_channel(String real_channel) {
		this.real_channel = real_channel;
	}

	public String getRf() {
		return rf;
	}

	public void setRf(String rf) {
		this.rf = rf;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getChannel_bandwidth() {
		return channel_bandwidth;
	}

	public void setChannel_bandwidth(String channel_bandwidth) {
		this.channel_bandwidth = channel_bandwidth;
	}
	
}
