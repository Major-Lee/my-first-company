package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

/**
 * 用户绑定的设备DTO
 * cloud
 */
@SuppressWarnings("serial")
public class UserDeviceCloudDTO implements Serializable {
	private int uid;
	//设备mac
    private String d_mac;
    //设备名称
    private String d_name;
    //设备在线状态 -1 从未上线 1 在线 0 离线
    private String d_online;
    //设备在线终端数量
    private long ohd_count;
    //设备的工作模式
    private String d_workmode;
    //设备的原始设备型号
    private String d_origmodel;
    
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getD_mac() {
		return d_mac;
	}
	public void setD_mac(String d_mac) {
		this.d_mac = d_mac;
	}
	public String getD_name() {
		return d_name;
	}
	public void setD_name(String d_name) {
		this.d_name = d_name;
	}
	public String getD_online() {
		return d_online;
	}
	public void setD_online(String d_online) {
		this.d_online = d_online;
	}
	public long getOhd_count() {
		return ohd_count;
	}
	public void setOhd_count(long ohd_count) {
		this.ohd_count = ohd_count;
	}
	public String getD_workmode() {
		return d_workmode;
	}
	public void setD_workmode(String d_workmode) {
		this.d_workmode = d_workmode;
	}
	public String getD_origmodel() {
		return d_origmodel;
	}
	public void setD_origmodel(String d_origmodel) {
		this.d_origmodel = d_origmodel;
	}

}
