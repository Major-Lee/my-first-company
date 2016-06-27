package com.bhu.vas.api.vto.wallet;

import java.util.List;

import com.bhu.vas.api.rpc.user.vto.UserOAuthStateVTO;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletDetailVTO implements java.io.Serializable {
	private int uid;
	private long vcurrency = 0l;
	private long vcurrency_bing = 0l;
	private double cash = 0.00d;;
	private boolean withdraw = false;
	private boolean haspwd = false; 
	private String mobileNo;
	private List<UserOAuthStateVTO> payments;
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
	public void setVcurrency(long vcurrency) {
		this.vcurrency = vcurrency;
	}
	public double getVcurrency_bing() {
		return vcurrency_bing;
	}
	public void setVcurrency_bing(long vcurrency_bing) {
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
	public long getVcurrency_total(){
		return (vcurrency+vcurrency_bing);
	}
	public List<UserOAuthStateVTO> getPayments() {
		return payments;
	}
	public void setPayments(List<UserOAuthStateVTO> payments) {
		this.payments = payments;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
}
