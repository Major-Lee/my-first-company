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
     * 0:导入 1:认领
     */
    private int status;

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

    public void setStatus(int status) {
        this.status = status;
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
