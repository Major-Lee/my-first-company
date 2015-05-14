package com.bhu.vas.api.dto.ret.setting;


/**
 * 设备配置信息的interface
 * @author tangzichao
 *
 */
public class WifiDeviceSettingInterfaceDTO implements DeviceSettingBuilderDTO{
	//不限制速率
	public static final String Rate_Unlimited = "0";
	
	//接口名称
	private String name;
	//接口是否可用
	private String enable;
	//接口发送速率(kbps)
	private String if_tx_rate;
	//接口接收速率(kbps)
	private String if_rx_rate;
	//单用户发送速率(kbps)
	private String users_tx_rate;
	//单用户接收速率(kbps)
	private String users_rx_rate;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getIf_tx_rate() {
		return if_tx_rate;
	}
	public void setIf_tx_rate(String if_tx_rate) {
		this.if_tx_rate = if_tx_rate;
	}
	public String getIf_rx_rate() {
		return if_rx_rate;
	}
	public void setIf_rx_rate(String if_rx_rate) {
		this.if_rx_rate = if_rx_rate;
	}
	public String getUsers_tx_rate() {
		return users_tx_rate;
	}
	public void setUsers_tx_rate(String users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}
	public String getUsers_rx_rate() {
		return users_rx_rate;
	}
	public void setUsers_rx_rate(String users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingInterfaceDTO){
			WifiDeviceSettingInterfaceDTO oo = (WifiDeviceSettingInterfaceDTO)o;
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
		// TODO Auto-generated method stub
		return null;
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
