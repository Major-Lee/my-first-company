package com.bhu.vas.api.rpc.payment.dto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
public class PaymentAlipaylocationVTO {
	private int uid;
	private String applyid;
	//提现金额
	private double cash;
	//税费
	private double taxcost;
	//交易费
	private double transcost;

	private double remain;
	
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
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getTaxcost() {
		return taxcost;
	}
	public void setTaxcost(double taxcost) {
		this.taxcost = taxcost;
	}
	public double getTranscost() {
		return transcost;
	}
	public void setTranscost(double transcost) {
		this.transcost = transcost;
	}
	public double getRemain() {
		return remain;
	}

	public void setRemain(double remain) {
		this.remain = remain;
	}
}
