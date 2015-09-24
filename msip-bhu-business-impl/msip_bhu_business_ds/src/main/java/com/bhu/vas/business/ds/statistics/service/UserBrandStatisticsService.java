package com.bhu.vas.business.ds.statistics.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.business.ds.statistics.dao.UserBrandStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

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
