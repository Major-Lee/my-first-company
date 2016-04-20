package com.bhu.vas.api.rpc.charging.model;

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
public class UserWithdrawCostConfigs extends BaseIntModel{
	public static final int Default_ConfigsID = -1;
	//扣除20%的税，3%的交易费用
	private double withdraw_tax_percent = 0.20d;
	//3%的交易费用
	private double withdraw_trancost_percent = 0.03d;
	
	public double getWithdraw_tax_percent() {
		return withdraw_tax_percent;
	}
	public void setWithdraw_tax_percent(double withdraw_tax_percent) {
		this.withdraw_tax_percent = withdraw_tax_percent;
	}
	public double getWithdraw_trancost_percent() {
		return withdraw_trancost_percent;
	}
	public void setWithdraw_trancost_percent(double withdraw_trancost_percent) {
		this.withdraw_trancost_percent = withdraw_trancost_percent;
	}
	
}
