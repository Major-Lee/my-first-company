package com.bhu.vas.business.asyn.spring.model.agent;

import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

import java.util.List;

/**
 * Created by bluesand on 9/8/15.
 */
public class AgentDeviceClaimUpdateDTO extends ActionDTO {


    private long logId;

    private List<AgentDeviceClaimDTO> devices;

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }


    public List<AgentDeviceClaimDTO> getDevices() {
        return devices;
    }

    public void setDevices(List<AgentDeviceClaimDTO> devices) {
        this.devices = devices;
    }

    @Override
    public String getActionType() {
        return ActionMessageType.AgentDeviceClaimUpdate.getPrefix();
    }
}
