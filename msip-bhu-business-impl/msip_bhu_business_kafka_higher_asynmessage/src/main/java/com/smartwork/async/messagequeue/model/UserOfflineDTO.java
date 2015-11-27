package com.smartwork.async.messagequeue.model;

import com.smartwork.async.messagequeue.builder.PayloadDTO;
import com.smartwork.async.messagequeue.type.MessageType;

public class UserOfflineDTO extends PayloadDTO{

	private String name;
	private boolean kick;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isKick() {
		return kick;
	}

	public void setKick(boolean kick) {
		this.kick = kick;
	}

	@Override
	public String payloadType() {
		return MessageType.UserOnline.getPrefix();
	}
}
