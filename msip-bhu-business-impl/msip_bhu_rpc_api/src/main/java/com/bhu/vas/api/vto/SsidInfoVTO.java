package com.bhu.vas.api.vto;

import java.io.Serializable;

import com.bhu.vas.api.rpc.wifi.model.SsidInfo;

@SuppressWarnings("serial")
public class SsidInfoVTO implements Serializable{
	private String bssid;
	private String ssid;
	private String capabilities;
	private String passwd;
	private Double latiude;
	private Double longitude;
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Double getLatiude() {
		return latiude;
	}
	public void setLatiude(Double latiude) {
		this.latiude = latiude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public static SsidInfoVTO fromSsidInfo(SsidInfo entity){
		if(entity == null)
			return null;
		SsidInfoVTO vto = new SsidInfoVTO();
		vto.setBssid(entity.getId());
		vto.setSsid(entity.getSsid());
		vto.setCapabilities(entity.getMode());
		vto.setPasswd(entity.getPwd());
		vto.setLatiude(entity.getLat());
		vto.setLongitude(entity.getLon());
		return vto;
	}
}
