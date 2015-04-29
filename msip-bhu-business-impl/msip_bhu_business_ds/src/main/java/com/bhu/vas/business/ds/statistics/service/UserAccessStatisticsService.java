package com.bhu.vas.business.ds.statistics.service;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.dao.UserAccessStatisticsDao;
import com.smartwork.msip.cores.orm.service.EntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 4/28/15.
 */
@Service
@Transactional("coreTransactionManager")
public class UserAccessStatisticsService extends
        EntityService<UserDatePK, UserAccessStatistics, UserAccessStatisticsDao> {

    @Resource
    @Override
    public void setEntityDao(UserAccessStatisticsDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
