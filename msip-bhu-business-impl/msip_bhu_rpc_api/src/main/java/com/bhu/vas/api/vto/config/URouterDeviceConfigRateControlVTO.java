package com.bhu.vas.api.vto.config;

import java.io.Serializable;

/**
 * 返回urouter限速配置数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterDeviceConfigRateControlVTO implements Serializable{
	//终端mac
	private String mac;
	//终端的下行限速 bps
	private String rx_limit;
	//终端的上行限速 bps
	private String tx_limit;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getRx_limit() {
		return rx_limit;
	}
	public void setRx_limit(String rx_limit) {
		this.rx_limit = rx_limit;
	}
	public String getTx_limit() {
		return tx_limit;
	}
	public void setTx_limit(String tx_limit) {
		this.tx_limit = tx_limit;
	}
}
