package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UserDeviceSharedNetworkApplyDTO extends ActionDTO implements IDTO {
	private String snk_type;
	@JsonInclude(Include.NON_NULL)
	private List<String> macs;
	private char dtoType;
	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}

	public String getSnk_type() {
		return snk_type;
	}

	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}

	
	@Override
	public String getActionType() {
		return ActionMessageType.UserDeviceSharedNetworkApply.getPrefix();
	}

	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}
}
