package com.bhu.vas.api.rpc.tag.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagGroupHandsetDetailDTO implements Serializable {
	private String hdMac;
	private String mobileno;
	private int gid;
	private boolean isNewly;
	
	
	public boolean isNewly() {
		return isNewly;
	}
	public void setNewly(boolean isNewly) {
		this.isNewly = isNewly;
	}
	public String getHdMac() {
		return hdMac;
	}
	public void setHdMac(String hdMac) {
		this.hdMac = hdMac;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
}
