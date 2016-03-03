package com.bhu.vas.business.ds.social.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.social.dao.WifiCommentDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractSocialService;

/**
 * Created by bluesand on 3/2/16.
 */
@Service
@Transactional("socialTransactionManager")
public class WifiCommentService  extends AbstractSocialService<Long, WifiComment, WifiCommentDao> {

    @Resource
    @Override
    public void setEntityDao(WifiCommentDao entityDao) {
        super.setEntityDao(entityDao);
    }

    @Override
    public WifiComment insert(WifiComment entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }
    
 
}
