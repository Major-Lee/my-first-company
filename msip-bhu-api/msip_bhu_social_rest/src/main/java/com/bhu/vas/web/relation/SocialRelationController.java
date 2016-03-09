package com.bhu.vas.web.relation;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.vto.SocialFetchFollowListVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
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
public class SocialRelationController {

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
    @RequestMapping(value = "/click_praise", method = {RequestMethod.GET, RequestMethod.POST})
    public void clickPraise(
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = true) String bssid,
            @RequestParam(required = false, defaultValue = "up") String type
    ) {
        RpcResponseDTO<Boolean> rpcResult = socialRpcService.clickPraise(uid, bssid, type);
        if (!rpcResult.hasError())
            SpringMVCHelper.renderJson(response,
                    ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response,
                    ResponseError.embed(rpcResult));
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
        RpcResponseDTO<Boolean> rpcResult = socialRpcService.unFollow(uid, hd_mac);
        if (!rpcResult.hasError())
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }

    /**
     * 分页获取关注列表
     * @param response
     * @param uid
     * @param hd_mac
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
        @RequestMapping(value = "/fetch_followlist", method = {RequestMethod.POST})
    public void fetchFollowList(
            HttpServletResponse response,
            @RequestParam(required = true) long uid,
            @RequestParam(required = true) String hd_mac,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {
        RpcResponseDTO<TailPage<SocialFetchFollowListVTO>> rpcResult = socialRpcService.fetchFollowList(uid, hd_mac,pageNo,pageSize);
        if (rpcResult != null && !rpcResult.hasError())
            SpringMVCHelper.renderJson(response,
                    ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response,
                    ResponseError.embed(rpcResult));
    }
}
