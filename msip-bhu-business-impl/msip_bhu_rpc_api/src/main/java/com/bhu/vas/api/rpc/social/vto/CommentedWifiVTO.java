package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CommentedWifiVTO implements Serializable{
	
	//wifimac 地址
	private String bssid;
	//wifi最大速率
	private String max_rate;
	//wifi所在坐标
	private String lat;
	private String lon;
	private String ssid;
	private String addr;
	
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getMax_rate() {
		return max_rate;
	}
	public void setMax_rate(String max_rate) {
		this.max_rate = max_rate;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}

}
