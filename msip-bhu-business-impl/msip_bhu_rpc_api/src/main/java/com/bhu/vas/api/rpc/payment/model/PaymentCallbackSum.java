package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentCallbackSum  extends BaseStringModel{

	private int subtotal; 
	private Date changed_at;
	private String remark; 
	
	public int getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getChanged_at() {
		return changed_at;
	}
	public void setChanged_at(Date changed_at) {
		this.changed_at = changed_at;
	}
	@Override
	public void preInsert() {
		if (this.changed_at == null)
			this.changed_at = new Date();
		super.preInsert();
	}
}
