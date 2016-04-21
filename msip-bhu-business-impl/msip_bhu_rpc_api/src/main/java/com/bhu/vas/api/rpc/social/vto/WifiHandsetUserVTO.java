package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/3/16.
 */
@SuppressWarnings("serial")
public class WifiHandsetUserVTO implements Serializable {

    private String bssid;

    private String ssid;

    private List<HandsetUserVTO> handsets;

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

    public List<HandsetUserVTO> getHandsets() {
        return handsets;
    }

    public void setHandsets(List<HandsetUserVTO> handsets) {
        this.handsets = handsets;
    }
}
