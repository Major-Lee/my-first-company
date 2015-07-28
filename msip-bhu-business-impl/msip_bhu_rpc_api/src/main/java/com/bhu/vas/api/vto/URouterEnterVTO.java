package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter主界面VTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterEnterVTO implements Serializable{
	//设备网速峰值
	//private String peak_rate;
	//设备实时上行速率
	private String tx_rate;
	//设备实时下行速率
	private String rx_rate;
	//设备信号强度
	//private int power;
	//在线终端数量
	private long ohd_count;
	
/*	public String getPeak_rate() {
		return peak_rate;
	}
	public void setPeak_rate(String peak_rate) {
		this.peak_rate = peak_rate;
	}*/
	public String getTx_rate() {
		return tx_rate;
	}
	public void setTx_rate(String tx_rate) {
		this.tx_rate = tx_rate;
	}
	public String getRx_rate() {
		return rx_rate;
	}
	public void setRx_rate(String rx_rate) {
		this.rx_rate = rx_rate;
	}
	public long getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(long ohd_count) {
		this.ohd_count = ohd_count;
	}
//	public void setPower(int power) {
//		this.power = power;
//	}
//	public int getPower() {
//		return power;
//	}
	
}
