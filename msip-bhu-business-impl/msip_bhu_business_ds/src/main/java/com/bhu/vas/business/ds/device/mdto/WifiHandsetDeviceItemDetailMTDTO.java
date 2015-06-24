package com.bhu.vas.business.ds.device.mdto;

import java.util.Date;

/**
 * Created by bluesand on 6/24/15.
 */
public class WifiHandsetDeviceItemDetailMTDTO {

    private String online;

    private Date last_login_at;

    private Date last_logout_at;

    private long online_time;


    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public long getOnline_time() {
        return online_time;
    }

    public void setOnline_time(long online_time) {
        this.online_time = online_time;
    }

    public Date getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(Date last_login_at) {
        this.last_login_at = last_login_at;
    }

    public Date getLast_logout_at() {
        return last_logout_at;
    }

    public void setLast_logout_at(Date last_logout_at) {
        this.last_logout_at = last_logout_at;
    }
}
