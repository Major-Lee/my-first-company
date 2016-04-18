package com.bhu.vas.api.vto.config;

import java.io.Serializable;

@SuppressWarnings("serial")
public class URouterDeviceConfigRadioVTO implements Serializable{
	//信号强度
	private int power;
	//当前使用信道
	private int real_channel;
	
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getReal_channel() {
		return real_channel;
	}
	public void setReal_channel(int real_channel) {
		this.real_channel = real_channel;
	}
}