package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    @Resource
    private AgentDeviceClaimService agentDeviceClaimService;

    public TailPage<AgentDeviceClaim> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNotNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> groups = agentDeviceClaimService.findModelByModelCriteria(mc);
        return new CommonPage<AgentDeviceClaim>(pageNo, pageSize, total,groups);
    }

    public TailPage<AgentDeviceClaim> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnIsNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> groups = agentDeviceClaimService.findModelByModelCriteria(mc);
        return new CommonPage<AgentDeviceClaim>(pageNo, pageSize, total,groups);
    }
}
