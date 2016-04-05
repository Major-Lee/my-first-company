package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 9/21/15.
 */
public class DeviceModifySettingVapDTO extends ActionDTO {
    @Override
    public String getActionType() {
        return ActionMessageType.DeviceModifySettingVap.getPrefix();
    }
}
