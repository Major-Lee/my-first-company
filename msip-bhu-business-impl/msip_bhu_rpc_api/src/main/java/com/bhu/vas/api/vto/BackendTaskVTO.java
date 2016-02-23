package com.bhu.vas.api.vto;

import java.util.Date;

@SuppressWarnings("serial")
public class BackendTaskVTO implements java.io.Serializable {

	private long id;

	private int uid;

	private String Message;

	private long total;

	private long current;
	
	private String state;

	private String description;
	
	private Date completed_at;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMessage() {
	    return Message;
	}

	public void setMessage(String message) {
	    Message = message;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCompleted_at() {
		return completed_at;
	}

	public void setCompleted_at(Date completed_at) {
		this.completed_at = completed_at;
	}

}
