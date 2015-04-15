package com.bhu.vas.api.vto;
/**
 * urouter实时速率vto
 * @author tangzichao
 *
 */
public class URouterRealtimeRateVTO {
	//设备上行速率
	private String tx_rate;
	//设备下行速率
	private String rx_rate;
	
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
}
