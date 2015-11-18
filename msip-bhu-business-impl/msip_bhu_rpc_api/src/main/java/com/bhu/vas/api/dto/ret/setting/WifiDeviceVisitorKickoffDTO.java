package com.bhu.vas.api.dto.ret.setting;

import java.io.Serializable;

/**
 * Created by bluesand on 10/28/15.
 */
@SuppressWarnings("serial")
public class WifiDeviceVisitorKickoffDTO implements Serializable {
    private String hd_mac;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }
}
