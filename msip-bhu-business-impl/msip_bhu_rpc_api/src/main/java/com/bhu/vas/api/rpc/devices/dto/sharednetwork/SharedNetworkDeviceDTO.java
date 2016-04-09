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
	//设备的原始设备型号
	private String d_origmodel;
	//设备类型
	private String d_type;
	//用户绑定的设备的昵称
	private String device_name;
	
	private String snk_type;
	private String template;
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
	public String getD_origmodel() {
		return d_origmodel;
	}
	public void setD_origmodel(String d_origmodel) {
		this.d_origmodel = d_origmodel;
	}
	public String getD_type() {
		return d_type;
	}
	public void setD_type(String d_type) {
		this.d_type = d_type;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
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
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
}
