package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class SsidInfoDTO implements Serializable {

	@JsonInclude(Include.NON_NULL)
	private String bssid;
	@JsonInclude(Include.NON_NULL)
	private String ssid;
	@JsonInclude(Include.NON_NULL)
	private String capabilities;
	
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	
}
