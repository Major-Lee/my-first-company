package com.bhu.vas.business.asyn.spring.builder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public abstract class ActionDTO {
	private int uid;
	@JsonInclude(Include.NON_NULL)
	private String mac;
	/**
	 * 动作时间
	 */
	private long ts;
	public abstract String getActionType();

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

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
}
