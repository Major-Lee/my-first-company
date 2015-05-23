package com.bhu.vas.business.statistics;

import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserBrandStatisticsTest extends BaseTest {

    @Resource
    private UserBrandStatisticsService userBrandStatisticsService;

    
    @Test
    public void insert() {
        UserBrandStatistics userBrandStatistics = new UserBrandStatistics();
        userBrandStatistics.setId("2015-05-23");
        userBrandStatistics.setCreated_at(new Date());

        List<UserBrandDTO> userBrandDTOList = new ArrayList<UserBrandDTO>();

        UserBrandDTO userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsang T1");
        userBrandDTO.setCount(10);
        userBrandDTO.setRatio("10%");
        userBrandDTOList.add(userBrandDTO);

        userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsnag T2");
        userBrandDTO.setCount(12);
        userBrandDTO.setRatio("21%");
        userBrandDTOList.add(userBrandDTO);

        userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsnag T3");
        userBrandDTO.setCount(23);
        userBrandDTO.setRatio("23%");
        userBrandDTOList.add(userBrandDTO);

        UserBrandStatisticsDTO userBrandStatisticsDTO = new UserBrandStatisticsDTO();

        userBrandStatisticsDTO.setBrand("Sumsang");

        userBrandStatisticsDTO.setCount(45);
        userBrandStatisticsDTO.setRatio("24%");

        userBrandStatisticsDTO.setDetail(userBrandDTOList);

        System.out.println(JsonHelper.getJSONString(userBrandStatisticsDTO));

        List<String> userBrandStatisticsDTOs = new ArrayList<String>();

        userBrandStatisticsDTOs.add(JsonHelper.getJSONString(userBrandStatisticsDTO));

        userBrandStatistics.putInnerModels(userBrandStatisticsDTOs);

        System.out.println(userBrandStatistics);

        userBrandStatisticsService.insert(userBrandStatistics);
    }


}
