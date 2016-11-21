package com.bhu.vas.api.rpc.advertise.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtIntModel;


@SuppressWarnings("serial")
public class AdvertiseDevicesIncome extends ListJsonExtIntModel<String>{

	private String advertiseid;
	private long count;
	private int state;
	private int cash;
	private Date created_at;
	private Date updated_at;
	
	public String getAdvertiseid() {
		return advertiseid;
	}

	public void setAdvertiseid(String advertiseid) {
		this.advertiseid = advertiseid;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
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
	
	@Override
	public void preUpdate() {
		this.updated_at = new Date();
		super.preUpdate();
	}
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
}
