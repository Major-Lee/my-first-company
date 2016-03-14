package com.bhu.vas.api.rpc.social.vto;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;

import java.io.Serializable;

/**
 * Created by bluesand on 3/3/16.
 */
public class HandsetUserVTO implements Serializable{

    private String hd_mac;

    private String nick;

    private boolean followed;

    private HandsetMeetDTO last;

    private SocialUserVTO user;

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

    public SocialUserVTO getUser() {
        return user;
    }

    public void setUser(SocialUserVTO user) {
        this.user = user;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public HandsetMeetDTO getLast() {
        return last;
    }

    public void setLast(HandsetMeetDTO last) {
        this.last = last;
    }
}
