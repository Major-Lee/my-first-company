package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.vto.agent.*;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
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

    /* 公共 start*/
    @Override
    public int claimAgentDevice(String sn, String mac) {
        logger.info(String.format("claimAgentDevice sn[%s] mac[%s]", sn, mac));
        return agentFacadeService.claimAgentDevice(sn, mac);
    }

    @Override
    public AgentDeviceVTO pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDeviceByUid uid:%s pageNo:%s pageSize:%s",uid, pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDeviceByUid(uid, pageNo, pageSize);
    }

    @Override
    public AgentDeviceVTO pageUnClaimAgentDevice(int pageNo, int pageSize) {
        logger.info(String.format("pageUnClaimAgentDevice pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageUnClaimAgentDevice(pageNo, pageSize);
    }
    /* 公共 end*/



    /* 代理商 start*/
    @Override
    public AgentDeviceVTO pageClaimedAgentDeviceByUid(int uid, int status, int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDeviceByUid uid[%s] status[%s] pageNo[%s] pageSize[%s]",uid, status, pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDeviceByUid(uid, status, pageNo, pageSize);
    }

	@Override
	public RpcResponseDTO<AgentRevenueStatisticsVTO> statistics(int uid, String enddate) {
		logger.info(String.format("statistics uid[%s] date[%s]", uid, enddate));
		return agentStatisticsUnitFacadeService.statistics(uid, enddate);
	}

	@Override
	public RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> pageHistoryRecords(int uid,String dateEndStr,int pageNo, int pageSize) {
		logger.info(String.format("historyrecords uid[%s] date[%s]", uid, dateEndStr));
		return agentStatisticsUnitFacadeService.pageHistoryRecords(uid, dateEndStr, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<SettlementPageVTO> pageSettlements(int operator_user,int viewstatus,int pageNo, int pageSize) {
		logger.info(String.format("pageSettlements operator_user[%s]", operator_user));
		return agentStatisticsUnitFacadeService.pageSettlements(operator_user, viewstatus, pageNo, pageSize);
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

    /* 代理商 end*/


    /* 仓储管理员 start */
    @Override
    public AgentDeviceVTO pageClaimedAgentDevice(int uid, int status, int pageNo, int pageSize) {
        logger.info(String.format("pageClaimedAgentDeviceByUid status[%s], pageNo[%s] pageSize[%s]", status, pageNo, pageSize));
        return agentFacadeService.pageClaimedAgentDevice(uid, status, pageNo, pageSize);
    }

    @Override
    public TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageAgentDeviceImportLog pageNo:%s pageSize:%s", pageNo, pageSize));
        return agentFacadeService.pageAgentDeviceImportLog(uid, pageNo, pageSize);
    }

    @Override
    public boolean updateAgentImportImport(int uid, int logId) {
        logger.info(String.format("updateAgentImportImport uid[%s] logId[%s]", uid, logId));
        return agentFacadeService.updateAgentImportImport(uid, logId);
    }

    @Override
    public AgentDeviceImportLogVTO importAgentDeviceClaim(int uid,int aid, int wid, String inputPath, String outputPath, String originName, String remark) {
        logger.info(String.format("importAgentDeviceClaim uid:%s aid:%s wid:%s orginName:%s inputpath:%s outputPath:%s remark:%s",
                uid, aid, wid, originName, inputPath, outputPath, remark));
        return agentFacadeService.importAgentDeviceClaim(uid, aid, wid, inputPath, outputPath, originName, remark);
    }

    @Override
    public AgentBulltinBoardVTO findAgentBulltinBoardById(int uid, long bid) {
        logger.info(String.format("findAgentBulltinBoardById bid:%s", bid));
        return agentFacadeService.findAgentBulltinBoardById(uid, bid);
    }

    @Override
    public TailPage<UserVTO> pageSellorVTO(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageSellorVTO pageNo[%s] pageSize", pageNo, pageSize));
        return agentFacadeService.pageUserVTO(uid, UserType.Sellor.getIndex(), pageNo, pageSize);
    }

    @Override
    public TailPage<UserVTO> pageAgentUserVTO(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageAgentUserVTO pageNo[%s] pageSize", pageNo, pageSize));
        return agentFacadeService.pageUserVTO(uid, UserType.Agent.getIndex(), pageNo, pageSize);
    }

    /* 仓储管理员 end */


    /* 财务 start */
    @Override
    public boolean postAgentFinancialSettlement(int uid, int aid, double account, String invoice, String receipt, String remark) {
        logger.info(String.format("postAgentFinancialSettlement uid[%s] aid[%s] account[%s] invoice[%s] receipt[%s] remark[%s]",
                uid, aid, account,invoice, receipt,remark));
        return agentFacadeService.postAgentFinancialSettlement(uid, aid, account, invoice, receipt, remark);
    }

    @Override
    public TailPage<AgentFinancialSettlementVTO> pageAgentFinancialSettlementVTO(int uid, int pageNo, int pageSize) {
        logger.info(String.format("pageAgentFinancialSettlementVTO uid[%s]  pageNo[%s] pageSize[%s]",
                uid, pageNo, pageSize));
        return agentFacadeService.pageAgentFinancialSettlementVTO(uid, pageNo, pageSize);
    }



    /* 财务 end */
}
