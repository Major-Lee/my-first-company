package com.bhu.vas.api.mdto;

import java.io.Serializable;

/**
 * Created by bluesand on 6/24/15.
 */
public class WifiHandsetDeviceItemDetailMDTO implements Serializable{

    private String online_time_ext;

    private String login_at;

    private String logout_at;

    private long online_time;

    public String getOnline_time_ext() {
        return online_time_ext;
    }

    public void setOnline_time_ext(String online_time_ext) {
        this.online_time_ext = online_time_ext;
    }

    public long getOnline_time() {
        return online_time;
    }

    public void setOnline_time(long online_time) {
        this.online_time = online_time;
    }

    public String getLogin_at() {
        return login_at;
    }

    public void setLogin_at(String login_at) {
        this.login_at = login_at;
    }

    public String getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(String logout_at) {
        this.logout_at = logout_at;
    }
}
