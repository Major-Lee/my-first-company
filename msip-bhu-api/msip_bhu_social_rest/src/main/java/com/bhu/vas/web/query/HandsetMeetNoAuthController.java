package com.bhu.vas.web.query;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.HandsetUserDetailVTO;
import com.bhu.vas.api.rpc.social.vto.WifiHandsetUserVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bluesand on 3/8/16.
 */

@Controller
@RequestMapping("/n/hd")
    public class HandsetMeetNoAuthController extends BaseController {

    @Resource
    private ISocialRpcService socialRpcService;


    /**
     * 终端扫描遇见
     *
     * @param response
     * @param uid
     * @param hd_mac
     * @param hd_macs
     * @param bssid
     * @param ssid
     * @param lat
     * @param lon
     */
    @ResponseBody()
    @RequestMapping(value = "/meet", method = {RequestMethod.POST})
    public void meet(
            HttpServletResponse response,
            @RequestParam(required = false, value = "uid") Long uid,
            @RequestParam(required = true, value = "hd_mac") String hd_mac,
            @RequestParam(required = true, value = "hd_macs") String hd_macs,
            @RequestParam(required = true, value = "bssid") String bssid,
            @RequestParam(required = true, value = "ssid") String ssid,
            @RequestParam(required = false, value = "lat") String lat,
            @RequestParam(required = false, value = "lon") String lon,
            @RequestParam(required = false, value = "addr") String addr
    ) {

        try {
            boolean ret = socialRpcService.handsetMeet(uid, hd_mac.toLowerCase(), hd_macs.toLowerCase(), bssid.toLowerCase(), ssid, lat, lon, addr);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));

        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }

    }


    /**
     * 获取终端列表
     *
     * @param response
     * @param uid
     * @param bssid
     * @param hd_macs
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch", method = {RequestMethod.POST})
    public void fetch(
            HttpServletResponse response,
            @RequestParam(required = false, value = "uid") Long uid,
            @RequestParam(required = true, value = "hd_mac") String hd_mac,
            @RequestParam(required = true, value = "bssid") String bssid,
            @RequestParam(required = true, value = "hd_macs") String hd_macs) {

        try {
            WifiHandsetUserVTO vto = socialRpcService.fetchHandsetList(uid, hd_mac.toLowerCase(), bssid.toLowerCase(), hd_macs.toLowerCase());
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
        } catch (Exception e) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }


    /**
     * 终端详情
     *
     * @param response
     * @param uid
     * @param bssid
     * @param hd_mac_self
     * @param hd_mac
     */
    @ResponseBody()
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public void detail(
            HttpServletResponse response,
            @RequestParam(required = false, value = "uid") Long uid,
            @RequestParam(required = true, value = "bssid") String bssid,
            @RequestParam(required = true, value = "hd_mac_self") String hd_mac_self,
            @RequestParam(required = true, value = "hd_mac") String hd_mac) {

        try {
            HandsetUserDetailVTO vto = socialRpcService.fetchHandsetDetail(uid, hd_mac_self.toLowerCase(), hd_mac.toLowerCase(), bssid.toLowerCase());
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
        } catch (Exception e) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }


}
