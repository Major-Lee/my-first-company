package com.bhu.vas.api.vto.guest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 10/26/15.
 */
public class URouterVisitorListVTO implements Serializable {

    private String mac;

    private String n;

    private int ohd_count;

    private int limit_rate;

    private List<URouterVisitorDetailVTO> items;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public int getOhd_count() {
        return ohd_count;
    }

    public void setOhd_count(int ohd_count) {
        this.ohd_count = ohd_count;
    }

    public int getLimit_rate() {
        return limit_rate;
    }

    public void setLimit_rate(int limit_rate) {
        this.limit_rate = limit_rate;
    }

    public List<URouterVisitorDetailVTO> getItems() {
        return items;
    }

    public void setItems(List<URouterVisitorDetailVTO> items) {
        this.items = items;
    }
}
