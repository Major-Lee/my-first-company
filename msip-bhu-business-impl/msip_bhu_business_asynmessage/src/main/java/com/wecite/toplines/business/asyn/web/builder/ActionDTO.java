package com.wecite.toplines.business.asyn.web.builder;

public abstract class ActionDTO {
	private int uid;
	/**
	 * 动作时间
	 */
	private long ts;
	public abstract String getActionType();
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
