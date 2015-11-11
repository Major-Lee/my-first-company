package com.bhu.vas.business.ds.agent.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.agent.model.AgentBillSettlements;
import com.bhu.vas.business.ds.agent.dao.AgentBillSettlementsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * 
 * @author Edmond
 *
 */
@Service
@Transactional("coreTransactionManager")
public class AgentBillSettlementsService extends AbstractCoreService<String, AgentBillSettlements, AgentBillSettlementsDao> {
    @Resource
    @Override
    public void setEntityDao(AgentBillSettlementsDao agentBillSettlementsDao) {
        super.setEntityDao(agentBillSettlementsDao);
    }

}
