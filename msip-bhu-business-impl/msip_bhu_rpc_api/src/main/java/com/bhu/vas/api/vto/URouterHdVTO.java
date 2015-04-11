package com.bhu.vas.api.vto;
/**
 * urouter的终端vto
 * @author tangzichao
 *
 */
public class URouterHdVTO {
	//终端mac
	private String hd_mac;
	//上行速率
	private String tx_rate;
	//下行速率
	private String rx_rate;
	//昵称
	private String n;
	//下行限速
	private String rx_limit;
	//上行限速
	private String tx_limit;
	//是否在线
	private boolean online;
	
	public String getHd_mac() {
		return hd_mac;
	}
	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}
	public String getRx_rate() {
		return rx_rate;
	}
	public void setRx_rate(String rx_rate) {
		this.rx_rate = rx_rate;
	}
	public String getTx_rate() {
		return tx_rate;
	}
	public void setTx_rate(String tx_rate) {
		this.tx_rate = tx_rate;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
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
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
}
