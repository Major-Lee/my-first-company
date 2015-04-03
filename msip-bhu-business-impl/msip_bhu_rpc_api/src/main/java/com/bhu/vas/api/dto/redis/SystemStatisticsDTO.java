package com.bhu.vas.api.dto.redis;

import java.io.Serializable;

/**
 * 系统统计数据DTO
 * 1:总wifi设备数
 * 2:总移动设备数
 * 3:在线wifi设备数
 * 4:在线移动设备数
 * 5:总移动设备接入次数
 * 6:总移动设备访问时长
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SystemStatisticsDTO implements Serializable{
	
	public static final String Field_Devices = "devices";//1:总wifi设备数
	public static final String Field_Handsets = "handsets";//2:总移动设备数
	public static final String Field_OnlineDevices = "online_devices";//3:在线wifi设备数
	public static final String Field_OnlineHandsets = "online_handsets";//4:在线移动设备数
	public static final String Field_Handset_AccessCount = "handset_accesscount";//5:总移动设备接入次数
	public static final String Field_Handset_Duration = "handset_duration";//6:总移动设备访问时长
	
	//1:总wifi设备数
	private long devices;
	//2:总移动设备数
	private long handsets;
	//3:在线wifi设备数
	private long online_devices;
	//4:在线移动设备数
	private long online_handsets;
	//5:总移动设备接入次数
	private long handset_accesscount;
	//6:总移动设备访问时长
	private String handset_duration;
	
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
	public long getOnline_devices() {
		return online_devices;
	}
	public void setOnline_devices(long online_devices) {
		this.online_devices = online_devices;
	}
	public long getOnline_handsets() {
		return online_handsets;
	}
	public void setOnline_handsets(long online_handsets) {
		this.online_handsets = online_handsets;
	}
	public long getHandset_accesscount() {
		return handset_accesscount;
	}
	public void setHandset_accesscount(long handset_accesscount) {
		this.handset_accesscount = handset_accesscount;
	}
	public String getHandset_duration() {
		return handset_duration;
	}
	public void setHandset_duration(String handset_duration) {
		this.handset_duration = handset_duration;
	}
	
}
