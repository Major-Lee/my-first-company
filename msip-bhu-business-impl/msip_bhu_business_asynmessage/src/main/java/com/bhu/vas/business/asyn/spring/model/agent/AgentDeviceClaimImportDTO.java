package com.bhu.vas.business.asyn.spring.model.agent;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 9/8/15.
 */
public class AgentDeviceClaimImportDTO extends ActionDTO {

    private String path;

    private String originName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    @Override
    public String getActionType() {
        return ActionMessageType.AgentDeviceClaimImport.getPrefix();
    }
}
