package com.bhu.vas.api.vto.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bluesand on 9/11/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceClaimVTO implements Serializable {
    /**
     * 设备sn号
     */
    private String sn;

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
     * 设备售出日期
     */
    private Date sold_at;

    /**
     * 设备认领日期
     */
    private Date claim_at;

    /**
     * 设备位置
     */
    private String adr;

    /**
     * 是否在线
     */
    private boolean online;

    /**
     * 在线总时长
     */
    private String uptime;

    /**
     * 收入
     */
    private double month_income;

    private String osv;

    private double total_income;

    private Date created_at;

    private long hd_count;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

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

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public double getMonth_income() {
        return month_income;
    }

    public void setMonth_income(double month_income) {
        this.month_income = month_income;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = osv;
    }

    public double getTotal_income() {
        return total_income;
    }

    public void setTotal_income(double total_income) {
        this.total_income = total_income;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public long getHd_count() {
        return hd_count;
    }

    public void setHd_count(long hd_count) {
        this.hd_count = hd_count;
    }
}
