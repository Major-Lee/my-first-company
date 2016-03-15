package com.bhu.vas.web.comment;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.WifiVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bluesand on 3/15/16.
 */
public class WifiInfoController extends BaseController {

    @Resource
    private ISocialRpcService socialRpcService;



    /**
     * 评论wifi
     * @param response
     * @param bssid
     * @param uid
     */
    @ResponseBody()
    @RequestMapping(value="/wifi/detail",method={RequestMethod.POST})
    public void detail(
            HttpServletResponse response,
            @RequestParam(required = true, value = "bssid") String bssid,
            @RequestParam(required = true, value = "uid") long uid) {

        WifiVTO vto = socialRpcService.fetchWifiDetail(uid, bssid);

        try {
            if (vto != null) {
                SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
            } else {
                SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }


    }

}
