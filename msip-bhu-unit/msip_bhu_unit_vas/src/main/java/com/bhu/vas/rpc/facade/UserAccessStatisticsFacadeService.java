package com.bhu.vas.rpc.facade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.bhu.vas.api.rpc.statistics.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 4/29/15.
 */
@Service
public class UserAccessStatisticsFacadeService {

    private final Logger logger = (Logger)LoggerFactory.getLogger(UserAccessStatisticsFacadeService.class);
    @Resource
    private UserAccessStatisticsService userAccessStatisticsService;


    public TailPage<UserAccessStatisticsDTO> fetchUserAccessStatistics(String date, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", date);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        TailPage<UserAccessStatistics> result = userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
        List<UserAccessStatisticsDTO> userAccessStatisticsList = new ArrayList<UserAccessStatisticsDTO>();

        for (UserAccessStatistics userAccessStatistics : result.getItems()) {
            UserAccessStatisticsDTO userAccessStatisticsDTO = new UserAccessStatisticsDTO();
            userAccessStatisticsDTO.setDate(userAccessStatistics.getDate());
            userAccessStatisticsDTO.setMac(userAccessStatistics.getMac());
            userAccessStatisticsDTO.setDevice_mac(userAccessStatistics.getDevice_mac());
            userAccessStatisticsDTO.setCreate_at(userAccessStatistics.getCreated_at());
            userAccessStatisticsDTO.setUpdate_at(userAccessStatistics.getUpdated_at());
            userAccessStatisticsList.add(userAccessStatisticsDTO);

            if (userAccessStatistics.getExtension_content() != null) {
                Map<String, Integer> extension = JsonHelper.getDTOMapKeyDto(userAccessStatistics.getExtension_content(),
                        Integer.class);
                userAccessStatisticsDTO.setExtension_content(JsonHelper.getJSONString(
                        SortMapHelper.sortMapByValue(extension), false));
            }
        }

        return new CommonPage<UserAccessStatisticsDTO>(pageNo, pageSize,
                result.getTotalItemsCount(), userAccessStatisticsList);
    }

    public TailPage<UserAccessStatisticsDTO> fetchUserAccessStatisticsWithDeviceMac(String date, String device_mac, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", date);
        mc.createCriteria().andColumnEqualTo("device_mac", device_mac);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        TailPage<UserAccessStatistics> result = userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
        List<UserAccessStatisticsDTO> userAccessStatisticsList = new ArrayList<UserAccessStatisticsDTO>();

        for (UserAccessStatistics userAccessStatistics : result.getItems()) {
            UserAccessStatisticsDTO userAccessStatisticsDTO = new UserAccessStatisticsDTO();
            userAccessStatisticsDTO.setDate(userAccessStatistics.getDate());
            userAccessStatisticsDTO.setMac(userAccessStatistics.getMac());
            userAccessStatisticsDTO.setDevice_mac(userAccessStatistics.getDevice_mac());
            userAccessStatisticsDTO.setCreate_at(userAccessStatistics.getCreated_at());
            userAccessStatisticsDTO.setUpdate_at(userAccessStatistics.getUpdated_at());
            userAccessStatisticsList.add(userAccessStatisticsDTO);

            if (userAccessStatistics.getExtension_content() != null) {
                Map<String, Integer> extension = JsonHelper.getDTOMapKeyDto(userAccessStatistics.getExtension_content(),
                        Integer.class);
                userAccessStatisticsDTO.setExtension_content(JsonHelper.getJSONString(
                        SortMapHelper.sortMapByValue(extension), false));
            }


        }

        return new CommonPage<UserAccessStatisticsDTO>(pageNo, pageSize,
                result.getTotalItemsCount(), userAccessStatisticsList);
    }
}
