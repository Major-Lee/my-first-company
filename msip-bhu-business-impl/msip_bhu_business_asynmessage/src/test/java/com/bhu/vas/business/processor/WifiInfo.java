package com.bhu.vas.business.processor;

public class WifiInfo {
	private String mac;
	private String sn;
	private String ip;
	
	public WifiInfo(String mac, String sn, String ip) {
		super();
		this.mac = mac;
		this.sn = sn;
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
