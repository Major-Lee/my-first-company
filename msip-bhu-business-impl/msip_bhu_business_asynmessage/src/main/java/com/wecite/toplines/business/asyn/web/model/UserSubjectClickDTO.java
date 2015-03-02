package com.wecite.toplines.business.asyn.web.model;

import com.wecite.toplines.business.asyn.web.builder.ActionDTO;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageType;

public class UserSubjectClickDTO extends ActionDTO{
	private int sid;
	private String act;//up&down
	private long incr;
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}

	public long getIncr() {
		return incr;
	}
	public void setIncr(long incr) {
		this.incr = incr;
	}
	@Override
	public String getActionType() {
		return ActionMessageType.USERSUBJECTCLICK.getPrefix();
	}
	
}
