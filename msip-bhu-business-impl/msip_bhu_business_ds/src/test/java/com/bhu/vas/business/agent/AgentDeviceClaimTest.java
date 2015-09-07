package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 9/7/15.
 */
public class AgentDeviceClaimTest extends BaseTest {

    @Resource
    public AgentDeviceClaimService agentDeviceClaimService;

    @Test
    public void create() {
        AgentDeviceClaim agentDeviceClaim = new AgentDeviceClaim();
        agentDeviceClaim.setId("1234567890");
        agentDeviceClaim.setUid(6);
        Date date = new Date();
        agentDeviceClaim.setSold_at(date);

        agentDeviceClaimService.insert(agentDeviceClaim);
    }

}
