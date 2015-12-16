package com.bhu.vas.api.dto.qiniu;

public class CurrentKey {
	//bucketName
	private String bn;
	private String ut;
	private String dt;
	public String getUt() {
		return ut;
	}
	public void setUt(String ut) {
		this.ut = ut;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getBn() {
		return bn;
	}
	public void setBn(String bn) {
		this.bn = bn;
	}
	
}
