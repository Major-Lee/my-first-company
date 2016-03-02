package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 用户提现申请
 * 用户提取申发起之后金额会从钱包零钱中扣除，如果提现金额审核不通过或者提现失败会重新返还金额到钱包
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWithdrawApply extends BaseLongModel implements IRedisSequenceGenable {
	private int uid;
	//申请提现的现金
	private double cash;
	//提现操作状态BusinessEnumType.UWithdrawStatus
	private String withdraw_oper;
	private Date created_at;
	public UserWithdrawApply() {
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
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

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	public void setSequenceKey(Long key) {
		this.id = key;
	}
}
