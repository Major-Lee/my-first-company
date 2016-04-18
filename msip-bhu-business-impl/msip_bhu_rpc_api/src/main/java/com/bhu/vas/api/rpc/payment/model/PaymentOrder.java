package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class PaymentOrder extends BaseLongModel{
	private String tid;
	private String gid;
	private Date created_at;
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
}
