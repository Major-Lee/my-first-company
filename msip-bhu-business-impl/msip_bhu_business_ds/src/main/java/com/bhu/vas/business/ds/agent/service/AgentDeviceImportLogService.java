package com.bhu.vas.business.ds.agent.service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.agent.dao.AgentDeviceImportLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 9/15/15.
 */
@Service
@Transactional("coreTransactionManager")
public class AgentDeviceImportLogService extends AbstractCoreService<Long, AgentDeviceImportLog, AgentDeviceImportLogDao> {

    @Resource
    @Override
    public void setEntityDao(AgentDeviceImportLogDao agentDeviceImportLogDao) {
        super.setEntityDao(agentDeviceImportLogDao);
    }

    @Override
    public AgentDeviceImportLog insert(AgentDeviceImportLog entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }
}
