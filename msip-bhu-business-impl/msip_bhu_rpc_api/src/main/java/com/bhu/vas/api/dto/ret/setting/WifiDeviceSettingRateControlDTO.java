package com.bhu.vas.api.dto.ret.setting;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 设备配置信息的rate_control
 * @author tangzichao
 *
 */
public class WifiDeviceSettingRateControlDTO implements DeviceSettingBuilderDTO{
	//限速成员最多128个
	public static final int Max_MemberCount = 128;
	//不限制速率
	public static final String Rate_Unlimited = "0";
	//所处序号
	//private String index;
	//终端mac
	private String mac;
	//设备发送终端速率(kbps)
	private String tx;
	//设备接收终端速率(kbps)
	private String rx;
	//标识是否删除
	@JsonIgnore
	private String ssdel;
	
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
/*	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}*/
	public String getSsdel() {
		return ssdel;
	}
	public void setSsdel(String ssdel) {
		this.ssdel = ssdel;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingRateControlDTO){
			WifiDeviceSettingRateControlDTO oo = (WifiDeviceSettingRateControlDTO)o;
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
		Object[] properties = new Object[3];
		properties[0] = mac;
		properties[1] = tx;
		properties[2] = rx;
		//properties[3] = index;
		return properties;
	}
	
	//删除rc
	public static final int BuilderType_RemoveRC = 1;
	
	@Override
	public Object[] builderProperties(int type) {
		Object[] properties = null;
		switch(type){
			case BuilderType_RemoveRC:
				properties = new Object[1];
				//properties[0] = index;
				properties[0] = mac;
				break;
			default:
				properties = builderProperties();
				break;
		}
		return properties;
	}

	@Override
	public boolean beRemoved() {
		return Removed.equals(ssdel);
	}
	
//	public static void main(String[] args){
//		String json = "{\"ssdel\":\"1\"}";
//		WifiDeviceSettingRateControlDTO dto = JsonHelper.getDTO(json, WifiDeviceSettingRateControlDTO.class);
//		System.out.println(dto.getSsdel());
//		System.out.println(JsonHelper.getJSONString(dto));
//	}
}
