package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class HandsetDeviceOfflineDTO extends ActionDTO {
	private String wifiId;//连接的wifi mac
	private String uptime;//移动设备的连接状态的持续时间

	//发送字节数
	private String tx_bytes;

	//接收字节数
	private String rx_bytes;
	
	public String getWifiId() {
		return wifiId;
	}

	public void setWifiId(String wifiId) {
		this.wifiId = wifiId;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getTx_bytes() {
		return tx_bytes;
	}

	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}

	public String getRx_bytes() {
		return rx_bytes;
	}

	public void setRx_bytes(String rx_bytes) {
		this.rx_bytes = rx_bytes;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.HandsetDeviceOffline.getPrefix();
	}

}
