package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.bhu.vas.api.rpc.payment.dto.PaymentReckoningDTO;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentOrderRel  extends BaseStringModel{

	private String order_id; 
	private int amount; 
	private String payment_type; 
	private String subject; 
	private String appid; 
	private String third_party_code; 
	private int pay_status; 
	private Date created_at;
	private String channel_no;
	private String channel_type;
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
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
	
	public String getThird_party_code() {
		return third_party_code;
	}
	public void setThird_party_code(String third_party_code) {
		this.third_party_code = third_party_code;
	}
	public int getPay_status() {
		return pay_status;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public String getChannel_no() {
		return channel_no;
	}
	public void setChannel_no(String channel_no) {
		this.channel_no = channel_no;
	}
	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}
	public String getChannel_type() {
		return channel_type;
	}
	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	public PaymentReckoningDTO toPaymentReckoningVTO(){
		PaymentReckoningDTO vto = new PaymentReckoningDTO();
		vto.setOrderId(order_id);
		vto.setReckoningId(id);
		return vto;
	}
}
