package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiMultiCmdsNotifyDTO extends ActionDTO {
	
	private List<DownCmds> downCmds;

	@Override
	public String getActionType() {
		return ActionMessageType.WifiMultiCmdsDownNotify.getPrefix();
	}

	public List<DownCmds> getDownCmds() {
		return downCmds;
	}

	public void setDownCmds(List<DownCmds> downCmds) {
		this.downCmds = downCmds;
	}

}
