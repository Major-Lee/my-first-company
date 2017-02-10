package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class GomeReportDTO implements Serializable {

	@JsonInclude(Include.NON_NULL)
	private String gid;
	@JsonInclude(Include.NON_NULL)
	private String deviceId;
	@JsonInclude(Include.NON_NULL)
	private String online;
	@JsonInclude(Include.NON_NULL)
	private String time;
	
	
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
