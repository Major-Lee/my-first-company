package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class GrayUsageVTO implements java.io.Serializable{
	//gray index
	private int index;
	//gray name
	private String n;
	//统计数量 多少设备使用当前灰度等级
	private int devices;
	//固件版本号id
	private String fwid;
	//增值模块 版本id
	private String omid;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public int getDevices() {
		return devices;
	}
	public void setDevices(int devices) {
		this.devices = devices;
	}
	public String getFwid() {
		return fwid;
	}
	public void setFwid(String fwid) {
		this.fwid = fwid;
	}
	public String getOmid() {
		return omid;
	}
	public void setOmid(String omid) {
		this.omid = omid;
	}
	
}
