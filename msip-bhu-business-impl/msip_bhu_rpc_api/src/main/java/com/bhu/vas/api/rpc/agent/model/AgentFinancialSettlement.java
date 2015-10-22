package com.bhu.vas.api.rpc.agent.model;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

import java.util.Date;

/**
 * Created by bluesand on 10/21/15.
 */
@SuppressWarnings("serial")
public class AgentFinancialSettlement extends BaseLongModel implements IRedisSequenceGenable {


    /**
     * 财务操作人员
     */
    private int uid;

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
    private String invoice_fid;

    /**
     * 收据图片
     */
    private String receipt_fid;

    /**
     * 备注
     */
    private String remark;

    /**
     * 详细
     */
    private String detail;

    /**
     * 创建日期
     */
    private Date created_at;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

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

    public String getInvoice_fid() {
        return invoice_fid;
    }

    public void setInvoice_fid(String invoice_fid) {
        this.invoice_fid = invoice_fid;
    }

    public String getReceipt_fid() {
        return receipt_fid;
    }

    public void setReceipt_fid(String receipt_fid) {
        this.receipt_fid = receipt_fid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void preInsert() {
        if (this.created_at == null)
            this.created_at = new Date();
        super.preInsert();
    }

    @Override
    public void setSequenceKey(Long key) {
        this.id = key;
    }
}
