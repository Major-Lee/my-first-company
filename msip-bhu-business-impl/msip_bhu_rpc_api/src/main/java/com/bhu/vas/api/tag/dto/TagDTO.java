package com.bhu.vas.api.tag.dto;

public class TagDTO {
	private int id;//tag id
	private String n;//tag名称
	private int pid;//父级tag id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
}
