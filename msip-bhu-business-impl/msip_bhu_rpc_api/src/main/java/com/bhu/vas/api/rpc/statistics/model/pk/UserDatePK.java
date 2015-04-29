package com.bhu.vas.api.rpc.statistics.model.pk;

import com.smartwork.msip.cores.helper.StringHelper;

import java.io.Serializable;

/**
 * Created by bluesand on 15/4/10.
 */
@SuppressWarnings("serial")
public class UserDatePK implements Serializable {

    private String mac;

    //yyyy-MM-dd
    private String date;

    public UserDatePK() {
    }

    public UserDatePK(String mac, String date) {
        this.mac = mac;
        this.date = date;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mac).append(StringHelper.MINUS_CHAR_GAP).append(date);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDatePK that = (UserDatePK) o;
        if(date == null || mac == null) return false;
        if (date != null && !date.equals(that.date)) return false;
        return !(mac != null ? !mac.equals(that.mac) : that.mac != null);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
