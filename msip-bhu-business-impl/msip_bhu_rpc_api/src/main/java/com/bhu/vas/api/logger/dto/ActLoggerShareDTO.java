package com.bhu.vas.api.logger.dto;

public class ActLoggerShareDTO {
	private int sid;
	private long ts;
	public ActLoggerShareDTO() {
	}
	public ActLoggerShareDTO(int sid, long ts) {
		super();
		this.sid = sid;
		this.ts = ts;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
}
