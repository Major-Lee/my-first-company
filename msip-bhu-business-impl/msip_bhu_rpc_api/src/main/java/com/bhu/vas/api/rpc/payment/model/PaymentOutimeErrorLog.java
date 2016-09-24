package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentOutimeErrorLog  extends BaseStringModel{

	private String order_id; 
	private int ot; 
	private String c_type; 
	private Date created_at;
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
	public int getOt() {
		return ot;
	}
	public void setOt(int ot) {
		this.ot = ot;
	}
	
	public String getC_type() {
		return c_type;
	}
	public void setC_type(String c_type) {
		this.c_type = c_type;
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
