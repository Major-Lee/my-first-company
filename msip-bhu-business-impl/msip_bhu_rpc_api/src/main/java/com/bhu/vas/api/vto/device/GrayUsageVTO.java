package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class GrayUsageVTO implements java.io.Serializable{
	//device unit type
	private String dut;
	//gray index
	private int gl;
	//device unit name
	private String dun;
	//gray name
	private String gn;
	//统计数量 多少设备使用当前灰度等级
	private int devices;
	//固件版本号id
	private String fwid;
	//增值模块 版本id
	private String omid;
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
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public int getGl() {
		return gl;
	}
	public void setGl(int gl) {
		this.gl = gl;
	}
	public String getDun() {
		return dun;
	}
	public void setDun(String dun) {
		this.dun = dun;
	}
	public String getGn() {
		return gn;
	}
	public void setGn(String gn) {
		this.gn = gn;
	}
	
}
