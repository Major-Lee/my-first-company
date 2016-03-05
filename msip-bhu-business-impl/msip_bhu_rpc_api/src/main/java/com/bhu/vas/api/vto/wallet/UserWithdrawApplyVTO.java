package com.bhu.vas.api.vto.wallet;

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
	private String mobileno;
	private String nick;
	private double cash = 0.00d;
	//当前提现申请的状态
	private String withdraw_oper;
	
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
	
}
