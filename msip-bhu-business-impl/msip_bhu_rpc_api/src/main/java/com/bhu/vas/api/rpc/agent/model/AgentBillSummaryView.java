package com.bhu.vas.api.rpc.agent.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 代理商 单据数据汇总表
 * 一个代理商一条记录 存储 需要结算的数据总数 和 已经 结算的总数
 * 此表数据是由 AgentBillSettlements 计算出来的
 * 结算过程中 结算的是AgentBillSettlements的记录，然后重新算出新的Summary并更新此表
 * @author Edmond
 */
@SuppressWarnings("serial")
public class AgentBillSummaryView extends BaseIntModel{
	public static final int SummaryView_Empty = -1;//代理商本身没有存在单据，但是AgentBillSummaryView仍然存在代理商的汇总记录，其状态为-1
	public static final int SummaryView_UnSettled = 0;
	public static final int SummaryView_Settled = 1;
	// id agent

	//冗余字段 存储 分销商 org字段内容 用于支持org排序
	private String org;
	//最后结算人
    private int last_reckoner;
    //Summary需要结算的金额
    private double t_price;
    //Summary中已经结算的金额 <= t_price
    private double sd_t_price;
    private int status;
    //结算操作的日期
    private String settled_at;
    /**
     * 创建日期
     */
    private Date created_at;

	public int getLast_reckoner() {
		return last_reckoner;
	}

	public void setLast_reckoner(int last_reckoner) {
		this.last_reckoner = last_reckoner;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getT_price() {
		return t_price;
	}

	public void setT_price(double t_price) {
		this.t_price = t_price;
	}

	public double getSd_t_price() {
		return sd_t_price;
	}

	public void setSd_t_price(double sd_t_price) {
		this.sd_t_price = sd_t_price;
	}

	public String getSettled_at() {
		return settled_at;
	}

	public void setSettled_at(String settled_at) {
		this.settled_at = settled_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	public static AgentBillSummaryView buildDefault(int user,String org){
		AgentBillSummaryView sview = new AgentBillSummaryView();
		sview.setId(user);
		sview.setOrg(org);
		sview.setStatus(SummaryView_Empty);
		sview.setT_price(0.00d);
		sview.setSd_t_price(0.00d);
		return sview;
	}
}
