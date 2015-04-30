package com.bhu.vas.rpc.facade;

import ch.qos.logback.classic.Logger;
import com.bhu.vas.api.rpc.statistics.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.Page;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

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
            userAccessStatisticsDTO.setCreate_at(userAccessStatistics.getCreated_at());
            userAccessStatisticsDTO.setUpdate_at(userAccessStatistics.getUpdated_at());
            userAccessStatisticsDTO.setExtension_content(userAccessStatistics.getExtension_content());
            userAccessStatisticsList.add(userAccessStatisticsDTO);
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
            userAccessStatisticsDTO.setCreate_at(userAccessStatistics.getCreated_at());
            userAccessStatisticsDTO.setUpdate_at(userAccessStatistics.getUpdated_at());
            userAccessStatisticsDTO.setExtension_content(userAccessStatistics.getExtension_content());
            userAccessStatisticsList.add(userAccessStatisticsDTO);
        }

        return new CommonPage<UserAccessStatisticsDTO>(pageNo, pageSize,
                result.getTotalItemsCount(), userAccessStatisticsList);
    }
}
