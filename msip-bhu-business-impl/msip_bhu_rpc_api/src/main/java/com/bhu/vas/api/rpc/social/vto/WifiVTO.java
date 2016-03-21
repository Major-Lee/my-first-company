package com.bhu.vas.api.rpc.social.vto;

import com.bhu.vas.api.dto.social.WifiActionDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/7/16.
 */
public class WifiVTO implements Serializable {

    private String bssid;

    private String ssid;

    private String lat;

    private String lon;

    private String addr;

    private String max_rate;

    /**
     * 生场商 manufactor
     */
    private String manu;

    private WifiActionDTO action;

    private List<WifiVisitorVTO> visitors;


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

    public String getMax_rate() {
        return max_rate;
    }

    public void setMax_rate(String max_rate) {
        this.max_rate = max_rate;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getManu() {
        return manu;
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public WifiActionDTO getAction() {
        return action;
    }

    public void setAction(WifiActionDTO action) {
        this.action = action;
    }

    public List<WifiVisitorVTO> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<WifiVisitorVTO> visitors) {
        this.visitors = visitors;
    }
}
