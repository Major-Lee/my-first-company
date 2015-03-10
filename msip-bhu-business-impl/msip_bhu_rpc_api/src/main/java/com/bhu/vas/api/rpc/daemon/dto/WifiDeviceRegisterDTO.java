package com.bhu.vas.api.rpc.daemon.dto;

import com.bhu.vas.api.rpc.devices.dto.WifiDeviceContextDTO;

@SuppressWarnings("serial")
public class WifiDeviceRegisterDTO implements java.io.Serializable{
	private WifiDeviceContextDTO contextDTO;
	private long t;
	
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	
	public WifiDeviceContextDTO getContextDTO() {
		return contextDTO;
	}
	public void setContextDTO(WifiDeviceContextDTO contextDTO) {
		this.contextDTO = contextDTO;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("WifiDeviceContextDTO:").append(contextDTO.toString()).append("   ").append(" t:").append(t);
		return sb.toString();
	}
}

