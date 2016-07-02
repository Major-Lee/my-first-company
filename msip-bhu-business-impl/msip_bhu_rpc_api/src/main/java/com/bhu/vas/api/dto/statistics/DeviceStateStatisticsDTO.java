package com.bhu.vas.api.dto.statistics;

public class DeviceStateStatisticsDTO {
	//总设备数
	private long countsDevices;
	//当前在线数
	private long online;
	//今日新增
	private long newInc;
	//昨日新增
//	private long yestInc;
	//今日活跃度
	private long liveness;
	//最大在线数
	private long online_max;
	
	public long getCountsDevices() {
		return countsDevices;
	}
	public void setCountsDevices(long countsDevices) {
		this.countsDevices = countsDevices;
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
//	public long getYestInc() {
//		return yestInc;
//	}
//	public void setYestInc(long yestInc) {
//		this.yestInc = yestInc;
//	}
	public long getLiveness() {
		return liveness;
	}
	public void setLiveness(long liveness) {
		this.liveness = liveness;
	}
	public long getOnline_max() {
		return online_max;
	}
	public void setOnline_max(long online_max) {
		this.online_max = online_max;
	}
	
}
