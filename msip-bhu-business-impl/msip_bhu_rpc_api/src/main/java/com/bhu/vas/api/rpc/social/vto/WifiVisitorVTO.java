package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;

/**
 * Created by bluesand on 3/7/16.
 */
public class WifiVisitorVTO  implements Serializable{

    private long uid;

    private String n;

    private String avatar;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
