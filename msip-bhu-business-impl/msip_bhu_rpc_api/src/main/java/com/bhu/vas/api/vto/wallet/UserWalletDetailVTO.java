package com.bhu.vas.api.vto.wallet;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletDetailVTO implements java.io.Serializable {
	private int uid;
	private double vcurrency = 0.00d;
	private double vcurrency_bing = 0.00d;
	private double cash = 0.00d;;
	private boolean withdraw = false;
	private boolean haspwd = false; 
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
	public double getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(double vcurrency) {
		this.vcurrency = vcurrency;
	}
	public double getVcurrency_bing() {
		return vcurrency_bing;
	}
	public void setVcurrency_bing(double vcurrency_bing) {
		this.vcurrency_bing = vcurrency_bing;
	}
	public boolean isWithdraw() {
		return withdraw;
	}
	public void setWithdraw(boolean withdraw) {
		this.withdraw = withdraw;
	}
	public boolean isHaspwd() {
		return haspwd;
	}
	public void setHaspwd(boolean haspwd) {
		this.haspwd = haspwd;
	}
	public double getVcurrency_total(){
		return ArithHelper.add(vcurrency, vcurrency_bing);
	}
}
