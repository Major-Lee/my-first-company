package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.bhu.vas.api.rpc.payment.dto.PaymentReckoningDTO;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentReckoning  extends BaseStringModel{

	private String order_id; 
	private int amount; 
	private String payment_type; 
	private String openid; 
	private String subject; 
	private String token; 
	private String appid; 
	private String exter_invoke_ip; 
	private String third_party_code; 
	private int pay_status; 
	private int notify_status; 
	private Date created_at;
	private Date notify_at; 
	private Date paid_at; 
	private int charge_user; 
	private int charge_merchant; 
	private int type; 
	private int tax; 
	private int balance; 
	private int tradeday; 
	private String channel_type;
	private String remark; 
	private String fee_type; 
	 
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
	public String getToken() {
		return token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	public String getExter_invoke_ip() {
		return exter_invoke_ip;
	}
	public void setExter_invoke_ip(String exter_invoke_ip) {
		this.exter_invoke_ip = exter_invoke_ip;
	}
	public void setToken(String token) {
		this.token = token;
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
	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}
	public int getNotify_status() {
		return notify_status;
	}
	public void setNotify_status(int notify_status) {
		this.notify_status = notify_status;
	}
	public int getCharge_user() {
		return charge_user;
	}
	public void setCharge_user(int charge_user) {
		this.charge_user = charge_user;
	}
	public int getCharge_merchant() {
		return charge_merchant;
	}
	public void setCharge_merchant(int charge_merchant) {
		this.charge_merchant = charge_merchant;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int getTradeday() {
		return tradeday;
	}
	public void setTradeday(int tradeday) {
		this.tradeday = tradeday;
	}
	public String getRemark(){ 
		return remark; 
	} 
	public void setRemark(String remark){ 
		this.remark = remark; 
	}
	
	public String getChannel_type() {
		return channel_type;
	}
	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getNotify_at() {
		return notify_at;
	}
	public void setNotify_at(Date notify_at) {
		this.notify_at = notify_at;
	}
	public Date getPaid_at() {
		return paid_at;
	}
	public void setPaid_at(Date paid_at) {
		this.paid_at = paid_at;
	}
	
	public String getFee_type() {
		return fee_type;
	}
	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
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
