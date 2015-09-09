package com.bhu.vas.rpc.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    @Resource
    private AgentDeviceClaimService agentDeviceClaimService;

    @Resource
    private DeliverMessageService deliverMessageService;

    public TailPage<AgentDeviceClaim> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNotNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> groups = agentDeviceClaimService.findModelByModelCriteria(mc);
        return new CommonPage<AgentDeviceClaim>(pageNo, pageSize, total,groups);
    }

    public TailPage<AgentDeviceClaim> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNull("mac");
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

    public void importAgentDeviceClaim(int uid, String path, String originName) {
        deliverMessageService.sendAgentDeviceClaimImportMessage(uid, path, originName);
    }
}
