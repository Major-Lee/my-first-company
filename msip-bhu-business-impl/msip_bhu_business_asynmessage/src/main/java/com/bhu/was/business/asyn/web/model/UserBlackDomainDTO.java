package com.bhu.was.business.asyn.web.model;

import com.bhu.was.business.asyn.web.builder.ActionDTO;
import com.bhu.was.business.asyn.web.builder.ActionMessageType;

public class UserBlackDomainDTO extends ActionDTO{
	private String url;//文章的url
	private String domain;
	private boolean add;//添加黑名单 或 取消黑名单
	private boolean single;//是否只对此文章进行黑名单处理 不处理其他同域名的文章
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERBLACKDOMAIN.getPrefix();
	}
	
}
