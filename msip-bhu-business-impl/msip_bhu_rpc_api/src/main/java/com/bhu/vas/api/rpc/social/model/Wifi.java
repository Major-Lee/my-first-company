package com.bhu.vas.api.rpc.social.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

import java.util.Date;

/**
 * Created by bluesand on 3/7/16.
 */
public class Wifi extends BaseStringModel {

    /**
     * ssid
     */
    private String ssid;

    /**
     * 纬度 latitude
     */
    private double lat;

    /**
     * 经度 longitude
     */
    private double lon;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 最大速率
     */
    private String max_rate;

    /**
     *
     */
    private Date created_at;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMax_rate() {
        return max_rate;
    }

    public void setMax_rate(String max_rate) {
        this.max_rate = max_rate;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
