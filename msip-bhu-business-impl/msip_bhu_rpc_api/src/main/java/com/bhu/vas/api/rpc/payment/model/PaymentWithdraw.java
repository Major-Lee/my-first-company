package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class PaymentWithdraw extends BaseLongModel {
	private String tid;
	private String wid;
	private String withdraw_type;
	private float price;
	private float transcost;
	private float taxcost;
	private String user_id;
	private String user_name;
	private String subject;
	private String appid;
	private String billno;
	private int withdraw_status;
	private int notify_status;
	private String fail_cause;
	private String exter_invoke_ip;
	private Date created_at;
	private Date updated_at;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getWithdraw_type() {
		return withdraw_type;
	}

	public void setWithdraw_type(String withdraw_type) {
		this.withdraw_type = withdraw_type;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getTranscost() {
		return transcost;
	}

	public void setTranscost(float transcost) {
		this.transcost = transcost;
	}

	public float getTaxcost() {
		return taxcost;
	}

	public void setTaxcost(float taxcost) {
		this.taxcost = taxcost;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public int getWithdraw_status() {
		return withdraw_status;
	}

	public void setWithdraw_status(int withdraw_status) {
		this.withdraw_status = withdraw_status;
	}

	public int getNotify_status() {
		return notify_status;
	}

	public void setNotify_status(int notify_status) {
		this.notify_status = notify_status;
	}

	public String getFail_cause() {
		return fail_cause;
	}

	public void setFail_cause(String fail_cause) {
		this.fail_cause = fail_cause;
	}

	public String getExter_invoke_ip() {
		return exter_invoke_ip;
	}

	public void setExter_invoke_ip(String exter_invoke_ip) {
		this.exter_invoke_ip = exter_invoke_ip;
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
