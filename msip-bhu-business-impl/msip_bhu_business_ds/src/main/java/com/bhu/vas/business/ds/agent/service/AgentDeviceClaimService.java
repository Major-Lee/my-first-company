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


    /**
     * 通过设备的sn进行认领操作
     * @param sn
     * @return
     * -1 认领库中不存在此设备
     * >0 认领库中的agentuser id
     */
    public int claimAgentDevice(String sn, String mac) {
        AgentDeviceClaim agentDeviceClaim = this.getById(sn);
        if (agentDeviceClaim != null) {
            int status = agentDeviceClaim.getStatus();
            if (status == 0) { //如果未认领过需要认领
                agentDeviceClaim.setClaim_at(new Date());
                agentDeviceClaim.setStatus(1);
                agentDeviceClaim.setMac(mac);
                this.update(agentDeviceClaim);
            }
            return agentDeviceClaim.getUid(); //不管认领未认领返回uid
        } else {
            return -1; //不存在
        }

    }
}
