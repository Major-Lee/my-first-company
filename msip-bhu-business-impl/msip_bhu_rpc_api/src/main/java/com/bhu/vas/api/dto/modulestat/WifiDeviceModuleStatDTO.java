package com.bhu.vas.api.dto.modulestat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 11/27/15.
 *
 * 增值服务统计
 */
public class WifiDeviceModuleStatDTO implements Serializable {
    @JsonProperty("dev")
    private String mac;//设备mac
    @JsonProperty("item")
    private List<WifiDeviceModuleStatItemDTO> items;//


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<WifiDeviceModuleStatItemDTO> getItems() {
        return items;
    }

    public void setItems(List<WifiDeviceModuleStatItemDTO> items) {
        this.items = items;
    }
}
