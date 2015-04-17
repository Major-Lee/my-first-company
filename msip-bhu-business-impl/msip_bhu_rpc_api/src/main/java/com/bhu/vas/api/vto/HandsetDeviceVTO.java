package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HandsetDeviceVTO implements Serializable{
	private String wid;//wifi id
	private String hid;//handset id
	private int ol;//是否在线
	private long ts;//最后一次接入的时间
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public int getOl() {
		return ol;
	}
	public void setOl(int ol) {
		this.ol = ol;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
