package com.bhu.vas.api.vto.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bluesand on 10/22/15.
 */
@SuppressWarnings("serial")
public class AgentFinancialSettlementVTO implements Serializable {

    /**
     * 财务操作人员
     */
    private int uid;

    /**
     *
     */
    private String name;

    /**
     * 代理商id
     */
    private int aid;

    private String aname;

    /**
     *代理商公司名称
     */
    private String org;

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

//    /**
//     * 详细
//     */
//    private String detail;

    /**
     * 创建日期
     */
    private Date updated_at;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
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

//    public String getDetail() {
//        return detail;
//    }
//
//    public void setDetail(String detail) {
//        this.detail = detail;
//    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
