package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceTerminalNotifyDTO extends ActionDTO {
	private List<WifiDeviceTerminalDTO> terminals;
	
	public List<WifiDeviceTerminalDTO> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<WifiDeviceTerminalDTO> terminals) {
		this.terminals = terminals;
	}
	
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceTerminalNotify.getPrefix();
	}

}
