package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 15/4/10.
 */
@SuppressWarnings("serial")
public class UserDeviceDTO implements Serializable {
    private String mac;
    private int uid;
    private String device_name;
    //wifi设备是否在线
    private boolean online;

    private long ohd_count;

    private String work_mode;
    private String orig_model;
    
	//设备的固件版本号
    private String ver;

    private String add;

    private String ip;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getOhd_count() {
        return ohd_count;
    }

    public void setOhd_count(long ohd_count) {
        this.ohd_count = ohd_count;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getWork_mode() {
        return work_mode;
    }

    public void setWork_mode(String work_mode) {
        this.work_mode = work_mode;
    }

	public String getOrig_model() {
		return orig_model;
	}

	public void setOrig_model(String orig_model) {
		this.orig_model = orig_model;
	}

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
