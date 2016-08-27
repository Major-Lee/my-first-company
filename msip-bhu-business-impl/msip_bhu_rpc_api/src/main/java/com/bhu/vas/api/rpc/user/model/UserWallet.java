package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 用户钱包
 * 包含 虚拟货币和现金两部分
 * 每次提现额度最大值限制 可配置，在BusinessRuntimeConfiguration配置
 * withdraw_status 如果为true的情况下，不允许进行任何交易，包括再次提现操作
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWallet extends BaseIntModel{// implements ISequenceGenable,TableSplitable<Integer>{
	
	public static final int Default_WalletUID_WhenUIDNotExist = 1;
	//提现密码
	private String password;
	private String plainpwd;
	//虚拟币virtual_currency 充值生成（货币购买的）
	private long vcurrency = 0l;
	//虚拟币virtual_currency 充值生成 绑定（系统或活动赠送的）
	private long vcurrency_bing = 0l;
	//现金零钱
	private double cash = 0.00d;
	//提现状态 目前处于的提现状态 申请提现后，此状态为true 提现成功后此状态置为初始false
	private boolean withdraw = false;
	
	//约定的收益分成比例 最多小数点保留后两位
	//private double percent = 0.00d;
	private Date created_at; 
	public String mobileNo;
	
	// 今日收益
	private double today_cash_sum = 0.00d;
	// 更新时间
	private Date last_update_cash_time;
	// 累计收益
	private double total_cash_sum;
	
	@Override
	public void preInsert() {
		if (this.created_at == null) {
			this.created_at = new Date();
		}
		
		if (this.last_update_cash_time == null) {
			this.last_update_cash_time = new Date();
		}
		
		if(StringUtils.isNotEmpty(this.plainpwd) && StringUtils.isEmpty(this.password))
			this.password = BCryptHelper.hashpw(plainpwd, BCryptHelper.gensalt());//DigestHelper.md5ToHex(this.plainpwd);
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		if(StringUtils.isNotEmpty(this.plainpwd) && StringUtils.isEmpty(this.password))
			this.password = BCryptHelper.hashpw(plainpwd, BCryptHelper.gensalt());//DigestHelper.md5ToHex(this.plainpwd);
		super.preUpdate();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPlainpwd() {
		return plainpwd;
	}
	public void setPlainpwd(String plainpwd) {
		this.plainpwd = plainpwd;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	/*public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}*/
	public long getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(long vcurrency) {
		this.vcurrency = vcurrency;
	}
	public long getVcurrency_bing() {
		return vcurrency_bing;
	}
	public void setVcurrency_bing(long vcurrency_bing) {
		this.vcurrency_bing = vcurrency_bing;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public boolean isWithdraw() {
		return withdraw;
	}
	public void setWithdraw(boolean withdraw) {
		this.withdraw = withdraw;
	}
	
	public String toString(){
		return String.format("Wallet uid[%s] cash[%s] vcurrency[%s %s] withdraw[%s]", id,cash,vcurrency,vcurrency_bing,withdraw);
	}
	
	public UserWalletDetailVTO toUserWalletDetailVTO(){
		UserWalletDetailVTO detail = new UserWalletDetailVTO();
		detail.setUid(id);
		detail.setCash(cash);
		detail.setVcurrency(vcurrency);
		detail.setVcurrency_bing(vcurrency_bing);
		detail.setWithdraw(withdraw);
		detail.setHaspwd(StringUtils.isNotEmpty(password));
		return detail;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public double getToday_cash_sum() {
		// 判断时间是否是今日收益, 如果不是现实的时候显示为0
		if (!DateTimeHelper.isSameDay(last_update_cash_time, new Date())) {
			today_cash_sum = 0.00d;
		}
		return today_cash_sum;
	}

	public Date getLast_update_cash_time() {
		return last_update_cash_time;
	}

	public double getTotal_cash_sum() {
		return total_cash_sum;
	}
    
}
