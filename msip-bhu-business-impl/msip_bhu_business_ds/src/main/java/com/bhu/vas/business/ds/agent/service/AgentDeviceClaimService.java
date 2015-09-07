package com.bhu.vas.business.ds.agent.service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.dao.AgentDeviceClaimDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
@Transactional("coreTransactionManager")
public class AgentDeviceClaimService extends AbstractCoreService<String, AgentDeviceClaim, AgentDeviceClaimDao> {

    @Resource
    @Override
    public void setEntityDao(AgentDeviceClaimDao agentDeviceClaimDao) {
        super.setEntityDao(agentDeviceClaimDao);
    }
}
