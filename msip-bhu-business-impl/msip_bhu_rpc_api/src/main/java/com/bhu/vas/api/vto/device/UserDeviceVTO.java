package com.bhu.vas.api.vto.device;

import java.io.Serializable;

/**
 * Created by bluesand on 1/25/16.
 */
public class UserDeviceVTO implements Serializable {

    private String d_mac;

    private String u_id;

    private String d_online;

    private String u_dnick;

    private String d_wanip;

    private String d_origmodel;

    private String d_origswver;


    public String getD_mac() {
        return d_mac;
    }

    public void setD_mac(String d_mac) {
        this.d_mac = d_mac;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getD_online() {
        return d_online;
    }

    public void setD_online(String d_online) {
        this.d_online = d_online;
    }

    public String getU_dnick() {
        return u_dnick;
    }

    public void setU_dnick(String u_dnick) {
        this.u_dnick = u_dnick;
    }

    public String getD_wanip() {
        return d_wanip;
    }

    public void setD_wanip(String d_wanip) {
        this.d_wanip = d_wanip;
    }

    public String getD_origmodel() {
        return d_origmodel;
    }

    public void setD_origmodel(String d_origmodel) {
        this.d_origmodel = d_origmodel;
    }

    public String getD_origswver() {
        return d_origswver;
    }

    public void setD_origswver(String d_origswver) {
        this.d_origswver = d_origswver;
    }
}
