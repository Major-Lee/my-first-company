package com.bhu.vas.web.query;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
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
 * Created by bluesand on 3/11/16.
 */

@Controller
@RequestMapping("/n/wifi")
public class WifiNoAuthController extends BaseController {


    @Resource
    private ISocialRpcService socialRpcService;

    /**
     * 修改wifi信息,暂时只修改rate速率
     *
     * @param response
     * @param uid
     * @param bssid
     * @param rate
     */
    @ResponseBody()
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public void modify(
            HttpServletResponse response,
            @RequestParam(required = false, value = "uid") Long uid,
            @RequestParam(required = true, value = "bssid") String bssid,
            @RequestParam(required = true, value = "rate") String rate) {

        try {
            boolean ret  = socialRpcService.modifyWifi(uid, bssid.toLowerCase(), rate);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));

        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }
}
