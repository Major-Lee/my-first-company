package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
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

    public TailPage<AgentDeviceClaimDTO> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNotNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimDTO>  dtos = new ArrayList<AgentDeviceClaimDTO>();
        if (dtos != null) {
            AgentDeviceClaimDTO agentDeviceClaimDTO = null;
            for (AgentDeviceClaim dto : agents) {
                agentDeviceClaimDTO.setId(dto.getId());
                agentDeviceClaimDTO.setMac(dto.getMac());
                agentDeviceClaimDTO.setSold_at(dto.getSold_at());
                agentDeviceClaimDTO.setClaim_at(dto.getClaim_at());
                agentDeviceClaimDTO.setUid(dto.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimDTO>(pageNo, pageSize, total,dtos);
    }

    public TailPage<AgentDeviceClaimDTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIsNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimDTO>  dtos = new ArrayList<AgentDeviceClaimDTO>();
        if (dtos != null) {
            AgentDeviceClaimDTO agentDeviceClaimDTO = null;
            for (AgentDeviceClaim dto : agents) {
                agentDeviceClaimDTO.setId(dto.getId());
                agentDeviceClaimDTO.setMac(dto.getMac());
                agentDeviceClaimDTO.setSold_at(dto.getSold_at());
                agentDeviceClaimDTO.setClaim_at(dto.getClaim_at());
                agentDeviceClaimDTO.setUid(dto.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimDTO>(pageNo, pageSize, total,dtos);
    }

    public TailPage<AgentDeviceClaimDTO> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnIsNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimDTO>  dtos = new ArrayList<AgentDeviceClaimDTO>();
        if (dtos != null) {
            AgentDeviceClaimDTO agentDeviceClaimDTO = null;
            for (AgentDeviceClaim dto : agents) {
                agentDeviceClaimDTO.setId(dto.getId());
                agentDeviceClaimDTO.setMac(dto.getMac());
                agentDeviceClaimDTO.setSold_at(dto.getSold_at());
                agentDeviceClaimDTO.setClaim_at(dto.getClaim_at());
                agentDeviceClaimDTO.setUid(dto.getUid());
            }
        }
        return new CommonPage<AgentDeviceClaimDTO>(pageNo, pageSize, total,dtos);
    }

    public void importAgentDeviceClaim(int uid, String inputPath, String outputPath, String originName) {
        deliverMessageService.sendAgentDeviceClaimImportMessage(uid, inputPath, outputPath, originName);
    }
}
