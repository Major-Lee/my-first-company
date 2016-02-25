package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 用户钱包
 * 包含 虚拟货币和现金两部分
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWallet extends BaseIntModel{// implements ISequenceGenable,TableSplitable<Integer>{
	//提现密码
	private String password;
	private String plainpwd;
	//虚拟币virtual_currency 充值生成（货币购买的）
	private Double vcurrency;
	//虚拟币virtual_currency 充值生成 绑定（系统或活动赠送的）
	private Double vcurrency_bing;
	//现金
	private Double cash;
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
	public Double getCash() {
		return cash;
	}
	public void setCash(Double cash) {
		this.cash = cash;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Double getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(Double vcurrency) {
		this.vcurrency = vcurrency;
	}
	public Double getVcurrency_bing() {
		return vcurrency_bing;
	}
	public void setVcurrency_bing(Double vcurrency_bing) {
		this.vcurrency_bing = vcurrency_bing;
	}
}
