package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
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

    public boolean claimAgentDevice(String sn) {
        AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(sn);

        if (agentDeviceClaim != null) {
            agentDeviceClaim.setClaim_at(new Date());
            agentDeviceClaim.setStatus(1);
            agentDeviceClaimService.update(agentDeviceClaim);
            return true;
        } else {
            return false;
        }

    }


    public TailPage<AgentDeviceClaimVTO> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNotNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  dtos = new ArrayList<AgentDeviceClaimVTO>();
        if (dtos != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = new AgentDeviceClaimVTO();
                vto.setId(agentDeviceClaim.getId());
                vto.setMac(agentDeviceClaim.getMac());
                vto.setStock_code(agentDeviceClaim.getStock_code());
                vto.setStock_name(agentDeviceClaim.getStock_name());
                vto.setSold_at(agentDeviceClaim.getSold_at());
                vto.setClaim_at(agentDeviceClaim.getClaim_at());
                vto.setUid(agentDeviceClaim.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total,dtos);
    }

    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  dtos = new ArrayList<AgentDeviceClaimVTO>();
        if (dtos != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = new AgentDeviceClaimVTO();
                vto.setId(agentDeviceClaim.getId());
                vto.setMac(agentDeviceClaim.getMac());
                vto.setStock_code(agentDeviceClaim.getStock_code());
                vto.setStock_name(agentDeviceClaim.getStock_name());
                vto.setSold_at(agentDeviceClaim.getSold_at());
                vto.setClaim_at(agentDeviceClaim.getClaim_at());
                vto.setUid(agentDeviceClaim.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total,dtos);
    }

    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnIsNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  dtos = new ArrayList<AgentDeviceClaimVTO>();
        if (dtos != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = new AgentDeviceClaimVTO();
                vto.setId(agentDeviceClaim.getId());
                vto.setMac(agentDeviceClaim.getMac());
                vto.setStock_code(agentDeviceClaim.getStock_code());
                vto.setStock_name(agentDeviceClaim.getStock_name());
                vto.setSold_at(agentDeviceClaim.getSold_at());
                vto.setClaim_at(agentDeviceClaim.getClaim_at());
                vto.setUid(agentDeviceClaim.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total,dtos);
    }

    public void importAgentDeviceClaim(int uid, int aid, String inputPath, String outputPath, String originName) {
        deliverMessageService.sendAgentDeviceClaimImportMessage(uid, aid, inputPath, outputPath, originName);
    }
}
