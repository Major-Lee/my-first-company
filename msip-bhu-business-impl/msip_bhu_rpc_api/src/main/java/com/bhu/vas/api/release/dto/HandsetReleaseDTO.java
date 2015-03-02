package com.bhu.vas.api.release.dto;


public class HandsetReleaseDTO{	
	private String start_date;//版本开始更新时间
	private String version;
	private String app_url;
	private String permit;
	private String change_log;
	private boolean force;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getApp_url() {
		return app_url;
	}
	public void setApp_url(String app_url) {
		this.app_url = app_url;
	}
	public String getPermit() {
		return permit;
	}
	public void setPermit(String permit) {
		this.permit = permit;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getChange_log() {
		return change_log;
	}
	public void setChange_log(String change_log) {
		this.change_log = change_log;
	}
	public boolean isForce() {
		return force;
	}
	public void setForce(boolean force) {
		this.force = force;
	}
}
