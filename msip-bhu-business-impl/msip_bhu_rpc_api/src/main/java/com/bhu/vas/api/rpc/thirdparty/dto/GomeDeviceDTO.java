package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class GomeDeviceDTO implements Serializable {
	@JsonInclude(Include.NON_NULL)
	private String online;

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}
	
	
}
