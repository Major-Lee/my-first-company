package com.smartwork.im.message;

@Deprecated
public class GroupMessage extends Message{
	//群id
	private String g;
	//群name
	private String gn;
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g;
	}
	public String getGn() {
		return gn;
	}
	public void setGn(String gn) {
		this.gn = gn;
	}
}
