package com.bhu.vas.api.logger.dto;

public class ActLoggerClickDTO {
	private int sid;
	private String act;
	//前端 || 编辑后台
	private String from;
	private long incr = 1l;
	private long ts;
	public ActLoggerClickDTO() {
	}
	public ActLoggerClickDTO(int sid, String act,String from,long incr, long ts) {
		super();
		this.sid = sid;
		this.act = act;
		this.from = from;
		this.incr = incr;
		this.ts = ts;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public long getIncr() {
		return incr;
	}
	public void setIncr(long incr) {
		this.incr = incr;
	}
	
}
