package com.bhu.vas.api.rpc.social.model.pk;

import com.smartwork.msip.cores.helper.StringHelper;

import java.io.Serializable;

/**
 * Created by bluesand on 3/3/16.
 */
public class UserHandsetPK implements Serializable {

    public UserHandsetPK() {
    }

    public UserHandsetPK(long uid, String hd_mac) {
        this.uid = uid;
        this.hd_mac = hd_mac;
    }

    private long uid;

    private String hd_mac;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(uid).append(StringHelper.MINUS_CHAR_GAP).append(hd_mac);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserHandsetPK that = (UserHandsetPK) o;

        if (uid != that.uid) return false;
        return !(hd_mac != null ? !hd_mac.equals(that.hd_mac) : that.hd_mac != null);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
