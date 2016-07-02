package com.bhu.vas.api.dto.statistics;

public class UserStateStatisticsDTO {
	//总用户数
	private long countsUser;
	//当前在线数
	private long online;
	//今日新增
	private long newInc;
	//昨日新增
//	private long yestInc;
	//当日用户数
	private long currentUser;
	//最大在线数
	private long online_max;
	
	public long getCountsUser() {
		return countsUser;
	}
	public void setCountsUser(long countsUser) {
		this.countsUser = countsUser;
	}
	public long getOnline() {
		return online;
	}
	public void setOnline(long online) {
		this.online = online;
	}
	public long getNewInc() {
		return newInc;
	}
	public void setNewInc(long newInc) {
		this.newInc = newInc;
	}
	public long getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(long currentUser) {
		this.currentUser = currentUser;
	}
	public long getOnline_max() {
		return online_max;
	}
	public void setOnline_max(long online_max) {
		this.online_max = online_max;
	}
}
