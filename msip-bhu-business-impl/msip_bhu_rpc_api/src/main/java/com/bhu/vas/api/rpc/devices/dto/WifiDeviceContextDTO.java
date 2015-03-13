package com.bhu.vas.api.rpc.devices.dto;

@SuppressWarnings("serial")
public class WifiDeviceContextDTO implements java.io.Serializable{
	private String cmId;
	private String cmName;
	private String mac;

	public String getCmId() {
		return cmId;
	}

	public void setCmId(String cmId) {
		this.cmId = cmId;
	}

	public String getCmName() {
		return cmName;
	}

	public void setCmName(String cmName) {
		this.cmName = cmName;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("cm_id:").append(cmId).append("   ").append(" cm_name:").append(cmName);
		sb.append(" Mac:").append(mac);
		return sb.toString();
	}
}

