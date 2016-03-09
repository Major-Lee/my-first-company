package com.bhu.vas.business.asyn.spring.model.social;

import com.bhu.vas.business.asyn.spring.builder.ActionSocialDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionSocialMessageType;

/**
 * Created by bluesand on 3/9/16.
 */
public class HandsetMeetDTO extends ActionSocialDTO {

    private String hd_mac_self;

    private String hd_mac;

    private String bssid;

    /**
     * 遇见dto
     */
    private String meet;

    public String getHd_mac_self() {
        return hd_mac_self;
    }

    public void setHd_mac_self(String hd_mac_self) {
        this.hd_mac_self = hd_mac_self;
    }

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getMeet() {
        return meet;
    }

    public void setMeet(String meet) {
        this.meet = meet;
    }

    @Override
    public String getActionType() {
        return ActionSocialMessageType.HandsetMeet.getPrefix();
    }
}
