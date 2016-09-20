package com.bhu.vas.api.rpc.payment.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class PaymentWithdraw extends BaseStringModel {
	private String orderId;
	private String withdrawType;
	private int amount;
	private int transcost;
	private int taxcost;
	private String userId;
	private String userName;
	private String subject;
	private String appid;
	private String thirdPartCode;
	private int withdrawStatus;
	private int notifyStatus;
	private String failCause;
	private String exterInvokeIp;
	private Date createdAt;
	private Date notifiedAt;
	private Date withdrawAt;


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}


	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getTranscost() {
		return transcost;
	}

	public void setTranscost(int transcost) {
		this.transcost = transcost;
	}

	public int getTaxcost() {
		return taxcost;
	}

	public void setTaxcost(int taxcost) {
		this.taxcost = taxcost;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getThirdPartCode() {
		return thirdPartCode;
	}

	public void setThirdPartCode(String thirdPartCode) {
		this.thirdPartCode = thirdPartCode;
	}

	public int getWithdrawStatus() {
		return withdrawStatus;
	}

	public void setWithdrawStatus(int withdrawStatus) {
		this.withdrawStatus = withdrawStatus;
	}

	public int getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(int notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public String getFailCause() {
		return failCause;
	}

	public void setFailCause(String failCause) {
		this.failCause = failCause;
	}

	public String getExterInvokeIp() {
		return exterInvokeIp;
	}

	public void setExterInvokeIp(String exterInvokeIp) {
		this.exterInvokeIp = exterInvokeIp;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(Date notifiedAt) {
		this.notifiedAt = notifiedAt;
	}

	public Date getWithdrawAt() {
		return withdrawAt;
	}

	public void setWithdrawAt(Date withdrawAt) {
		this.withdrawAt = withdrawAt;
	}
}
