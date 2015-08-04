package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 8/3/15.
 */
public class WifiDeviceGroupAsynCreateIndexDTO extends ActionDTO{


    private String wifiIds;

    private String groupIds;


    public String getWifiIds() {
        return wifiIds;
    }

    public void setWifiIds(String wifiIds) {
        this.wifiIds = wifiIds;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public String getActionType() {
        return ActionMessageType.WifiDeviceGroupCreateIndex.getPrefix();
    }
}
