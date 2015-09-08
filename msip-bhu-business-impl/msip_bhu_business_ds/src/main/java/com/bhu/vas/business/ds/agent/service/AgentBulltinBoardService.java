package com.bhu.vas.business.ds.agent.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.agent.dao.AgentBulltinBoardDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * 
 * @author Edmond
 *
 */
@Service
@Transactional("coreTransactionManager")
public class AgentBulltinBoardService extends AbstractCoreService<Long, AgentBulltinBoard, AgentBulltinBoardDao> {

    @Resource
    @Override
    public void setEntityDao(AgentBulltinBoardDao agentBulltinBoardDao) {
        super.setEntityDao(agentBulltinBoardDao);
    }
    
    @Override
	public AgentBulltinBoard insert(AgentBulltinBoard entity) {
		if(entity.getId() == null)
			SequenceService.getInstance().onCreateSequenceKey(entity, false);
		return super.insert(entity);
	}
}
