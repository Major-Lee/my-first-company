package com.bhu.vas.api.mdto;

import java.io.Serializable;

/**
 * Created by bluesand on 6/24/15.
 */
public class WifiHandsetDeviceItemDetailMDTO implements Serializable{

    private long login_at;

    private long logout_at;

    public long getLogin_at() {
        return login_at;
    }

    public void setLogin_at(long login_at) {
        this.login_at = login_at;
    }

    public long getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(long logout_at) {
        this.logout_at = logout_at;
    }

}
