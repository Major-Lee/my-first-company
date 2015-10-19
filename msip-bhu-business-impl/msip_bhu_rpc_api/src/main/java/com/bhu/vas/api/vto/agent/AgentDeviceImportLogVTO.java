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
    private int count;

    /**
     * 导入状态
     * @return
     */
    private int status;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
