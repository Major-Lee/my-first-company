package com.bhu.vas.api.dto;

@SuppressWarnings("serial")
public class WifiDeviceContextDTO implements java.io.Serializable{
	/*private String cmId;
	private String cmName;*/
	private CmCtxInfo info;
	private String mac;

	/*public String getCmId() {
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
	}*/
	public WifiDeviceContextDTO() {
	}
	
	public WifiDeviceContextDTO(CmCtxInfo info, String mac) {
		super();
		this.info = info;
		this.mac = mac;
	}

	public String getMac() {
		return mac;
	}

	public CmCtxInfo getInfo() {
		return info;
	}

	public void setInfo(CmCtxInfo info) {
		this.info = info;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("CmCtxInfo:").append(info.toString()).append("   ").append(" Mac:").append(mac);
		return sb.toString();
	}
}

