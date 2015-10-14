package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 8/3/15.
 */
public class WifiDeviceGroupAsynCreateIndexDTO extends ActionDTO{


    public static final String GROUP_INDEX_GRANT = "grant";

    public static final String GROUP_INDEX_UNGRANT = "ungrant";

    private String wifiIds;

    private long gid;

    private String type;


    public String getWifiIds() {
        return wifiIds;
    }

    public void setWifiIds(String wifiIds) {
        this.wifiIds = wifiIds;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getActionType() {
        return ActionMessageType.WifiDeviceGroupCreateIndex.getPrefix();
    }
}
