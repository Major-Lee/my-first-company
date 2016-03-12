package com.bhu.vas.api.rpc.social.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/12/16.
 */
public class SocialStatisManufatorDTO implements Serializable {

    private String bssid;

    private List<SocialStatisManufatorItemDTO> items;

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public List<SocialStatisManufatorItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SocialStatisManufatorItemDTO> items) {
        this.items = items;
    }
}
