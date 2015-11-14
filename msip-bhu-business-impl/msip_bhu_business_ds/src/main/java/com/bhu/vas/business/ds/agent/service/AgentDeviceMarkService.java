package com.bhu.vas.business.ds.agent.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceMark;
import com.bhu.vas.business.ds.agent.dao.AgentDeviceMarkDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * 
 * @author Edmond
 *
 */
@Service
@Transactional("coreTransactionManager")
public class AgentDeviceMarkService extends AbstractCoreService<String, AgentDeviceMark, AgentDeviceMarkDao> {

    @Resource
    @Override
    public void setEntityDao(AgentDeviceMarkDao agentDeviceMarkDao) {
        super.setEntityDao(agentDeviceMarkDao);
    }

}
