package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

public class SystemStatisticsDTO {
	
	public static final String Field_Devices = "devices";
	public static final String Field_Handsets = "handsets";
	public static final String Field_OnlineHandsets = "online_handsets";
	public static final String Field_OnlineDevices = "online_devices";
	public static final String Field_AccessTimes = "total_accesstimes";
	public static final String Field_Total = "total";
	
	//总设备数
	private long devices;
	//总用户数
	private long handsets;
	//当前在线用户数
	private long online_handsets;
	//当前在线设备数
	private long online_devices;
	//总平均访问时长 = access_times/total
	private long avg_accesstimes;
	//总访问时长
	private long total_accesstimes;
	private long total;
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getDevices() {
		return devices;
	}
	public void setDevices(long devices) {
		this.devices = devices;
	}
	public long getHandsets() {
		return handsets;
	}
	public void setHandsets(long handsets) {
		this.handsets = handsets;
	}
	public long getOnline_handsets() {
		return online_handsets;
	}
	public void setOnline_handsets(long online_handsets) {
		this.online_handsets = online_handsets;
	}
	public long getOnline_devices() {
		return online_devices;
	}
	public void setOnline_devices(long online_devices) {
		this.online_devices = online_devices;
	}
	public long getAvg_accesstimes() {
		return avg_accesstimes;
	}
	public void setAvg_accesstimes(long avg_accesstimes) {
		this.avg_accesstimes = avg_accesstimes;
	}
	public long getTotal_accesstimes() {
		return total_accesstimes;
	}
	public void setTotal_accesstimes(long total_accesstimes) {
		this.total_accesstimes = total_accesstimes;
	}
}
