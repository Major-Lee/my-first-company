package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * urouter的终端vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterHdVTO implements Serializable{
	//终端mac
	private String hd_mac;
	//终端上行速率 bps
	private String tx_rate;
	//终端下行速率 bps
	private String rx_rate;
	//昵称
	private String n;
	//终端的下行限速 bps
	private String rx_limit;
	//终端的上行限速 bps
	private String tx_limit;
	//是否在线
	private boolean online;
	//是否是访客网络的
	private boolean guest;
	//下行字节累加
	private String total_rx_bytes;

	private List<URouterHdTimeLineVTO> timeline;
	
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
	public boolean isGuest() {
		return guest;
	}
	public void setGuest(boolean guest) {
		this.guest = guest;
	}

	public String getTotal_rx_bytes() {
		return total_rx_bytes;
	}

	public void setTotal_rx_bytes(String total_rx_bytes) {
		this.total_rx_bytes = total_rx_bytes;
	}

	public List<URouterHdTimeLineVTO> getTimeline() {
		return timeline;
	}

	public void setTimeline(List<URouterHdTimeLineVTO> timeline) {
		this.timeline = timeline;
	}
}
