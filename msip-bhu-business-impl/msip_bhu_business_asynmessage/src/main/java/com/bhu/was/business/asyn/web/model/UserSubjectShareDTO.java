package com.bhu.was.business.asyn.web.model;

import com.bhu.was.business.asyn.web.builder.ActionDTO;
import com.bhu.was.business.asyn.web.builder.ActionMessageType;

public class UserSubjectShareDTO extends ActionDTO{
	private int sid;
	private String custom_abstract;
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getCustom_abstract() {
		return custom_abstract;
	}

	public void setCustom_abstract(String custom_abstract) {
		this.custom_abstract = custom_abstract;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERSUBJECTSHARE.getPrefix();
	}
	
}
