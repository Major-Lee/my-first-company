package com.bhu.vas.api.rpc.agent.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

import java.util.Date;

/**
 * Created by bluesand on 9/7/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceClaim extends BaseStringModel {
    /**
     * 设备sn号
     */
//    private String id;

    /**
     * 代理商用户ID
     */
    private int uid;

    /**
     * 设备mac地址
     */
    private String mac;

    /**
     * 存货编码
     */
    private String stock_code;
    /**
     * 存货名称
     */
    private String stock_name;

    /**
     * 0:导入 1:认领
     */
    private int status;

    /**
     * 导入批次id
     */
    private long import_id;

    /**
     * 设备售出日期
     */
    private Date sold_at;

    /**
     * 设备认领日期
     */
    private Date claim_at;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getStatus() {
        return status;
    }

    public String getStock_code() {
        return stock_code;
    }

    public void setStock_code(String stock_code) {
        this.stock_code = stock_code;
    }

    public String getStock_name() {
        return stock_name;
    }

    public void setStock_name(String stock_name) {
        this.stock_name = stock_name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getImport_id() {
        return import_id;
    }

    public void setImport_id(long import_id) {
        this.import_id = import_id;
    }

    public Date getSold_at() {
        return sold_at;
    }

    public void setSold_at(Date sold_at) {
        this.sold_at = sold_at;
    }

    public Date getClaim_at() {
        return claim_at;
    }

    public void setClaim_at(Date claim_at) {
        this.claim_at = claim_at;
    }

}
