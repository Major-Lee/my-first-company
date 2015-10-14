package com.bhu.vas.business.statistics;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
import com.bhu.vas.business.ds.statistics.service.UserUrlStatisticsService;
import com.smartwork.msip.localunit.BaseTest;

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
