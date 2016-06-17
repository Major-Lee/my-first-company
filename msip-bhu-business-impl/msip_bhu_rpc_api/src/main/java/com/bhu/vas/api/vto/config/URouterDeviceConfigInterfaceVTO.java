package com.bhu.vas.api.vto.config;

import java.io.Serializable;

@SuppressWarnings("serial")
public class URouterDeviceConfigInterfaceVTO implements Serializable{
	//interface name
	private String name;
	//interface 开关
	private String enable;
	//终端下行限速(KBps)
	private int users_tx_rate = 0;
	//终端上行限速(KBps)
	private int users_rx_rate = 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public int getUsers_tx_rate() {
		return users_tx_rate;
	}
	public void setUsers_tx_rate(int users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}
	public int getUsers_rx_rate() {
		return users_rx_rate;
	}
	public void setUsers_rx_rate(int users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}
}
