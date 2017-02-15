package com.bhu.vas.api.rpc.user.model;


import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class UserIdentityAuth extends BaseStringModel {
	
	
	public static final String[] dirtyMacs ={"00:00:00:00:00:00"};
	public static final int countrycode = 86;
	private String mobileno;
	private Date created_at;
	private boolean isAuthorize;
	private Integer uid;



	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public boolean isAuthorize() {
		return isAuthorize;
	}

	public void setAuthorize(boolean isAuthorize) {
		this.isAuthorize = isAuthorize;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
}
