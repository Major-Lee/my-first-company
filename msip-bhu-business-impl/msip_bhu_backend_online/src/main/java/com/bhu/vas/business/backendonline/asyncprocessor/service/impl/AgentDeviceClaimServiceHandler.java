package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimImportDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by bluesand on 9/8/15.
 */
@Service
public class AgentDeviceClaimServiceHandler {

    private final Logger logger = LoggerFactory.getLogger(AgentDeviceClaimServiceHandler.class);

    /**
     * 导入代理商设备
     * @param message
     */
    public void importAgentDeviceClaim(String message) {
        logger.info(String.format("AgentDeviceClaimServiceHandler importAgentDeviceClaim message[%s]", message));
        AgentDeviceClaimImportDTO dto =  JsonHelper.getDTO(message, AgentDeviceClaimImportDTO.class);

        //todo(bluesand)：处理POI excel,导入数据
    }

}
