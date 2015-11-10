package com.bhu.vas.api.rpc.agent.model;

import java.util.Date;

import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * 代理商 每月单据流水表
 * @author Edmond
 */
@SuppressWarnings("serial")
public class AgentBillSettlements extends BaseStringModel{
	public static final int Bill_Created = 0;
	public static final int Bill_Parted = 1;
	public static final int Bill_Done = 2;
	// id generateId(date,agent) date(yyyy-MM) agent(uid)
    private int agent;
    private String date;
    //结算人
    private int reckoner;
    //此单据中需要结算的金额
    private double iSVPrice;
    //此单据中已经结算的金额 <= iSVPrice
    private double sdPrice;
    //单据结算状态
    private int status = Bill_Created;
    
    //结算操作的日期
    private String settled_at;
    /**
     * 创建日期
     */
    private Date created_at;

    public int getAgent() {
		return agent;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getReckoner() {
		return reckoner;
	}

	public void setReckoner(int reckoner) {
		this.reckoner = reckoner;
	}

	public double getiSVPrice() {
		return iSVPrice;
	}

	public void setiSVPrice(double iSVPrice) {
		this.iSVPrice = iSVPrice;
	}

	public double getSdPrice() {
		return sdPrice;
	}

	public void setSdPrice(double sdPrice) {
		this.sdPrice = sdPrice;
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

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static String generateId(String date, int agent){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(agent);
		return idstring.toString();
	}
}
