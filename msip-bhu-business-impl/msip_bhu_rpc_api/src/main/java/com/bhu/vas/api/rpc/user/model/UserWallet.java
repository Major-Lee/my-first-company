package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

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
	private double vcurrency = 0.00d;
	//虚拟币virtual_currency 充值生成 绑定（系统或活动赠送的）
	private double vcurrency_bing = 0.00d;;
	//现金零钱
	private double cash = 0.00d;;
	//提现状态 目前处于的提现状态 申请提现后，此状态为true 提现成功后此状态置为初始false
	private boolean withdraw = false;
	
	//约定的收益分成比例 最多小数点保留后两位
	//private double percent = 0.00d;
	private Date created_at;
	public UserWallet() {
		super();
	}
	public UserWallet(Integer userid){//,String email){
		super();
		this.id = userid;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
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
}
