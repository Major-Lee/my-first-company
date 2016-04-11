package com.bhu.vas.web.relation;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.vto.SocialFetchFollowListVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by NeeBie on 2016/3/8.
 */
@Controller
@RequestMapping("/relation")
public class SocialRelationController extends BaseController {

    @Resource
    private ISocialRpcService socialRpcService;

    /**
     * 点赞/踩/举报
     *
     * @param response
     * @param uid
     * @param type
     */
    @ResponseBody()
    @RequestMapping(value = "/click", method = {RequestMethod.GET, RequestMethod.POST})
    public void clickPraise(
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = true) String bssid,
            @RequestParam(required = true) String type,
            @RequestParam(required = true) boolean flag) {

        RpcResponseDTO<Boolean> rpcResult = socialRpcService.clickPraise(uid, bssid.toLowerCase(), type, flag);
        if (!rpcResult.hasError())
            SpringMVCHelper.renderJson(response,
                    ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response,
                    ResponseError.embed(rpcResult));
    }



    /**
     * 分页获取关注列表
     *
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public void fetchFollowList(
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {
        RpcResponseDTO<TailPage<SocialFetchFollowListVTO>> rpcResult = socialRpcService.fetchFollowList(uid,pageNo, pageSize);
        try {
            SpringMVCHelper.renderJson(response,
                    ResponseSuccess.embed(rpcResult.getPayload()));
        } catch (Exception e) {
            SpringMVCHelper.renderJson(response,
                    ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }

    /**
     * 关注
     *
     * @param request
     * @param response
     * @param uid
     * @param hd_mac
     */
    @ResponseBody()
    @RequestMapping(value = "/follow", method = {RequestMethod.GET, RequestMethod.POST})
    public void follow(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = true) String hd_mac) {
        RpcResponseDTO<Boolean> rpcResult = socialRpcService.follow(uid, hd_mac);
        if (!rpcResult.hasError())
            SpringMVCHelper.renderJson(response,
                    ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response,
                    ResponseError.embed(rpcResult));
    }

    /**
     * 取消关注
     *
     * @param request
     * @param response
     * @param uid
     * @param hd_mac
     */
    @ResponseBody()
    @RequestMapping(value = "/unfollow", method = {RequestMethod.GET, RequestMethod.POST})
    public void unFollow(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = true) String hd_mac) {
        try {
            RpcResponseDTO<Boolean> rpcResult = socialRpcService.unFollow(uid, hd_mac);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } catch (Exception e) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_BUSINESS_ERROR));
        }
    }

}
