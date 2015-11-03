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

    private String ver;

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
}
