package com.bhu.vas.business.ds.statistics.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.dao.UserAccessStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * Created by bluesand on 4/28/15.
 */
@Service
@Transactional("coreTransactionManager")
public class UserAccessStatisticsService extends AbstractCoreService<UserDatePK, UserAccessStatistics, UserAccessStatisticsDao> {

    @Resource
    @Override
    public void setEntityDao(UserAccessStatisticsDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
