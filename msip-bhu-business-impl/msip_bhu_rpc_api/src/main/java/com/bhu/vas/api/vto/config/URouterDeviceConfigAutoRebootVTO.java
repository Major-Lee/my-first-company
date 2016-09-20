package com.bhu.vas.api.vto.config;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.vto.URouterInfoVTO;
import com.bhu.vas.api.vto.URouterModeVTO;

@SuppressWarnings("serial")
public class URouterDeviceConfigAutoRebootVTO implements Serializable{
	private String enable;
	private String time;
	
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
