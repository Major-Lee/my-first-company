package com.bhu.vas.api.vto;
/**
 * urouter主界面VTO
 * @author tangzichao
 *
 */
public class URouterEnterVTO {
	//设备网络速率
	private String wd_rate;
	//设备信号强度
	private int power;
	//在线终端数量
	private int ohd_count;
	
	public String getWd_rate() {
		return wd_rate;
	}
	public void setWd_rate(String wd_rate) {
		this.wd_rate = wd_rate;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(int ohd_count) {
		this.ohd_count = ohd_count;
	}
}
