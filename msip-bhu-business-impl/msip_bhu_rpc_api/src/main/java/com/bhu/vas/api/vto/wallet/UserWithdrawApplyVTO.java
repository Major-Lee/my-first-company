package com.bhu.vas.api.vto.wallet;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWithdrawApplyVTO implements java.io.Serializable {
	private String applyid;
	private int uid;
	private int appid;
	private String payment_type;
	private String mobileno;
	private String nick;
	private double cash = 0.00d;
	//当前提现申请的状态
	private String withdraw_oper;
	//交易手续费
	private double transcost;
	//交易税费
	private double taxcost;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public String getWithdraw_oper() {
		return withdraw_oper;
	}
	public void setWithdraw_oper(String withdraw_oper) {
		this.withdraw_oper = withdraw_oper;
	}
	public String getApplyid() {
		return applyid;
	}
	public void setApplyid(String applyid) {
		this.applyid = applyid;
	}
	public int getAppid() {
		return appid;
	}
	public void setAppid(int appid) {
		this.appid = appid;
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

	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public double getRealCash(){
		return ArithHelper.sub(cash,ArithHelper.add(transcost, taxcost));
	}
	
	public void calculate(double withdraw_tax_percent,double withdraw_trancost_percent){
		if(cash > 0){
			this.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			this.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
		}
	}
}
