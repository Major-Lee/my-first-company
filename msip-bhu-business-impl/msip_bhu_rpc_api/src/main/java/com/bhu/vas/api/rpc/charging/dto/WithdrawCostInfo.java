package com.bhu.vas.api.rpc.charging.dto;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
public class WithdrawCostInfo {
	private int uid;
	private String applyid;
	//提现金额
	private double cash;
	//税费
	private double taxcost;
	//交易费
	private double transcost;

	private double remain;
	public WithdrawCostInfo(int uid,String applyid,double cash) {
		super();
		this.uid = uid;
		this.applyid = applyid;
		this.cash = cash;
	}
	
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
	//余额 剩余金额
	/*public double getRemain() {
		return ArithHelper.sub(cash,ArithHelper.add(transcost, taxcost));
	}*/
	
	public static WithdrawCostInfo calculate(int uid,String applyid,double cash,
			double withdraw_tax_percent,double withdraw_trancost_percent){
		WithdrawCostInfo costInfo= new WithdrawCostInfo(uid,applyid,cash);
		if(cash > 0){
			costInfo.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			costInfo.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
			costInfo.setRemain( ArithHelper.sub(cash,ArithHelper.add(costInfo.getTaxcost(), costInfo.getTranscost())));
		}
		return costInfo;
	}

	public double getRemain() {
		return remain;
	}

	public void setRemain(double remain) {
		this.remain = remain;
	}
}
