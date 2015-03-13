package com.bhu.vas.api.dto;

@SuppressWarnings("serial")
public class DeviceDTO implements java.io.Serializable{
	private String mac;
	private long t;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Mac:").append(mac).append("   ").append(" t:").append(t);
		return sb.toString();
	}
}

