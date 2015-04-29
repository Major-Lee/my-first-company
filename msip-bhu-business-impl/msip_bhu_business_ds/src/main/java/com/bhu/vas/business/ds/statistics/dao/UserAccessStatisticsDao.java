package com.bhu.vas.business.ds.statistics.dao;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
import org.springframework.stereotype.Repository;

/**
 * Created by bluesand on 4/28/15.
 */

@Repository
public class UserAccessStatisticsDao extends AbstractCoreDao<UserDatePK, UserAccessStatistics> {
}
