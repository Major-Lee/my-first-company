package com.bhu.vas.api.dto.statistics;

import java.util.Date;
/**
 * 用于统计新增、活跃设备/终端的模型
 * @author tangzichao
 *
 */
public class DeviceStatistics {
	public static final int Statis_Device_Type = 1;//设备
	public static final int Statis_HandsetDevice_Type = 2;//终端
	
	//mac
	private String mac;
	//上一次的登录时间
	private Date last_reged_at;
	//是否是新注册
	private boolean newed;
	
	public DeviceStatistics(){
		
	}
	
	public DeviceStatistics(String mac, boolean newed){
		this.mac = mac;
		this.newed = newed;
	}
	
	public DeviceStatistics(String mac, Date last_reged_at){
		this.mac = mac;
		this.last_reged_at = last_reged_at;
	}
	
	public DeviceStatistics(String mac, boolean newed, Date last_reged_at){
		this.mac = mac;
		this.newed = newed;
		this.last_reged_at = last_reged_at;
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public Date getLast_reged_at() {
		return last_reged_at;
	}
	public void setLast_reged_at(Date last_reged_at) {
		this.last_reged_at = last_reged_at;
	}
	public boolean isNewed() {
		return newed;
	}
	public void setNewed(boolean newed) {
		this.newed = newed;
	}
}
