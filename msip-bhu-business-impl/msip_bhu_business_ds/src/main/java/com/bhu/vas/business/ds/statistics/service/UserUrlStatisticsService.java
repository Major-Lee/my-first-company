package com.bhu.vas.business.ds.statistics.service;

import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserUrlStatistics;
import com.bhu.vas.business.ds.statistics.dao.UserBrandStatisticsDao;
import com.bhu.vas.business.ds.statistics.dao.UserUrlStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 4/28/15.
 */
@Service
@Transactional("coreTransactionManager")
public class UserUrlStatisticsService extends AbstractCoreService<String, UserUrlStatistics, UserUrlStatisticsDao> {

    @Resource
    @Override
    public void setEntityDao(UserUrlStatisticsDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
