package com.bhu.vas.api.rpc.social.vto;

public class UWifiCommentVTO {
	private String bssid;
	private String max_rate;
	private String lat;
	private String lon;
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
