package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UcloudMacStatistic implements Serializable{
	//厂家
	private String vender;
	//打赏时间
	private String time;
	//打赏收益
	private String income;
	//打赏方式
	private String method;
	//打赏设备
	private String uMac;
	//终端设备
	private String mac;
	public String getVender() {
		return vender;
	}
	public void setVender(String vender) {
		this.vender = vender;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getuMac() {
		return uMac;
	}
	public void setuMac(String uMac) {
		this.uMac = uMac;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
}
