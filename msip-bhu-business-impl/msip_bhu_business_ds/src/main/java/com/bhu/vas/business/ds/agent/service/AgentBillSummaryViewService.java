package com.bhu.vas.business.ds.agent.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.agent.model.AgentBillSummaryView;
import com.bhu.vas.business.ds.agent.dao.AgentBillSummaryViewDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * 
 * @author Edmond
 *
 */
@Service
@Transactional("coreTransactionManager")
public class AgentBillSummaryViewService extends AbstractCoreService<Integer, AgentBillSummaryView, AgentBillSummaryViewDao> {
    @Resource
    @Override
    public void setEntityDao(AgentBillSummaryViewDao agentBillSummaryViewDao) {
        super.setEntityDao(agentBillSummaryViewDao);
    }

}
