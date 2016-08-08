package com.bhu.vas.api.rpc.user.model;


import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class UserIdentityAuth extends BaseStringModel {
	private String hdmac;
	private String create_at;
	

	public String getHdmac() {
		return hdmac;
	}

	public void setHdmac(String hdmac) {
		this.hdmac = hdmac;
	}
	
	public String getCreate_at() {
		return create_at;
	}

	public void setCreate_at(String create_at) {
		this.create_at = create_at;
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
