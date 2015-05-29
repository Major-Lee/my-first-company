package com.bhu.vas.web.console;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.statistics.dto.*;
import com.bhu.vas.api.vto.*;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.statistics.iservice.IStatisticsRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.msip.exception.BusinessException;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseStatus;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console")
public class ConsoleController extends BaseController {

    @Resource
    private IDeviceRestRpcService deviceRestRpcService;

    @Resource
    private IStatisticsRpcService statisticsRpcService;


    /**
     * 获取最繁忙的TOP5wifi设备
     *
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_max_busy_wifidevices", method = {RequestMethod.POST})
    public void fetch_max_busy_devices(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {

        List<WifiDeviceMaxBusyVTO> vtos = deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));

    }

    /**
     * 根据条件搜索wifi设备列表
     * region表示 需要包含的地域名称
     * excepts表示 需要不包含的地域名称 逗号分割
     * 排序以当前在线和当前在线移动设备数量
     *
     * @param keyword          可以是 mac 或 地理名称
     * @param region           北京市
     * @param excepts          广东省,上海市
     * @param newVersionDevice 新老版本设备
     * @param request
     * @param response
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_wifidevices_by_keyword", method = {RequestMethod.POST})
    public void fetch_wifidevices_by_keyword(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            //@RequestParam(required = false, value = "q") String keyword,
            @RequestParam(required = false) String mac,
            @RequestParam(required = false) String orig_swver,
            @RequestParam(required = false) String adr,
            @RequestParam(required = false) String work_mode,
            @RequestParam(required = false) String config_mode,
            @RequestParam(required = false) String devicetype,
            @RequestParam(required = false) Boolean online,
            @RequestParam(required = false, value = "nvd") Boolean newVersionDevice,
            @RequestParam(required = false, value = "region") String region,
            @RequestParam(required = false, value = "excepts") String excepts,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {

        TailPage<WifiDeviceVTO> vtos_page = deviceRestRpcService.fetchWDevicesByKeywords(mac, orig_swver,
                adr, work_mode, config_mode, devicetype, online, newVersionDevice, region, excepts, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos_page));
    }

    /**
     * 获取统计通用数据展示
     * 页面中统计数据体现：
     * a、总设备数、总用户数、在线设备数、在线用户数、总接入次数、总用户访问时长
     * b、今日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
     * c、昨日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
     *
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_statistics_general", method = {RequestMethod.POST})
    public void fetch_statistics_general(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid) {

        StatisticsGeneralVTO vto = deviceRestRpcService.fetchStatisticsGeneral();
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
    }

    /**
     * 获取wifi设备地域分布饼图
     *
     * @param request
     * @param response
     * @param regions  地域名称 逗号分割
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_region_count", method = {RequestMethod.POST})
    public void fetch_region_count(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = true) String regions) {

        List<RegionCountDTO> dtos = deviceRestRpcService.fetchWDeviceRegionCount(regions);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
    }

    /**
     * 获取最近30天内接入的wifi设备列表
     *
     * @param request
     * @param response
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_wifidevices_by_registerat", method = {RequestMethod.POST})
    public void fetch_wifidevices_by_registerat(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {

        TailPage<WifiDeviceVTO> result = deviceRestRpcService.fetchRecentWDevice(pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }

    /**
     * 根据wifi设备的id获取移动设备列表
     *
     * @param request
     * @param response
     * @param wifiId
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_handsetdevices", method = {RequestMethod.POST})
    public void fetch_handsetdevices(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = true, value = "wid") String wifiId,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {

        TailPage<HandsetDeviceVTO> result = deviceRestRpcService.fetchHDevices(wifiId, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }

    /**
     * 获取地图设备数据
     * 暂时采用读取500条wifi设备全量返回
     * @param request
     * @param response
     */
//	@ResponseBody()
//	@RequestMapping(value="/fetch_geo_map",method={RequestMethod.GET,RequestMethod.POST})
//	public void fetch_geo_map(
//			HttpServletRequest request,
//			HttpServletResponse response) {
//		
//		Collection<GeoMapVTO> result = deviceRestRpcService.fetchGeoMap();
//		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
//	}

    /**
     * 获取最繁忙的TOP5wifi设备
     *
     * @param request
     * @param response
     * @param t        年-0 季度-1 月-2 周-3 日-4
     * @param ml       代表显示几条曲线的数据 1-本日（周、月、季度、年） 2-本日、前一天（周、月、季度、年） 3-本日、前一天、前两天（周、月、季度、年）,依次类推
     *                 public static final int YEAR = 0;
     *                 public static final int YEAR_QUARTER = 1;
     *                 public static final int YEAR_MONTH = 2;
     *                 public static final int YEAR_WHICH_WEEK = 3;
     *                 public static final int YEAR_MONTH_DD = 4;
     */
    @ResponseBody()
    @RequestMapping(value = "/statistics/fetch_online_handset", method = {RequestMethod.POST})
    public void fetch_online_handset(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = true, defaultValue = "4", value = "t") int type,
            @RequestParam(required = false, defaultValue = "1", value = "ml") int ml
            //@RequestParam(required = false) String fragment
    ) {
        if (type < 0 || type > 4) {
            throw new BusinessException(ResponseStatus.Forbidden, ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
        }
        if (ml < 1) ml = 1;
        if (ml > 5) ml = 5;

        RpcResponseDTO<Map<String, Object>> rpcResult = statisticsRpcService.buildHandsetOnline4Chart(type, ml);
        if (rpcResult.getErrorCode() == null) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
        }
        //Map<String,List<String>> result = build4Chart(type,ml);
        //SpringMVCHelper.renderJson(response, ResponseSuccess.embed(build4Chart(type,ml)));
    }


    @ResponseBody()
    @RequestMapping(value = "/statistics/fetch_online_device", method = {RequestMethod.POST})
    public void fetch_online_device(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = true, defaultValue = "4", value = "t") int type,
            @RequestParam(required = false, defaultValue = "1", value = "ml") int ml
            //@RequestParam(required = false) String fragment
    ) {
        if (type < 0 || type > 4) {
            throw new BusinessException(ResponseStatus.Forbidden, ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
        }
        if (ml < 1) ml = 1;
        if (ml > 5) ml = 5;

        RpcResponseDTO<Map<String, Object>> rpcResult = statisticsRpcService.buildDeviceOnline4Chart(type, ml);
        if (rpcResult.getErrorCode() == null) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
        }
        //Map<String,List<String>> result = build4Chart(type,ml);
        //SpringMVCHelper.renderJson(response, ResponseSuccess.embed(build4Chart(type,ml)));
    }

    @ResponseBody()
    @RequestMapping(value = "/statistics/fetch_user_access_statistics", method = {RequestMethod.POST})
    public void fetch_user_access_statistics(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String device_mac,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {
        if (date == null || date.isEmpty()) {
            date = DateHelper.COMMON_HELPER.getDateText(new Date());
        }
        TailPage<UserAccessStatisticsDTO> result;
        if (device_mac != null && !device_mac.isEmpty()) {
            if (!StringHelper.isValidMac(device_mac)) {
                SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                return;
            }
            result = statisticsRpcService.fetchUserAccessStatisticsWithDeviceMac(date, device_mac, pageNo, pageSize);
        } else {
            result = statisticsRpcService.fetchUserAccessStatistics(date, pageNo, pageSize);
        }

        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }

    @ResponseBody()
    @RequestMapping(value = "/statistics/fetch_user_brand_statistics", method = {RequestMethod.POST})
    public void fetch_user_brand_statistics(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = DateHelper.COMMON_HELPER.getDateText(new Date());
        }
        RpcResponseDTO<List<UserBrandDTO>> result = statisticsRpcService.fetchUserBrandStatistics(date);


        if (result != null) {
            List<UserBrandDTO> userBrandDTOList = result.getPayload();
            List<UserBrandVTO> userBrandVTOList = new ArrayList<UserBrandVTO>();


            for (UserBrandDTO userBrandDTO : userBrandDTOList) {
                UserBrandVTO userBrandVTO = new UserBrandVTO();
                userBrandVTO.setBrand(userBrandDTO.getBrand());
                userBrandVTO.setCount(userBrandDTO.getCount());
                List<String> brandDetail = new ArrayList<String>();
                List<Integer> countDetail = new ArrayList<Integer>();

                List<UserBrandSubDTO> userBrandSubDTOList = userBrandDTO.getDetail();
                for (UserBrandSubDTO userBrandSubDTO : userBrandSubDTOList) {
                    brandDetail.add(userBrandSubDTO.getBrand());
                    countDetail.add(userBrandSubDTO.getCount());
                }
                userBrandVTO.setBrandDetail(brandDetail);
                userBrandVTO.setCountDetail(countDetail);
                userBrandVTOList.add(userBrandVTO);
            }

            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.builderSuccessRpcResponse(userBrandVTOList)));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }


    }


    @ResponseBody()
    @RequestMapping(value = "/statistics/fetch_user_url_statistics", method = {RequestMethod.POST})
    public void fetch_user_url_statistics(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = DateHelper.COMMON_HELPER.getDateText(new Date());
        }
        RpcResponseDTO<List<UserUrlDTO>> result = statisticsRpcService.fetchUserUrlStatistics(date);


        if (result != null) {
            List<UserUrlDTO> userUrlDTOList = result.getPayload();
            List<UserUrlVTO> userUrlVTOList = new ArrayList<UserUrlVTO>();


            for (UserUrlDTO userUrlDTO : userUrlDTOList) {
                UserUrlVTO userUrlVTO = new UserUrlVTO();
                userUrlVTO.setCategory(userUrlDTO.getCategory());
                userUrlVTO.setCount(userUrlDTO.getCount());
                List<String> categoryDetail = new ArrayList<String>();
                List<Integer> countDetail = new ArrayList<Integer>();

                List<UserUrlSubDTO> userUrlSubDTOList = userUrlDTO.getDetail();
                for (UserUrlSubDTO userUrlSubDTO : userUrlSubDTOList) {
                    categoryDetail.add(userUrlSubDTO.getCategory());
                    countDetail.add(userUrlSubDTO.getCount());
                }
                userUrlVTO.setCategoryDetail(categoryDetail);
                userUrlVTO.setCountDetail(countDetail);
                userUrlVTOList.add(userUrlVTO);
            }

            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.builderSuccessRpcResponse(userUrlVTOList)));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }
    }

}
