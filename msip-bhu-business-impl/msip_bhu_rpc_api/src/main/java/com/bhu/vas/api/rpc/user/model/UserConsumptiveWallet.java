package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 用户消费的钱包
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class UserConsumptiveWallet extends BaseIntModel{// implements ISequenceGenable,TableSplitable<Integer>{
	
	public static final int Default_WalletUID_WhenUIDNotExist = 1;
	//现金零钱
	private long cash = 0;
	
	private Date created_at; 
	
	public long getCash() {
		return cash;
	}

	public void setCash(long cash) {
		this.cash = cash;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null) {
			this.created_at = new Date();
		}
		super.preInsert();
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
