package com.bhu.vas.business.asyn.spring.model.topic;

public abstract class NotifyDTO {
	private String mac;
	/**
	 * 动作时间
	 */
	private long ts;
	public abstract String getNotifyType();

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
}
