package com.bhu.vas.api.dto.ret.setting;

import java.io.Serializable;

/**
 * Created by bluesand on 10/28/15.
 */
public class WifiDeviceVisitorKickoffDTO implements Serializable {
    private String mac;
    private String hd_mac;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }
}
