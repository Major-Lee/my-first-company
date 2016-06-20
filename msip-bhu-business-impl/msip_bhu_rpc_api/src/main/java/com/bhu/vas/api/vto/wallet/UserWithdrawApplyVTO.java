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
	private String withdraw_oper_desc;
	//交易手续费
	private double transcost;
	//交易税费
	private double taxcost;
	
	//add by dongrui 2016-06-17 start
	//审核人
	private int verify_reckoner;
	//操作人
	private int operate_reckoner;
	//对公账号备注
	private String note;
	//审核人名称
	private String verify_name;
	//操作人名称
	private String operate_name;
	//add by dongrui 2016-06-17 E N D
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
	
	public String getWithdraw_oper_desc() {
		return withdraw_oper_desc;
	}
	public void setWithdraw_oper_desc(String withdraw_oper_desc) {
		this.withdraw_oper_desc = withdraw_oper_desc;
	}
	/*public void calculate(double withdraw_tax_percent,double withdraw_trancost_percent){
		if(cash > 0){
			this.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			this.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
		}
	}*/
	public int getVerify_reckoner() {
		return verify_reckoner;
	}
	public void setVerify_reckoner(int verify_reckoner) {
		this.verify_reckoner = verify_reckoner;
	}
	public int getOperate_reckoner() {
		return operate_reckoner;
	}
	public void setOperate_reckoner(int operate_reckoner) {
		this.operate_reckoner = operate_reckoner;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getVerify_name() {
		return verify_name;
	}
	public void setVerify_name(String verify_name) {
		this.verify_name = verify_name;
	}
	public String getOperate_name() {
		return operate_name;
	}
	public void setOperate_name(String operate_name) {
		this.operate_name = operate_name;
	}
	
}
