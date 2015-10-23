package com.bhu.vas.business.asyn.spring.model.agent;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 9/8/15.
 */
public class AgentDeviceClaimUpdateDTO extends ActionDTO {


    private long logId;


    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }


    @Override
    public String getActionType() {
        return ActionMessageType.AgentDeviceClaimUpdate.getPrefix();
    }
}
