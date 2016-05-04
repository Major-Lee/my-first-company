package com.bhu.vas.rpc.asyncprocessor.dto;


public abstract class AsyncIndexDTO {
	private int uid;
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

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
}
