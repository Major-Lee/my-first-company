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
	//单用户发送速率(kbps) 终端下行限速
	private Integer users_tx_rate;// = 0;
	//单用户接收速率(kbps) 终端上行限速
	private Integer users_rx_rate;// = 0;

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
	
	/*public int getUsers_tx_rate() {
		return users_tx_rate;
	}
	public void setUsers_tx_rate(int users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}
	public int getUsers_rx_rate() {
		return users_rx_rate;
	}
	public void setUsers_rx_rate(int users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}*/
	
	public Integer getUsers_tx_rate() {
		return users_tx_rate;
	}
	public void setUsers_tx_rate(Integer users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}
	public Integer getUsers_rx_rate() {
		return users_rx_rate;
	}
	public void setUsers_rx_rate(Integer users_rx_rate) {
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
	
	//修改主网络开关
	public static final int BuilderType_InterfaceMasterSwitch = 1;
	//修改主网络统一限速
	public static final int BuilderType_InterfaceMasterLimit = 2;
	
	@Override
	public Object[] builderProperties(int type) {
		Object[] properties = null;
		switch(type){
			case BuilderType_InterfaceMasterSwitch:
				properties = new Object[2];
				properties[0] = name;
				properties[1] = enable;
				break;
			case BuilderType_InterfaceMasterLimit:
				properties = new Object[3];
				properties[0] = name;
				if(users_tx_rate ==null){
					properties[1] = 0;//KBps 转成 kbps
				}else{
					properties[1] = users_tx_rate.intValue() * 8;//KBps 转成 kbps
				}
				
				if(users_tx_rate ==null){
					properties[2] = 0;//KBps 转成 kbps
				}else{
					properties[2] = users_rx_rate.intValue() * 8;//KBps 转成 kbps
				}
				/*properties[1] = users_tx_rate * 8;//KBps 转成 kbps
				properties[2] = users_rx_rate * 8;//KBps 转成 kbps
*/				break;
			default:
				properties = builderProperties();
				break;
		}
		return properties;
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
}
