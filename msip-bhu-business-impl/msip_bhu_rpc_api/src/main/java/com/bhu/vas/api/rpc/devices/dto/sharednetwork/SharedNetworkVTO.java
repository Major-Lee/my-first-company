package com.bhu.vas.api.rpc.devices.dto.sharednetwork;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class SharedNetworkVTO implements java.io.Serializable{
	private String key;
	private String name;
	private String ssid;
	public SharedNetworkVTO() {
	}
	public SharedNetworkVTO(String key,String name,String ssid) {
		this.key = key;
		this.name = name;
		this.ssid = ssid;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
}
