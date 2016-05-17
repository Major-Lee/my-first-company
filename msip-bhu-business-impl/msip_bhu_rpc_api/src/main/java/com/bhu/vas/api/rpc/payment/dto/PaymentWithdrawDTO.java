package com.bhu.vas.api.rpc.payment.dto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
public class PaymentWithdrawDTO {
	private int uid;
	private String applyid;
	//提现金额
	private String cash;
	//税费
	private String taxcost;
	//交易费
	private String transcost;

	private String remain;
	
	public String getApplyid() {
		return applyid;
	}

	public void setApplyid(String applyid) {
		this.applyid = applyid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getTaxcost() {
		return taxcost;
	}
	public void setTaxcost(String taxcost) {
		this.taxcost = taxcost;
	}
	public String getTranscost() {
		return transcost;
	}
	public void setTranscost(String transcost) {
		this.transcost = transcost;
	}
	public String getRemain() {
		return remain;
	}

	public void setRemain(String remain) {
		this.remain = remain;
	}
}
