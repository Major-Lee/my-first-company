package com.bhu.vas.api.vto;

@SuppressWarnings("serial")
public class URouterInfoVTO implements java.io.Serializable{
	private String wan_ip;//wan ip
	private String adr;//设备地址
	private String carrier; //设备网络运营商信息
	private String wm;//设备工作模式
	
	public String getWan_ip() {
		return wan_ip;
	}
	public void setWan_ip(String wan_ip) {
		this.wan_ip = wan_ip;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getWm() {
		return wm;
	}
	public void setWm(String wm) {
		this.wm = wm;
	}
}
