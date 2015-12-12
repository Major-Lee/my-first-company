package com.bhu.vas.api.mdto;

import java.io.Serializable;
import java.util.Date;

import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * Created by bluesand on 6/24/15.
 */
@SuppressWarnings("serial")
public class WifiHandsetDeviceItemDetailMDTO implements Serializable{

    private long login_at;

    private long logout_at;

    private long rx_bytes;

    public long getLogin_at() {
        return login_at;
    }

    public void setLogin_at(long login_at) {
        this.login_at = login_at;
    }

    public long getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(long logout_at) {
        this.logout_at = logout_at;
    }

    public long getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(long rx_bytes) {
        this.rx_bytes = rx_bytes;
    }

    public String getOd(){
    	if(login_at >0){
    		return DateTimeHelper.getDateTime(new Date(login_at), DateTimeHelper.FormatPattern3);
    	}else{
    		return null;
    	}
    }
    public String getFd(){
    	if(logout_at >0){
    		return DateTimeHelper.getDateTime(new Date(logout_at), DateTimeHelper.FormatPattern3);
    	}else{
    		return null;
    	}
    }
    public WifiHandsetDeviceItemDetailMDTO() {
	}
	public WifiHandsetDeviceItemDetailMDTO(long login_at, long logout_at,
			long rx_bytes) {
		this.login_at = login_at;
		this.logout_at = logout_at;
		this.rx_bytes = rx_bytes;
	}
}
