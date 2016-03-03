package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;

/**
 * Created by bluesand on 3/3/16.
 */
public class UserHandsetVTO implements Serializable{

    private String hd_mac;

    private String nick;

    private Long uid;

    private String avatar;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
