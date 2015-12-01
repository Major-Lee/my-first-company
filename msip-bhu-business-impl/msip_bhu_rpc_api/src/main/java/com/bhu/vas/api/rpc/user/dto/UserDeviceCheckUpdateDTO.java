package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 15/4/10.
 */
@SuppressWarnings("serial")
public class UserDeviceCheckUpdateDTO implements Serializable {
	private int uid;
	private String mac;
	private String dut;
	private int gray;
	
    //wifi设备是否在线
    private boolean online;
    private boolean forceDeviceUpdate;
    //currentDeviceVersionBuilder//
    private String currentDVB;
    private boolean forceAppUpdate;
    //currentAppVersionBuilder
    private String currentAVB;
    
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


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

	public boolean isForceDeviceUpdate() {
		return forceDeviceUpdate;
	}

	public void setForceDeviceUpdate(boolean forceDeviceUpdate) {
		this.forceDeviceUpdate = forceDeviceUpdate;
	}

	public boolean isForceAppUpdate() {
		return forceAppUpdate;
	}

	public void setForceAppUpdate(boolean forceAppUpdate) {
		this.forceAppUpdate = forceAppUpdate;
	}

	public String getDut() {
		return dut;
	}

	public void setDut(String dut) {
		this.dut = dut;
	}

	public int getGray() {
		return gray;
	}

	public void setGray(int gray) {
		this.gray = gray;
	}

	public String getCurrentDVB() {
		return currentDVB;
	}

	public void setCurrentDVB(String currentDVB) {
		this.currentDVB = currentDVB;
	}

	public String getCurrentAVB() {
		return currentAVB;
	}

	public void setCurrentAVB(String currentAVB) {
		this.currentAVB = currentAVB;
	}
    
}
