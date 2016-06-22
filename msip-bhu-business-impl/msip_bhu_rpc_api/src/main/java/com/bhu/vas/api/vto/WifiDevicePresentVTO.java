package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiDevicePresentVTO implements Serializable{
	private String mac;//wifi id
	private int ol;//online wifi设备是否在线 1表示在线 0标识离线 -1从未上线
	public int getOl() {
		return ol;
	}
	public void setOl(int ol) {
		this.ol = ol;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
