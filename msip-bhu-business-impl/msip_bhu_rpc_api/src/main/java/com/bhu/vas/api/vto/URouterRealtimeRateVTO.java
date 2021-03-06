package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter实时速率vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterRealtimeRateVTO implements Serializable{
	//设备实时上行速率
	private String tx_rate;
	//设备实时下行速率
	private String rx_rate;
	//当前时间戳
	private long ts;
	
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
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
}
