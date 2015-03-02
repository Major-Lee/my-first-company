package com.bhu.vas.api.logger.dto;

public class ActLoggerDTO {
	private int uid;
	private int t =1;
	private String payload;
	
	public static final int Type_Subject_UpAndDown = 1;
	public static final int Type_SubjectAbstract_UpAndDown = 2;
	public static final int Type_Share = 9;
	public static final String UP = "up";
	public static final String DOWN = "down";
	
	public static final String FROM_FRONTEND = "front";
	public static final String FROM_BACKEND = "back";
	public ActLoggerDTO() {
	}
	public ActLoggerDTO(int uid,int type) {
		super();
		this.uid = uid;
		this.t = type;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
