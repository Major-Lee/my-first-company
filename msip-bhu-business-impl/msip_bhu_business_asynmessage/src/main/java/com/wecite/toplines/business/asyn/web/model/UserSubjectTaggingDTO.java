package com.wecite.toplines.business.asyn.web.model;

import java.util.Set;

import com.wecite.toplines.business.asyn.web.builder.ActionDTO;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageType;

public class UserSubjectTaggingDTO extends ActionDTO{
	private int sid;
	private Set<Integer> otags;//old tags
	private Set<Integer> ntags;//new tags
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}

	public Set<Integer> getOtags() {
		return otags;
	}
	public void setOtags(Set<Integer> otags) {
		this.otags = otags;
	}
	public Set<Integer> getNtags() {
		return ntags;
	}
	public void setNtags(Set<Integer> ntags) {
		this.ntags = ntags;
	}
	@Override
	public String getActionType() {
		return ActionMessageType.USERSUBJECTTAGGING.getPrefix();
	}
	
}
