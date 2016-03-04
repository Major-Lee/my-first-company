package com.bhu.vas.business.ds.social.service;

import com.bhu.vas.api.rpc.social.model.HandsetUser;
import com.bhu.vas.business.ds.social.dao.HandsetUserDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractSocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 3/3/16.
 */
@Service
@Transactional("socialTransactionManager")
public class HandsetUserService extends AbstractSocialService<String, HandsetUser, HandsetUserDao> {

    @Resource
    @Override
    public void setEntityDao(HandsetUserDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
