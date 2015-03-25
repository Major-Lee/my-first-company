package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiDeviceRecentVTO implements Serializable{
	private String wid;//wifi mac
	private long ts;//wifi设备接入时间
	private String adr;//所在地域的格式化地址
	private String wm;//工作模式
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
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
