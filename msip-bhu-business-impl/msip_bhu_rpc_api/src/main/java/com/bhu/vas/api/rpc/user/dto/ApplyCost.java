package com.bhu.vas.api.rpc.user.dto;

import com.smartwork.msip.cores.helper.ArithHelper;

public class ApplyCost {
	//提现金额
	private double cash;
	//税费
	private double taxcost;
	//交易费
	private double transcost;
	
	//private double remain;
	
	public ApplyCost() {
	}
	public ApplyCost(double cash) {
		super();
		this.cash = cash;
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
	public double getRemain() {
		return ArithHelper.sub(cash,ArithHelper.add(transcost, taxcost));
	}
	
	public void calculate(double withdraw_tax_percent,double withdraw_trancost_percent){
		if(cash > 0){
			this.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			this.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
		}
	}
}
