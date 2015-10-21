package com.bhu.vas.web.financial;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bluesand on 10/21/15.
 */
@Controller
@RequestMapping("/financial")
public class FinancialController {

    @Resource
    private IAgentRpcService agentRpcService;

    @ResponseBody()
    @RequestMapping(value="/post")
    public void postFinanical(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer aid,
            @RequestParam(required = true) Double account,
            @RequestParam(required = true) String invoice,
            @RequestParam(required = true) String receipt,
            @RequestParam(required = false) String remark
    ) {
        try {
            boolean ret = agentRpcService.postAgentFinancialSettlement(uid, aid, account, invoice, receipt, remark);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }

    }


//    @ResponseBody()
//    @RequestMapping(value="/upload")
//    public void uploadFinanical(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam(required = true) Integer uid,
//            @RequestParam(required = true) Integer type,
//            @RequestParam("file") CommonsMultipartFile file
//
//    ) {
//        try {
//
//            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));
//        } catch (Exception e) {
//            e.printStackTrace();
//            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
//        }
//
//    }
}
