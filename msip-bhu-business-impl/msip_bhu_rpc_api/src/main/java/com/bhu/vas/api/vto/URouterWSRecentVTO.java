package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的周边探测最近出现vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSRecentVTO implements Serializable{
	//终端mac
	private String hd_mac;
	//终端型号
	private String tt;
	//最新一次探测上线时间
	private long last_snifftime;
	
	public String getHd_mac() {
		return hd_mac;
	}
	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}
	public String getTt() {
		return tt;
	}
	public void setTt(String tt) {
		this.tt = tt;
	}
	public long getLast_snifftime() {
		return last_snifftime;
	}
	public void setLast_snifftime(long last_snifftime) {
		this.last_snifftime = last_snifftime;
	}

}
