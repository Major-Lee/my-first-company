package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter主界面VTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterEnterVTO implements Serializable{
	//设备下行网速
	private String wd_date_rx_rate;
	//设备信号强度
	private int power;
	//在线终端数量
	private long ohd_count;
	
	public String getWd_date_rx_rate() {
		return wd_date_rx_rate;
	}
	public void setWd_date_rx_rate(String wd_date_rx_rate) {
		this.wd_date_rx_rate = wd_date_rx_rate;
	}
	public long getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(long ohd_count) {
		this.ohd_count = ohd_count;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getPower() {
		return power;
	}
	
}
