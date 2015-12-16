package com.bhu.vas.api.dto.qiniu;

public class CurrentKeys {
	private CurrentKey log;
	private CurrentKey avatar;
	public CurrentKey getLog() {
		return log;
	}
	public void setLog(CurrentKey log) {
		this.log = log;
	}
	public CurrentKey getAvatar() {
		return avatar;
	}
	public void setAvatar(CurrentKey avatar) {
		this.avatar = avatar;
	}
	
}
