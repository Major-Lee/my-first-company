package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
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

    @Resource
    private AgentStatisticsUnitFacadeService agentStatisticsUnitFacadeService;
    
    @Override
    public int claimAgentDevice(String sn) {
        logger.info(String.format("claimAgentDevice sn[%s]", sn));
        return agentFacadeService.claimAgentDevice(sn);
    }

    @Override
    public TailPage<AgentDeviceClaimVTO> pageClaimedAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDeviceByUid uid[%s] pageNo[%s] pageSize[%s]",uid, pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDeviceById(uid, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceClaimVTO> pageClaimedAgentDevice(int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDeviceByUid pageNo[%s] pageSize[%s]", pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDeviceById(pageNo, pageSize);
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
    public TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int pageNo, int pageSize) {
        logger.info(String.format("pageAgentDeviceImportLog pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageAgentDeviceImportLog(pageNo, pageSize);
    }


    @Override
    public AgentBulltinBoardVTO findAgentBulltinBoardById(long bid) {
        logger.info(String.format("findAgentBulltinBoardById bid:%s", bid));
        return agentFacadeService.findAgentBulltinBoardById(bid);
    }

	@Override
	public RpcResponseDTO<StatisticsVTO> statistics(int uid, String enddate) {
		logger.info(String.format("statistics uid[%s] date[%s]", uid, enddate));
		return agentStatisticsUnitFacadeService.statistics(uid, enddate);
	}

	@Override
	public RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> pageHistoryRecords(int uid,String dateEndStr,int pageNo, int pageSize) {
		logger.info(String.format("historyrecords uid[%s] date[%s]", uid, dateEndStr));
		return agentStatisticsUnitFacadeService.pageHistoryRecords(uid, dateEndStr, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<TailPage<SettlementVTO>> pageSettlements(int uid,String dateCurrent,int pageNo, int pageSize) {
		logger.info(String.format("pageSettlements uid[%s]", uid));
		return agentStatisticsUnitFacadeService.pageSettlements(uid,dateCurrent, pageNo, pageSize);
	}
	
    @Override
    public TailPage<AgentBulltinBoardVTO> pageAgentBulltinBoardByUid(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageAgentBulltinBoardByUid uid:%s pageNo:%s pageSize:%s", uid, pageNo, pageSize));
        return agentFacadeService.pageAgentBulltinBoardByUid(uid, pageNo, pageSize);
    }

	@Override
	public RpcResponseDTO<AgentDeviceStatisticsVTO> fetchAgentDeviceStatistics(int agentuser) {
		logger.info(String.format("fetchAgentDeviceStatistics agentuser[%s]", agentuser));
		return agentStatisticsUnitFacadeService.fetchAgentDeviceStatistics(agentuser);
	}
}
