package com.smartwork.async.messagequeue.model;

import com.smartwork.async.messagequeue.builder.PayloadDTO;
import com.smartwork.async.messagequeue.type.MessageType;

public class UserOnlineDTO extends PayloadDTO{

	private String name;
	private String area;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Override
	public String payloadType() {
		return MessageType.UserOnline.getPrefix();
	}
}
