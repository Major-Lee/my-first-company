package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiCmdsNotifyDTO extends ActionDTO {
	
	private List<String> payloads;

	@Override
	public String getActionType() {
		return ActionMessageType.WifiCmdsDownNotify.getPrefix();
	}

	public List<String> getPayloads() {
		return payloads;
	}

	public void setPayloads(List<String> payloads) {
		this.payloads = payloads;
	}

}
