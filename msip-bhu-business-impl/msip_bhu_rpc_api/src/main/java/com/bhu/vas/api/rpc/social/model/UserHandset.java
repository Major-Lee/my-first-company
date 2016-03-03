package com.bhu.vas.api.rpc.social.model;

import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

import java.util.Date;

/**
 * Created by bluesand on 3/3/16.
 */
@SuppressWarnings("serial")
public class UserHandset extends BasePKModel<UserHandsetPK> {

    private String nick;

    private Date created_at;

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

    public long getUid() {
        if (this.getId() == null) {
            return 0;
        }
        return this.getId().getUid();
    }

    public void setUid(long uid) {
        if (this.getId() == null) {
            this.setId(new UserHandsetPK());
        }
        this.getId().setUid(uid);
    }

    public String getHd_mac() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getHd_mac();
    }

    public void setHd_mac(String hd_mac) {
        if(this.getId() == null) {
            this.setId(new UserHandsetPK());
        }
        this.getId().setHd_mac(hd_mac);
    }


    @Override
    protected Class<UserHandsetPK> getPKClass() {
        return UserHandsetPK.class;
    }
}
