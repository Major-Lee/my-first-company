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
    //设备sn
    private String d_sn;
	//设备的hdtype
	private String d_type;
    //设备在线状态 -1 从未上线 1 在线 0 离线
    private String d_online;
    //设备在线终端数量
    private long ohd_count;
    //设备的工作模式
    private String d_workmode;
    //设备的原始设备型号
    private String d_origmodel;
    //设备的软件版本号
    private String d_origswver;
    //设备的共享网络类型
    private String d_snk_type;
    //设备的业务线
    private String d_dut;
    //设备所在位置
    private String d_address;
    //设备上网方式
    private String link_mode_type;
    private int d_snk_allowturnoff;
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
	public String getD_type() {
		return d_type;
	}
	public void setD_type(String d_type) {
		this.d_type = d_type;
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
	public String getD_origswver() {
		return d_origswver;
	}
	public void setD_origswver(String d_origswver) {
		this.d_origswver = d_origswver;
	}
	public String getD_snk_type() {
		return d_snk_type;
	}
	public void setD_snk_type(String d_snk_type) {
		this.d_snk_type = d_snk_type;
	}
	public String getD_dut() {
		return d_dut;
	}
	public void setD_dut(String d_dut) {
		this.d_dut = d_dut;
	}
	public String getLink_mode_type() {
		return link_mode_type;
	}
	public void setLink_mode_type(String link_mode_type) {
		this.link_mode_type = link_mode_type;
	}
	public String getD_sn() {
		return d_sn;
	}
	public void setD_sn(String d_sn) {
		this.d_sn = d_sn;
	}
	public String getD_address() {
		return d_address;
	}
	public void setD_address(String d_address) {
		this.d_address = d_address;
	}
	public int getD_snk_allowturnoff() {
		return d_snk_allowturnoff;
	}
	public void setD_snk_allowturnoff(int d_snk_allowturnoff) {
		this.d_snk_allowturnoff = d_snk_allowturnoff;
	}
}
