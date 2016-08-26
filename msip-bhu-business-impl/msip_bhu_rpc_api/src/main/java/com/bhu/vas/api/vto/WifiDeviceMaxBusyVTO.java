package com.bhu.vas.api.vto;


@SuppressWarnings("serial")
public class WifiDeviceMaxBusyVTO extends WifiDeviceVTO{
	private long hdc;//handset device count 移动设备接入的总数量
	
	private boolean ipgen = false;
	
	public long getHdc() {
		return hdc;
	}
	public void setHdc(long hdc) {
		this.hdc = hdc;
	}
}
