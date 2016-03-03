package com.bhu.vas.business.ds.social.service;

import com.bhu.vas.api.rpc.social.model.UserHandset;
import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.bhu.vas.business.ds.social.dao.UserHandsetDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractSocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 3/3/16.
 */
@Service
@Transactional("socialTransactionManager")
public class UserHandsetService extends AbstractSocialService<UserHandsetPK, UserHandset, UserHandsetDao> {

    @Resource
    @Override
    public void setEntityDao(UserHandsetDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
