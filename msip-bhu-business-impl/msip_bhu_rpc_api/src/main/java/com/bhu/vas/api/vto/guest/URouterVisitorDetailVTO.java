package com.bhu.vas.api.vto.guest;

import java.io.Serializable;

/**
 * Created by bluesand on 10/26/15.
 */
public class URouterVisitorDetailVTO implements Serializable {

    private String hd_mac;

    private String n;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}
