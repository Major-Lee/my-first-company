package com.bhu.vas.api.dto.search.increment;

@SuppressWarnings("serial")
public abstract class IncrementDocumentDTO implements java.io.Serializable{
	//增量索引事件
	private String action;
	//索引库唯一id
	private int uniqueid;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(int uniqueid) {
		this.uniqueid = uniqueid;
	}
	
	public abstract String getPrefix();
}
