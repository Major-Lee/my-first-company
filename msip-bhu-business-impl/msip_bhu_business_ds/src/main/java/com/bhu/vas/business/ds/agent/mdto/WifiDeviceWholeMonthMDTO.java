package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

@Document(collection = "t_wifi_device_wholemonth")
public class WifiDeviceWholeMonthMDTO {
	/**
	 * 规则为 yyyy-MM_mac
	 */
	@Id
	private String id;
	//yyyyMMdd
	private String date;
	private String mac;
	//当天在线时长
	private long onlinetime;
	//当天连接次数
	private int connecttimes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getOnlinetime() {
		return onlinetime;
	}
	public void setOnlinetime(long onlinetime) {
		this.onlinetime = onlinetime;
	}
	public int getConnecttimes() {
		return connecttimes;
	}
	public void setConnecttimes(int connecttimes) {
		this.connecttimes = connecttimes;
	}
	
	public static String generateId(String date, String mac){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(mac);
		return idstring.toString();
	}
}
