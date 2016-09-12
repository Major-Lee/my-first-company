package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagGroupSendSortMessageVTO implements Serializable{
	private long taskid;
	private long vcurrency_cost = 0;
	private int sm_count =0;
	private long total_vcurrency;
	private String message;
	
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getVcurrency_cost() {
		return vcurrency_cost;
	}
	public void setVcurrency_cost(long vcurrency_cost) {
		this.vcurrency_cost = vcurrency_cost;
	}
	public int getSm_count() {
		return sm_count;
	}
	public void setSm_count(int sm_count) {
		this.sm_count = sm_count;
	}
	public long getTotal_vcurrency() {
		return total_vcurrency;
	}
	public void setTotal_vcurrency(long total_vcurrency) {
		this.total_vcurrency = total_vcurrency;
	}
}
