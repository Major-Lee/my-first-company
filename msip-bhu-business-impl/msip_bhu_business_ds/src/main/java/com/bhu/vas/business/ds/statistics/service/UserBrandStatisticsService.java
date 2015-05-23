package com.bhu.vas.business.ds.statistics.service;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.dao.UserAccessStatisticsDao;
import com.bhu.vas.business.ds.statistics.dao.UserBrandStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 4/28/15.
 */
@Service
@Transactional("coreTransactionManager")
public class UserBrandStatisticsService extends AbstractCoreService<String, UserBrandStatistics, UserBrandStatisticsDao> {

    @Resource
    @Override
    public void setEntityDao(UserBrandStatisticsDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
