package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.user.service.UserService;
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

    private final Logger logger = LoggerFactory.getLogger(AgentFacadeService.class);

    @Resource
    private AgentDeviceClaimService agentDeviceClaimService;

    @Resource
    private DeliverMessageService deliverMessageService;

    @Resource
    private AgentDeviceImportLogService agentDeviceImportLogService;

    @Resource
    private UserService userService;

    @Resource
    private AgentBulltinBoardService agentBulltinBoardService;

    public boolean claimAgentDevice(String sn) {
        logger.info(String.format("AgentFacadeService claimAgentDevice sn[%s]", sn));
        AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(sn);

        if (agentDeviceClaim != null) {
            int status = agentDeviceClaim.getStatus();
            if (status == 0) { //如果未认领过需要认领
                agentDeviceClaim.setClaim_at(new Date());
                agentDeviceClaim.setStatus(1);
                agentDeviceClaimService.update(agentDeviceClaim);
                return true;
            }
        }
        return false;

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

    public TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ");
        int total = agentDeviceImportLogService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceImportLog> logs = agentDeviceImportLogService.findModelByModelCriteria(mc);
        List<AgentDeviceImportLogVTO>  dtos = new ArrayList<AgentDeviceImportLogVTO>();
        if (dtos != null) {
            AgentDeviceImportLogVTO vto = null;
            for (AgentDeviceImportLog log : logs) {
                vto = new AgentDeviceImportLogVTO();
                vto.setAid(log.getAid());
                vto.setCount(log.getCount());
                vto.setCreated_at(log.getCreated_at().getTime());

                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setName(agent.getNick() == null ? "" : agent.getNick());
                }
            }
        }
        return new CommonPage<AgentDeviceImportLogVTO>(pageNo, pageSize, total,dtos);
    }

    public AgentBulltinBoardVTO findAgentBulltinBoardById(long bid) {
        AgentBulltinBoard agentBulltinBoard = agentBulltinBoardService.getById(bid);

        AgentBulltinBoardVTO vto = null;
        if (agentBulltinBoard != null) {
            vto = new AgentBulltinBoardVTO();
            vto.setId(agentBulltinBoard.getId());
            int cid = agentBulltinBoard.getConsumer();
            vto.setCid(cid);
            User consumer = userService.getById(cid);
            if (consumer != null) {
                vto.setC_name(consumer.getNick());
            }
            int pid = agentBulltinBoard.getPublisher();
            vto.setPid(pid);
            User publisher = userService.getById(pid);
            if (publisher != null) {
                vto.setP_name(publisher.getNick());
            }

            vto.setContent(agentBulltinBoard.getContent());
            vto.setType(agentBulltinBoard.getType());
            vto.setCreated_at(agentBulltinBoard.getCreated_at().getTime());
        }

        return vto;

    }

}
