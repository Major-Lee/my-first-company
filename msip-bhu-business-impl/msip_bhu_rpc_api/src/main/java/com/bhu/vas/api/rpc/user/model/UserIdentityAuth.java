package com.bhu.vas.api.rpc.user.model;


import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class UserIdentityAuth extends BaseStringModel {
	private String hdmac;
	private String created_at;
	

	public String getHdmac() {
		return hdmac;
	}

	public void setHdmac(String hdmac) {
		this.hdmac = hdmac;
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
