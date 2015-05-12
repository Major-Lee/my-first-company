package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter实时速率vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterPeakRateVTO implements Serializable{
	//设备网速下行速率
	private String rx_peak_rate;

	public String getRx_peak_rate() {
		return rx_peak_rate;
	}

	public void setRx_peak_rate(String rx_peak_rate) {
		this.rx_peak_rate = rx_peak_rate;
	}
}
