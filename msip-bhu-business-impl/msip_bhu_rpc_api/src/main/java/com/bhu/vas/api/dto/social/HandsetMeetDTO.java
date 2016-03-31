package com.bhu.vas.api.dto.social;

import java.io.Serializable;

/**
 * Created by bluesand on 3/3/16.
 */
@SuppressWarnings("serial")
public class HandsetMeetDTO implements Serializable {

    private String bssid;

    private String ssid;

    private long ts;

    private String lat;

    private String lon;

    private String addr;


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
