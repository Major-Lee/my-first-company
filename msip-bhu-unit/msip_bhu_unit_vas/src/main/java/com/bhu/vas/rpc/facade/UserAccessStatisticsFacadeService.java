package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 4/29/15.
 */
@Service
public class UserAccessStatisticsFacadeService {
    @Resource
    private UserAccessStatisticsService userAccessStatisticsService;


    public TailPage<UserAccessStatistics> fetchUserAccessStatistics(String date, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", date);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        return userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
    }

}
