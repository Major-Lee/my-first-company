package com.bhu.vas.web.handset;

import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.HandsetUserDetailVTO;
import com.bhu.vas.api.rpc.social.vto.WifiHandsetUserVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.smartwork.msip.jdo.ResponseSuccessCode;
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
@RequestMapping("/hd")
    public class HandsetController extends BaseController {

    @Resource
    private ISocialRpcService socialRpcService;

    /**
     * 修改终端信息,暂时只提供修改终端的备注
     *
     * @param response
     * @param uid
     * @param hd_mac
     * @param nick
     */
    @ResponseBody()
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public void modify(
            HttpServletResponse response,
            @RequestParam(required = true, value = "uid") Long uid,
            @RequestParam(required = true, value = "hd_mac") String hd_mac,
            @RequestParam(required = true, value = "nick") String nick) {

        try {
            boolean ret  = socialRpcService.modifyHandset(uid, hd_mac.toLowerCase(), nick);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }




}
