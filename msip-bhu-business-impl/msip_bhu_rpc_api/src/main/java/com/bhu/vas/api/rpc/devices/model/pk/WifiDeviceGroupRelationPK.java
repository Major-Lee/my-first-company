package com.bhu.vas.api.rpc.devices.model.pk;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * Created by bluesand on 7/16/15.
 */
@SuppressWarnings("serial")
public class WifiDeviceGroupRelationPK implements Serializable {

    private long gid;

    private String mac;

    public WifiDeviceGroupRelationPK() {

    }

    public WifiDeviceGroupRelationPK(long gid, String mac) {
        this.gid = gid;
        this.mac = mac;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(gid).append(StringHelper.MINUS_CHAR_GAP).append(mac);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiDeviceGroupRelationPK that = (WifiDeviceGroupRelationPK) o;

        if (gid != that.gid) return false;
        return !(mac != null ? !mac.equals(that.mac) : that.mac != null);

    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
