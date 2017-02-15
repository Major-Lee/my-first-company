package com.bhu.vas.api.dto.advertise;

@SuppressWarnings("serial")
public class AdvertiseCPMDTO implements java.io.Serializable{
	private String adid;
	private String mac;
	
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
