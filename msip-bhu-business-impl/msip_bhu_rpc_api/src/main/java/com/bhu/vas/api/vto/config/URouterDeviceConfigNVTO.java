package com.bhu.vas.api.vto.config;

import java.io.Serializable;

/** 终端名称
 * Created by bluesand on 8/31/15.
 */
@SuppressWarnings("serial")
public class URouterDeviceConfigNVTO  implements Serializable {

    private String mac;

    private String n;

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
}
