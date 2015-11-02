package com.bhu.vas.web.manager;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.vto.agent.UserVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bluesand on 11/2/15.
 */

@Controller
@RequestMapping("/common")
public class CommonController {

    @Resource
    private IAgentRpcService agentRpcService;
    /**
     * 销售人员列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/sellor/list")
    public void pageSellorList(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {

        try {
            TailPage<UserVTO> vtos = agentRpcService.pageSellorVTO(pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }
    }

    @ResponseBody()
    @RequestMapping(value="/agent/list")
    public void pageAgentUserList(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {

        try {
            TailPage<UserVTO> vtos = agentRpcService.pageAgentUserVTO(pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }
    }
}
