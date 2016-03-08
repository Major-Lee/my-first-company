package com.bhu.vas.api.vto;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;

import java.io.Serializable;


/**
 * Created by NeeBie_xw on 2016/3/7.
 */
public class SocialFetchFollowListVTO implements Serializable{

    private String hd_mac;
    private long uid;
    private String avatar;
    private String nick;
    private HandsetMeetDTO last_meet;
    private String type;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public HandsetMeetDTO getLast_meet() {
        return last_meet;
    }

    public void setLast_meet(HandsetMeetDTO last_meet) {
        this.last_meet = last_meet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
