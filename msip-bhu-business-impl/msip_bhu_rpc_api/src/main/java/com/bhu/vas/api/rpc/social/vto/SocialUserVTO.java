package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;

/**
 * Created by bluesand on 3/12/16.
 */
public class SocialUserVTO implements Serializable {

    private Long uid;

    private String nick;

    private String avatar;

    private String memo;

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
