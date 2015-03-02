package com.bhu.vas.api.subject.dto;

public class SubjectShareResponseDTO {
	private boolean success;
	private int fid;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
}
