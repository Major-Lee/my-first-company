package com.bhu.vas.business.asyn.spring.model.social;

import com.bhu.vas.business.asyn.spring.builder.ActionSocialDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionSocialMessageType;

/**
 * Created by bluesand on 3/9/16.
 */
public class HandsetMeetDTO extends ActionSocialDTO {

    private String hd_mac;

    private String hd_macs;

    private String bssid;

    /**
     * 遇见dto
     */
    private String meet;

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getHd_macs() {
        return hd_macs;
    }

    public void setHd_macs(String hd_macs) {
        this.hd_macs = hd_macs;
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
