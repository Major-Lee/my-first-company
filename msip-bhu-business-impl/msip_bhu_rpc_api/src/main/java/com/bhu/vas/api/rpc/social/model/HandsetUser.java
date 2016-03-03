package com.bhu.vas.api.rpc.social.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

import java.util.Date;

/**
 * Created by bluesand on 3/3/16.
 */
public class HandsetUser extends BaseStringModel {

    private long uid;

    private String nick;

    private Date created_at;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
