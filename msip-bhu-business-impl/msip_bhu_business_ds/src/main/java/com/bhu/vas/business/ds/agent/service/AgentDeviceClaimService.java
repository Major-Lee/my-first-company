package com.bhu.vas.business.ds.agent.service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.dao.AgentDeviceClaimDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

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


    public int claimAgentDevice(String sn) {
        AgentDeviceClaim agentDeviceClaim = this.getById(sn);
        if (agentDeviceClaim != null) {
            int status = agentDeviceClaim.getStatus();
            if (status == 0) { //如果未认领过需要认领
                agentDeviceClaim.setClaim_at(new Date());
                agentDeviceClaim.setStatus(1);
                this.update(agentDeviceClaim);
                return agentDeviceClaim.getUid();
            } else {
                return 0; //已经认领过
            }
        } else {
            return -1; //不存在
        }

    }
}
