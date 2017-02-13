package com.bhu.vas.api.helper;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DeviceCapability  implements Serializable {
	private String icon;
	private List<DeviceRadio> radio;
			
	public DeviceCapability(String icon){
		this.icon = icon;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public List<DeviceRadio> getRadio() {
		return radio;
	}
	public void setRadio(List<DeviceRadio> radio) {
		this.radio = radio;
	}
	
	public DeviceCapability() {
	}

}
