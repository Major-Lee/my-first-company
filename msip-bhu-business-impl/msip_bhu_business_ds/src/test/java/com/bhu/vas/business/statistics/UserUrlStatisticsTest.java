package com.bhu.vas.business.statistics;

import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandSubDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserUrlStatisticsService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserUrlStatisticsTest extends BaseTest {

    @Resource
    private UserUrlStatisticsService userUrlStatisticsService;

    
    @Test
    public void find() {
        List<UserUrlDTO> userUrlDTOList =  userUrlStatisticsService.getById("2015-05-29").getInnerModels();

        System.out.println(userUrlDTOList);
    }


}
