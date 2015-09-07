package com.bhu.vas.rpc.service.device;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.rpc.facade.AgentFacadeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 9/7/15.
 */
@Service("agentRpcService")
public class AgentRpcService implements IAgentRpcService {

    @Resource
    public AgentFacadeService agentFacadeService;

    @Override
    public String hello() {
        return agentFacadeService.hello();
    }
}
