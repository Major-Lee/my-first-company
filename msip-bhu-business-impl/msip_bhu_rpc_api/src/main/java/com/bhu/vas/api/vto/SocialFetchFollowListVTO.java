package com.bhu.vas.api.vto;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;
import com.bhu.vas.api.rpc.social.vto.SocialUserVTO;

import java.io.Serializable;


/**
 * Created by NeeBie_xw on 2016/3/7.
 */
public class SocialFetchFollowListVTO implements Serializable{

    public static final String TYPE = "urooter";

    private String hd_mac;
    private SocialUserVTO SUser;
    private HandsetMeetDTO last_meet;
    private String type;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public SocialUserVTO getSUser() {
        return SUser;
    }

    public void setSUser(SocialUserVTO SUser) {
        this.SUser = SUser;
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
