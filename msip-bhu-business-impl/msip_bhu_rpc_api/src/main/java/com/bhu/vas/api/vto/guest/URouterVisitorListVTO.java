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

    private int rx_rate;

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

    public int getRx_rate() {
        return rx_rate;
    }

    public void setRx_rate(int rx_rate) {
        this.rx_rate = rx_rate;
    }

    public List<URouterVisitorDetailVTO> getItems() {
        return items;
    }

    public void setItems(List<URouterVisitorDetailVTO> items) {
        this.items = items;
    }
}
