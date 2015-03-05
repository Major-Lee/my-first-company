package com.smartwork.im.message.u2u;

import java.util.List;

public class ChatResponseMessage {
	//context id
	private String cid;
	//ChatMessage json string
	private List<String> bodies;
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public List<String> getBodies() {
		return bodies;
	}
	public void setBodies(List<String> bodies) {
		this.bodies = bodies;
	}
}
