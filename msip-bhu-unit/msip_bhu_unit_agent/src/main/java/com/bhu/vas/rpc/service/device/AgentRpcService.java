package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.rpc.facade.AgentFacadeService;
import com.bhu.vas.rpc.facade.AgentStatisticsUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
@Service("agentRpcService")
public class AgentRpcService implements IAgentRpcService {

    private final Logger logger = LoggerFactory.getLogger(AgentRpcService.class);

    @Resource
    private AgentFacadeService agentFacadeService;

    private AgentStatisticsUnitFacadeService agentStatisticsUnitFacadeService;
    
    @Override
    public boolean claimAgentDevice(String sn) {
        logger.info(String.format("claimAgentDevice sn[%s]", sn));
        return agentFacadeService.claimAgentDevice(sn);
    }

    @Override
    public TailPage<AgentDeviceClaimVTO> pageClaimedAgentDevice(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDevice uid:%s pageNo:%s pageSize:%s",uid, pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDevice(uid, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDeviceByUid uid:%s pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDeviceByUid(uid, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDevice pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDevice(pageNo, pageSize);
    }

    @Override
    public void importAgentDeviceClaim(int uid,int aid, String inputPath, String outputPath, String originName) {
        logger.info(String.format("importAgentDeviceClaim uid:%s aid:%s orginName:%s inputpath:%s outputPath:%s",
                uid, aid, originName, inputPath, outputPath));
        agentFacadeService.importAgentDeviceClaim(uid, aid, inputPath, outputPath, originName);
    }

	@Override
	public RpcResponseDTO<StatisticsVTO> statistics(int uid, String enddate) {
		logger.info(String.format("statistics uid[%s] date[%s]", uid, enddate));
		return agentStatisticsUnitFacadeService.statistics(uid, enddate);
	}

	@Override
	public RpcResponseDTO<List<DailyRevenueRecordVTO>> historyrecords(int uid,String enddate) {
		logger.info(String.format("historyrecords uid[%s] date[%s]", uid, enddate));
		return agentStatisticsUnitFacadeService.historyrecords(uid, enddate);
	}
}
