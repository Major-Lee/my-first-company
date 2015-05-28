package com.bhu.vas.business.statistics;

import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandSubDTO;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
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
        userBrandStatistics.setId("2015-05-22");
        userBrandStatistics.setCreated_at(new Date());

        List<UserBrandSubDTO> userBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandSubDTO userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsang T1");
        userBrandSubDTO.setCount(10);
        userBrandSubDTO.setRatio("10%");
        userBrandSubDTOList.add(userBrandSubDTO);

        userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsnag T2");
        userBrandSubDTO.setCount(12);
        userBrandSubDTO.setRatio("21%");
        userBrandSubDTOList.add(userBrandSubDTO);

        userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsnag T3");
        userBrandSubDTO.setCount(23);
        userBrandSubDTO.setRatio("23%");
        userBrandSubDTOList.add(userBrandSubDTO);

        UserBrandDTO userBrandDTO = new UserBrandDTO();

        userBrandDTO.setBrand("Sumsang");

        userBrandDTO.setCount(45);
        userBrandDTO.setRatio("24%");

        userBrandDTO.setDetail(userBrandSubDTOList);

        System.out.println(JsonHelper.getJSONString(userBrandDTO));

        List<UserBrandDTO> userBrandStatisticsDTOs = new ArrayList<UserBrandDTO>();

        userBrandStatisticsDTOs.add(userBrandDTO);

        userBrandStatistics.putInnerModels(userBrandStatisticsDTOs);

        System.out.println(userBrandStatistics);

        userBrandStatisticsService.insert(userBrandStatistics);
    }


    @Test
    public void find() {
//        UserBrandStatistics userBrandStatistics = userBrandStatisticsService.getById("2015-05-23");
//
//        System.out.println(userBrandStatistics.getExtension_content());
//
//        System.out.println(userBrandStatistics.getInnerModels());
////        List<UserBrandDTO> userBrandStatisticsDTOs  =
////                JsonHelper.getDTOList(userBrandStatistics.getExtension_content(),UserBrandDTO.class);
//
//        //System.out.println(userBrandStatisticsDTOs);


        ModelCriteria mc = new ModelCriteria();
        mc.setPageNumber(1);
        mc.setPageSize(5);
        TailPage<UserBrandStatistics> result = userBrandStatisticsService.findModelTailPageByModelCriteria(mc);

        for (UserBrandStatistics userBrandStatistics : result.getItems()) {
            UserBrandStatisticsDTO userBrandStatisticsDTO = new UserBrandStatisticsDTO();
            userBrandStatisticsDTO.setDate(userBrandStatistics.getId());
            userBrandStatisticsDTO.setContent(userBrandStatistics.getInnerModelJsons());
        }

        System.out.println(result);

    }


}
