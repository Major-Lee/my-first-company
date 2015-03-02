package com.wecite.toplines.business.asyn.web.model;

import com.wecite.toplines.business.asyn.web.builder.ActionDTO;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageType;

public class SubjectWeixinShareDTO extends ActionDTO{
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.SUBJECTWEIXINSHARE.getPrefix();
	}
	
}
