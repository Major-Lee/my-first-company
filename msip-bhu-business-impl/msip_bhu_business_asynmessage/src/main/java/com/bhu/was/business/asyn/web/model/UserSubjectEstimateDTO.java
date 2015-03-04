package com.bhu.was.business.asyn.web.model;

import com.bhu.was.business.asyn.web.builder.ActionDTO;
import com.bhu.was.business.asyn.web.builder.ActionMessageType;

public class UserSubjectEstimateDTO extends ActionDTO{
	private int sid;
	private String estimate;
	
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getEstimate() {
		return estimate;
	}
	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}
	@Override
	public String getActionType() {
		return ActionMessageType.USERSUBJECTESTIMATE.getPrefix();
	}
	
}
