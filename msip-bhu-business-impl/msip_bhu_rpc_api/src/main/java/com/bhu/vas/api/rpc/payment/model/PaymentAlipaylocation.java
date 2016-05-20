package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class PaymentAlipaylocation extends BaseLongModel {
	private String tid;
	private String location;
	private Date created_at;
	private Date updated_at;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
}
