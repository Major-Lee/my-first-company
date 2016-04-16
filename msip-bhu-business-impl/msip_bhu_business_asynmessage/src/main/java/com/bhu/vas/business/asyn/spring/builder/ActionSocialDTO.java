package com.bhu.vas.business.asyn.spring.builder;

public abstract class ActionSocialDTO {
	private String uid;
	/**
	 * 动作时间
	 */
	private long ts;
	public abstract String getActionType();

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
