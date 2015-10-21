package com.bhu.vas.business.ds.agent.service;

import com.bhu.vas.api.rpc.agent.model.AgentFinancialSettlement;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.agent.dao.AgentFinancialSettlementDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 10/21/15.
 */
@Service
@Transactional("coreTransactionManager")
public class AgentFinancialSettlementService extends AbstractCoreService<Long, AgentFinancialSettlement, AgentFinancialSettlementDao> {
    @Resource
    @Override
    public void setEntityDao(AgentFinancialSettlementDao agentFinancialSettlementDao) {
        super.setEntityDao(agentFinancialSettlementDao);
    }

    @Override
    public AgentFinancialSettlement insert(AgentFinancialSettlement entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }
}
