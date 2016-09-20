package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentParameter extends BaseStringModel{
	private String name;
	private String value;
	private int status;
	private Date changed_at;
	private String remark;
	private String charge_rate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getChanged_at() {
		return changed_at;
	}

	public void setChanged_at(Date changed_at) {
		this.changed_at = changed_at;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCharge_rate() {
		return charge_rate;
	}

	public void setCharge_rate(String charge_rate) {
		this.charge_rate = charge_rate;
	}

	@Override
	public void preInsert() {
		if (this.changed_at == null)
			this.changed_at = new Date();
		
		super.preInsert();
	}
}
