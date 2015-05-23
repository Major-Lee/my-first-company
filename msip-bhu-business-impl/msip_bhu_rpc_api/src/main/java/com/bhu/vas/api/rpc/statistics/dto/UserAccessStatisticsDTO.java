package com.bhu.vas.api.rpc.statistics.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bluesand on 4/29/15.
 */
@SuppressWarnings("serial")
public class UserAccessStatisticsDTO implements Serializable {
    private String mac;
    private String date;
    private String device_mac;
    private Date create_at;
    private Date update_at;
    private String extension_content;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDevice_mac() {
        return device_mac;
    }

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }

    public String getExtension_content() {
        return extension_content;
    }

    public void setExtension_content(String extension_content) {
        this.extension_content = extension_content;
    }
}
