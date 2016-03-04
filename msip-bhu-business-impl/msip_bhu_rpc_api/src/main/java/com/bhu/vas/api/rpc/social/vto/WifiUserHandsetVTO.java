package com.bhu.vas.api.rpc.social.vto;

import com.bhu.vas.api.vto.device.UserDeviceVTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/3/16.
 */
public class WifiUserHandsetVTO implements Serializable {

    private String bssid;

    private String ssid;

    private List<UserDeviceVTO> userDeviceVTOList;

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public List<UserDeviceVTO> getUserDeviceVTOList() {
        return userDeviceVTOList;
    }

    public void setUserDeviceVTOList(List<UserDeviceVTO> userDeviceVTOList) {
        this.userDeviceVTOList = userDeviceVTOList;
    }
}
