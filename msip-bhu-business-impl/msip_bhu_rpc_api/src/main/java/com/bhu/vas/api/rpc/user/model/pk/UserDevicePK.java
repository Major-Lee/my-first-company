package com.bhu.vas.api.rpc.user.model.pk;

import com.smartwork.msip.cores.helper.StringHelper;

import java.io.Serializable;

/**
 * Created by bluesand on 15/4/10.
 */
public class UserDevicePK implements Serializable {

    private String mac;

    private int uid;

    public UserDevicePK() {
    }

    public UserDevicePK(String mac, int uid) {
        this.mac = mac;
        this.uid = uid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mac).append(StringHelper.MINUS_CHAR_GAP).append(uid);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDevicePK that = (UserDevicePK) o;

        if (uid != that.uid) return false;
        return !(mac != null ? !mac.equals(that.mac) : that.mac != null);

    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
