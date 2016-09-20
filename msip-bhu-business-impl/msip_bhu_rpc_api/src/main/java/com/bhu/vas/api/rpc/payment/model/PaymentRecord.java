package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentRecord extends BaseStringModel{
	private int amount;
	private int count;
	private int dx_count;
	private int video_count;
	private String info;
	private Date created_at;
	private Date updated_at;


	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getVideo_count() {
		return video_count;
	}

	public void setVideo_count(int video_count) {
		this.video_count = video_count;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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
	
	public int getDx_count() {
		return dx_count;
	}

	public void setDx_count(int dx_count) {
		this.dx_count = dx_count;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		if (this.updated_at == null)
			this.updated_at = new Date();
		super.preInsert();
	}
}
