package com.bhu.vas.api.helper;


public class WithdrawCashHelper{
	/*private double cash;
	//交易手续费
	private double transcost;
	//交易税费
	private double taxcost;
	
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getTranscost() {
		return transcost;
	}
	public void setTranscost(double transcost) {
		this.transcost = transcost;
	}
	public double getTaxcost() {
		return taxcost;
	}
	public void setTaxcost(double taxcost) {
		this.taxcost = taxcost;
	}

	public double getRealCash(){
		return ArithHelper.sub(cash,ArithHelper.add(transcost, taxcost));
	}
	
	public String toString(){
		return String.format("withdraw detail:Cash[%s] RealCash[%s] TaxCost[%s] TransCost[%s]", cash,this.getRealCash(),taxcost,transcost);
	}*/
	
	/*public static WithdrawCashDetail build(double cash,double withdraw_tax_percent,double withdraw_trancost_percent){
		WithdrawCashDetail detail = new WithdrawCashDetail();
		detail.setCash(cash);
		if(cash > 0){
			detail.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			detail.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
		}
		return detail;
	}*/
	
}
