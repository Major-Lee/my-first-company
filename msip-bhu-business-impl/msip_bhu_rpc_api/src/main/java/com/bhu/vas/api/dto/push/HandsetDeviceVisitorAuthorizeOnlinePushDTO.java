package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by bluesand on 11/17/15.
 */
@SuppressWarnings("serial")
public class HandsetDeviceVisitorAuthorizeOnlinePushDTO extends NotificationPushDTO {

    //上线的终端的mac
	@JsonIgnore
    private String hd_mac;
    //终端别名或主机名
	@JsonIgnore
    private String n;
    //此终端是否第一次连接到此设备


    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    @Override
    public String getPushType() {
        return PushType.HandsetDeviceVisitorAuthorizeOnline.getType();
    }
}
