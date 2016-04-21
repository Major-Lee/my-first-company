package com.bhu.vas.api.rpc.social.dto;

import com.smartwork.msip.cores.helper.JsonHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/12/16.
 */
@SuppressWarnings("serial")
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


    public static void main(String[] args) {
        String manu = "{ \"bssid\": \"84:82:f4:1c:00:03\", \"items\": [  { \"name\": \"apple\", \"count\": 10},             { \"name\": \"samsung\", \"count\": 2},             { \"name\": \"mi\", \"count\": 1},             { \"name\": \"huawei\", \"count\": 5},             { \"name\": \"vivo\", \"count\": 6}         ]     }";
        SocialStatisManufatorDTO dto = JsonHelper.getDTO(manu,SocialStatisManufatorDTO.class);
        System.out.println(dto);
    }
}
