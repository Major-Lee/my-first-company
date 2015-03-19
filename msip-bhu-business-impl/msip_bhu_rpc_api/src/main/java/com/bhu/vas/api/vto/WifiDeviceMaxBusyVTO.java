package com.bhu.vas.api.vto;

public class WifiDeviceMaxBusyVTO {
	private String wid;//wifi id
	private int hdc;//handset device count 移动设备接入的总数量
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public int getHdc() {
		return hdc;
	}
	public void setHdc(int hdc) {
		this.hdc = hdc;
	}
}
