package com.bhu.vas.api.vto.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bluesand on 9/15/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceImportLogVTO implements Serializable {

    private long id;
    /**
     * 代理商id
     */
    private int aid;

    /**
     * 销售id
     */
    private int wid;

    /**
     * 公告id
     */
    private long bid;

    /**
     * 代理商名称
     */
    private String nick;

    /**
     * 销售名称
     */
    private String wnick;

    /**
     * 创建日期
     */
    private Date created_at;

    /**
     * 导入数量
     */
    private int scount;

    /**
     * 失败数量
     */
    private int fcount;

    /**
     * 导入状态
     * @return
     */
    private int status;


    private String filename;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getWnick() {
        return wnick;
    }

    public void setWnick(String wnick) {
        this.wnick = wnick;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getScount() {
        return scount;
    }

    public void setScount(int scount) {
        this.scount = scount;
    }

    public int getFcount() {
        return fcount;
    }

    public void setFcount(int fcount) {
        this.fcount = fcount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
