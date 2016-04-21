package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;

/**
 * Created by bluesand on 3/7/16.
 */
@SuppressWarnings("serial")
public class WifiVisitorVTO  implements Serializable{

    private long uid;

    private String nick;

    private String avatar;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
