package com.bhu.vas.business.ds.agent.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.agent.dao.AgentBulltinBoardDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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
    
    
    public AgentBulltinBoard bulltinPublish(int publisher,int consumer,AgentBulltinType type,String content){
    	AgentBulltinBoard bulltin = new AgentBulltinBoard();
    	bulltin.setPublisher(publisher);
    	bulltin.setConsumer(consumer);
    	bulltin.setType(type.getKey());
    	bulltin.setContent(content);
    	return this.insert(bulltin);
    }
    
    public TailPage<AgentBulltinBoard> pageAgentBulltinBoard4Consumer(int consumer,int pageno,int pagesize){
    	return pageAgentBulltinBoard(0,consumer,null, pageno, pagesize);
    }
    
    public TailPage<AgentBulltinBoard> pageAllAgentBulltinBoard(int pageno,int pagesize){
    	return pageAgentBulltinBoard(0,0,null, pageno, pagesize);
    }
    
    private TailPage<AgentBulltinBoard> pageAgentBulltinBoard(int publisher,int consumer,AgentBulltinType type,int pageno,int pagesize){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		if(type != null)
			createCriteria.andColumnEqualTo("type", type.getKey());
		if(publisher > 0)
			createCriteria.andColumnEqualTo("publisher", publisher);
		
		if(consumer > 0)
			createCriteria.andColumnEqualTo("consumer", consumer);
		mc.setPageNumber(pageno);
		mc.setPageSize(pagesize);
		return this.findModelTailPageByModelCriteria(mc);
	}
    
}
