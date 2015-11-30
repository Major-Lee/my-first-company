package com.bhu.vas.api.dto.modulestat;

import java.io.Serializable;

/**
 * Created by bluesand on 11/27/15.
 */
public class WifiDeviceModuleStatItemDTO implements Serializable {
    private  String mac;
    private  int type;
    private int sequence;
    private long systime;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getSystime() {
        return systime;
    }

    public void setSystime(long systime) {
        this.systime = systime;
    }
}
