package com.smartwork.async.messagequeue.model;

import com.smartwork.async.messagequeue.builder.PayloadDTO;
import com.smartwork.async.messagequeue.type.MessageType;

public class UserCreateDTO extends PayloadDTO{

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String payloadType() {
		return MessageType.UserCreate.getPrefix();
	}
}
