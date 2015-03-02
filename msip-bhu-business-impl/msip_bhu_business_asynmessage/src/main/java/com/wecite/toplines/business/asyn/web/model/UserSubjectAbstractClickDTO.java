package com.wecite.toplines.business.asyn.web.model;

import com.wecite.toplines.business.asyn.web.builder.ActionDTO;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageType;

public class UserSubjectAbstractClickDTO extends ActionDTO{
	private int aid;
	private String act;//up&down

	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERSUBJECTABSTRACTCLICK.getPrefix();
	}
	
}
