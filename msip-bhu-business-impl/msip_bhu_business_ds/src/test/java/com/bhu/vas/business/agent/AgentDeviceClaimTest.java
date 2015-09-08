package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 9/7/15.
 */
public class AgentDeviceClaimTest extends BaseTest {

    @Resource
    public AgentDeviceClaimService agentDeviceClaimService;

    //@Test
    public void create() {
        AgentDeviceClaim agentDeviceClaim = new AgentDeviceClaim();
        agentDeviceClaim.setId("1234567893");
        agentDeviceClaim.setUid(6);
        Date date = new Date();
        agentDeviceClaim.setSold_at(date);
        agentDeviceClaimService.insert(agentDeviceClaim);
    }

    @Test
    public void list() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ");
        mc.setPageNumber(1);
        mc.setPageSize(5);
        List<AgentDeviceClaim> groups = agentDeviceClaimService.findModelByModelCriteria(mc);
        System.out.println(groups);
    }

}
