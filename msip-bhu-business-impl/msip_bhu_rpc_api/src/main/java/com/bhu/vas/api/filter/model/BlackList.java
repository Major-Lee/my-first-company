package com.bhu.vas.api.filter.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class BlackList extends BaseStringModel{
	private Date created_at;
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
