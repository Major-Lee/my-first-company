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
	//设备信号强度类型 (二居室)
	private String power_type;
	//在线终端数量
	private long ohd_count;
	
	public String getWd_rate() {
		return wd_rate;
	}
	public void setWd_rate(String wd_rate) {
		this.wd_rate = wd_rate;
	}
	public long getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(long ohd_count) {
		this.ohd_count = ohd_count;
	}
	public String getPower_type() {
		return power_type;
	}
	public void setPower_type(String power_type) {
		this.power_type = power_type;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getPower() {
		return power;
	}
	
}
