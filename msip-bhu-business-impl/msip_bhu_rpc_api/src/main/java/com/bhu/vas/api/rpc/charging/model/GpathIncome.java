package com.bhu.vas.api.rpc.charging.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class GpathIncome extends BaseStringModel{
	private String time;
	private String gpath;
	private String income;
	private int times;
	
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getGpath() {
		return gpath;
	}
	public void setGpath(String gpath) {
		this.gpath = gpath;
	}
	
	
}
