package com.bhu.vas.api.rpc.devices.dto.sharednetwork;

/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class OpenMacDTO implements java.io.Serializable{
	
	private String mac;
	private String name;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
