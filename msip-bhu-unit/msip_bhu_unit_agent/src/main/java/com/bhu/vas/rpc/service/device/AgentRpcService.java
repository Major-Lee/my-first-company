package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.rpc.facade.AgentFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 9/7/15.
 */
@Service("agentRpcService")
public class AgentRpcService implements IAgentRpcService {

    private final Logger logger = LoggerFactory.getLogger(AgentRpcService.class);

    @Resource
    public AgentFacadeService agentFacadeService;

    @Override
    public TailPage<AgentDeviceClaimDTO> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDevice uid:%s pageNo:%s pageSize:%s",uid, pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDevice(uid, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceClaimDTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDevice uid:%s pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDeviceByUid(uid, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceClaimDTO> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDevice pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDevice(pageNo, pageSize);
    }

    @Override
    public void importAgentDeviceClaim(int uid, String path, String originName) {
        logger.info(String.format("importAgentDeviceClaim uid:%s orginName:%s path:%s", uid, originName, path));
        agentFacadeService.importAgentDeviceClaim(uid, path, originName);
    }
}
