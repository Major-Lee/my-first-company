package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiDeviceMaxBusyVTO implements Serializable{
	private String wid;//wifi id
	private long hdc;//handset device count 移动设备接入的总数量
	private String adr;//所在地域的格式化地址
	private String wm;//工作模式
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public long getHdc() {
		return hdc;
	}
	public void setHdc(long hdc) {
		this.hdc = hdc;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getWm() {
		return wm;
	}
	public void setWm(String wm) {
		this.wm = wm;
	}
}
