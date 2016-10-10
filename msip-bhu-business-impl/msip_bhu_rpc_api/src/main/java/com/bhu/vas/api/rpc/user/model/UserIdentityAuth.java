package com.bhu.vas.api.rpc.user.model;


import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class UserIdentityAuth extends BaseStringModel {
	
	
	public static final String[] dirtyMacs ={"00:00:00:00:00:00"};
	public static final int countrycode = 86;
	private String mobileno;
	private String created_at;
	
	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	
	@Override
	public void preInsert() {
		super.preInsert();
	}
}
