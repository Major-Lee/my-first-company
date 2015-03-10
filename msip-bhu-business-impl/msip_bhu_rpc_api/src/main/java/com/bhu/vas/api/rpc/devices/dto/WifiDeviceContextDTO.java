package com.bhu.vas.api.rpc.devices.dto;

@SuppressWarnings("serial")
public class WifiDeviceContextDTO implements java.io.Serializable{
	private String cm_id;
	private String cm_name;
	private String mac;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getCm_id() {
		return cm_id;
	}
	public void setCm_id(String cm_id) {
		this.cm_id = cm_id;
	}
	public String getCm_name() {
		return cm_name;
	}
	public void setCm_name(String cm_name) {
		this.cm_name = cm_name;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("cm_id:").append(cm_id).append("   ").append(" cm_name:").append(cm_name);
		sb.append(" Mac:").append(mac);
		return sb.toString();
	}
}

