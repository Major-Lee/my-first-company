package com.bhu.vas.api.vto.device;

import java.io.Serializable;

/**
 * Created by bluesand on 1/25/16.
 *
 * 参照 com.bhu.vas.business.search.model.WifiDeviceDocument注释
 */
@SuppressWarnings("serial")
public class UserDeviceVTO implements Serializable {

    private String d_mac;

    private String u_id;

    private String d_online;

    private String u_dnick;

    private String d_wanip;

    private String d_origmodel;

    private String d_origswver;

    private String d_workmodel;

    private String d_dut;

    private String d_type;

    private String d_power;

    private String d_channel;

    private String d_address;

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

    public String getD_workmodel() {
        return d_workmodel;
    }

    public void setD_workmodel(String d_workmodel) {
        this.d_workmodel = d_workmodel;
    }

    public String getD_dut() {
        return d_dut;
    }

    public void setD_dut(String d_dut) {
        this.d_dut = d_dut;
    }

    public String getD_type() {
        return d_type;
    }

    public void setD_type(String d_type) {
        this.d_type = d_type;
    }

    public String getD_power() {
        return d_power;
    }

    public void setD_power(String d_power) {
        this.d_power = d_power;
    }

    public String getD_channel() {
        return d_channel;
    }

    public void setD_channel(String d_channel) {
        this.d_channel = d_channel;
    }

    public String getD_address() {
        return d_address;
    }

    public void setD_address(String d_address) {
        this.d_address = d_address;
    }
}
