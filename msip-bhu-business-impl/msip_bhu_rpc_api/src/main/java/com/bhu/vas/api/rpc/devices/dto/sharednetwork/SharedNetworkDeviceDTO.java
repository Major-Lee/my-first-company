package com.bhu.vas.api.rpc.devices.dto.sharednetwork;


/**
 * 共享网络的设备列表DTO
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class SharedNetworkDeviceDTO implements java.io.Serializable{
	//设备mac
	private String mac;
	//设备所在位置
	private String d_address;
	//设备的在线终端数量
	private long ohd_count;
	//设备的工作模式
	private String d_workmodel;
	
	private String snk_type;
	private boolean on;
	private boolean matched;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getD_address() {
		return d_address;
	}
	public void setD_address(String d_address) {
		this.d_address = d_address;
	}
	public long getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(long ohd_count) {
		this.ohd_count = ohd_count;
	}
	public String getD_workmodel() {
		return d_workmodel;
	}
	public void setD_workmodel(String d_workmodel) {
		this.d_workmodel = d_workmodel;
	}
	public String getSnk_type() {
		return snk_type;
	}
	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	public boolean isMatched() {
		return matched;
	}
	public void setMatched(boolean matched) {
		this.matched = matched;
	}
}
