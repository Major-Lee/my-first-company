package com.bhu.vas.api.dto.charging;

public abstract class ChargingAction {
	private String mac;
	private long ts;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public abstract String getAct();
}
