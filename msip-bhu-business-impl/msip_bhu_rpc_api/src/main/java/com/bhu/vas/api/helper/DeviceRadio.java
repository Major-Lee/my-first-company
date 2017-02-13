package com.bhu.vas.api.helper;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DeviceRadio implements Serializable {
	private String name;
	private String mode;
	private String power_range;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getPower_range() {
		return power_range;
	}
	public void setPower_range(String power_range) {
		this.power_range = power_range;
	}
	
	public DeviceRadio(){
		
	}
}
