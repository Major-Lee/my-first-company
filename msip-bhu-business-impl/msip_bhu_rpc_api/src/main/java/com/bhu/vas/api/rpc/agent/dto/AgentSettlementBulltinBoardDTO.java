package com.bhu.vas.api.rpc.agent.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 11/10/15.
 */
public class AgentSettlementBulltinBoardDTO implements Serializable{

    /**
     * 代理商id
     */
    private int aid;

    /**
     * 结算金额
     */
    private double amount;

    /**
     * 发票图片
     */
    private String invoice;

    /**
     * 收据图片
     */
    private String receipt;

    /**
     * 备注
     */
    private String remark;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
