package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class UserIncomeVTO implements java.io.Serializable{
	private int uid;
	private String time;
	private int times;
	private String income;
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
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	
	
}
