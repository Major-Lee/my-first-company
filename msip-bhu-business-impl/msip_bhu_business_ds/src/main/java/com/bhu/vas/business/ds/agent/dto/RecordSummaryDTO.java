package com.bhu.vas.business.ds.agent.dto;

public class RecordSummaryDTO {
	private String id;
	private long total_onlinetime;
	private int  total_connecttimes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTotal_onlinetime() {
		return total_onlinetime;
	}
	public void setTotal_onlinetime(long total_onlinetime) {
		this.total_onlinetime = total_onlinetime;
	}
	public int getTotal_connecttimes() {
		return total_connecttimes;
	}
	public void setTotal_connecttimes(int total_connecttimes) {
		this.total_connecttimes = total_connecttimes;
	}
	
}
