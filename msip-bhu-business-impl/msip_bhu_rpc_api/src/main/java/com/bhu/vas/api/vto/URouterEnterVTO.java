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
	private String data_rx_rate_peak;
	//设备实时下行速率
	private String data_rx_rate;
	//设备信号强度
	private int power;
	//在线终端数量
	private long ohd_count;
	
	public String getData_rx_rate_peak() {
		return data_rx_rate_peak;
	}
	public void setData_rx_rate_peak(String data_rx_rate_peak) {
		this.data_rx_rate_peak = data_rx_rate_peak;
	}
	public String getData_rx_rate() {
		return data_rx_rate;
	}
	public void setData_rx_rate(String data_rx_rate) {
		this.data_rx_rate = data_rx_rate;
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
