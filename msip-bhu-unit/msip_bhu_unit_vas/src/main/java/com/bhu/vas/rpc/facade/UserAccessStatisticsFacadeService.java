package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserUrlStatistics;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserUrlStatisticsService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 4/29/15.
 */
@Service
public class UserAccessStatisticsFacadeService {

    //private final Logger logger = LoggerFactory.getLogger(UserAccessStatisticsFacadeService.class);
    @Resource
    private UserAccessStatisticsService userAccessStatisticsService;

    @Resource
    private UserBrandStatisticsService userBrandStatisticsService;

    @Resource
    private UserUrlStatisticsService userUrlStatisticsService;


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

    public RpcResponseDTO<List<UserBrandDTO>> fetchUserBrandStatistics(String date) {
        UserBrandStatistics userBrandStatistics = userBrandStatisticsService.getById(date);

        if (userBrandStatistics == null) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_NOTEXIST);
        } else {
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(userBrandStatistics.getInnerModels());
        }


    }

    public RpcResponseDTO<List<UserUrlDTO>> fetchUserUrlStatistics(String date) {
        UserUrlStatistics userUrlStatistics = userUrlStatisticsService.getById(date);

        if (userUrlStatistics == null) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_NOTEXIST);
        } else {
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(userUrlStatistics.getInnerModels());
        }


    }


    public TailPage<UserBrandStatisticsDTO> fetchUserBrandStatistics(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        TailPage<UserBrandStatistics> result = userBrandStatisticsService.findModelTailPageByModelCriteria(mc);
        List<UserBrandStatisticsDTO> userBrandStatisticsDTOs = new ArrayList<UserBrandStatisticsDTO>();
        for (UserBrandStatistics userBrandStatistics : result.getItems()) {
            UserBrandStatisticsDTO userBrandStatisticsDTO = new UserBrandStatisticsDTO();
            userBrandStatisticsDTO.setDate(userBrandStatistics.getId());
            userBrandStatisticsDTO.setContent(userBrandStatistics.getInnerModelJsons());
            userBrandStatisticsDTOs.add(userBrandStatisticsDTO);
        }

        return new CommonPage<UserBrandStatisticsDTO>(pageNo, pageSize,
                result.getTotalItemsCount(), userBrandStatisticsDTOs);

    }
}
