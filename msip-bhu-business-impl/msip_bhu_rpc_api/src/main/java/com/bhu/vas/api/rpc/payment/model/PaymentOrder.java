package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class PaymentOrder extends BaseLongModel{
	private String tid;
	private String gid;
	private float price;
	private int num;
	private String payment_type;
	private String openid;
	private String subject;
	private String exter_invoke_ip;
	private String appid;
	private String token;
	private String billno;
	private String others;
	private Date created_at;
	private Date updated_at;    
	private int pay_status;    
	private int notify_status; 

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



	public float getPrice() {
		return price;
	}



	public void setPrice(float price) {
		this.price = price;
	}



	public int getNum() {
		return num;
	}



	public void setNum(int num) {
		this.num = num;
	}



	public String getPayment_type() {
		return payment_type;
	}



	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}



	public String getOpenid() {
		return openid;
	}



	public void setOpenid(String openid) {
		this.openid = openid;
	}



	public String getSubject() {
		return subject;
	}



	public void setSubject(String subject) {
		this.subject = subject;
	}



	public String getExter_invoke_ip() {
		return exter_invoke_ip;
	}



	public void setExter_invoke_ip(String exter_invoke_ip) {
		this.exter_invoke_ip = exter_invoke_ip;
	}



	public String getAppid() {
		return appid;
	}



	public void setAppid(String appid) {
		this.appid = appid;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public String getBillno() {
		return billno;
	}



	public void setBillno(String billno) {
		this.billno = billno;
	}



	public String getOthers() {
		return others;
	}



	public void setOthers(String others) {
		this.others = others;
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



	public int getPay_status() {
		return pay_status;
	}



	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}



	public int getNotify_status() {
		return notify_status;
	}



	public void setNotify_status(int notify_status) {
		this.notify_status = notify_status;
	}



	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
}
