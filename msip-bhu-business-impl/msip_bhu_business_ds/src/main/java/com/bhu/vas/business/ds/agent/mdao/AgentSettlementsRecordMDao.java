package com.bhu.vas.business.ds.agent.mdao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.bhu.vas.business.ds.agent.mdto.AgentSettlementsRecordMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.BaseMongoDAOImpl;

@Repository
public class AgentSettlementsRecordMDao extends BaseMongoDAOImpl<AgentSettlementsRecordMDTO>{
	@Resource(name = "mongoTemplate")
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
