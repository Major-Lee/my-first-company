package com.bhu.vas.web.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;

@Controller
@RequestMapping("/console/withdraw")
public class ConsoleWithdrawController extends BaseController {

	/**
	 * 分页提取提现申请记录
	 * @param request
	 * @param response
	 * @param uid 操作员uid
	 * @param tuid 指定谁的提现申请uid
	 * @param withdraw_status 指定提现申请的状态
	 * @param pageNo 分页no
	 * @param pageSize 分页size
	 */
    @ResponseBody()
    @RequestMapping(value = "/fetch_applies", method = {RequestMethod.POST})
    public void fetch_styles(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "tuid") int tuid,
            @RequestParam(required = false,defaultValue = "", value = "status") String withdraw_status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
		/*RpcResponseDTO<TailPage<ModuleStyleVTO>> rpcResult = vapRpcService.pagesVapStyles(uid,pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));*/
    }
    
    
    /**
     * 审核提现申请，只有状态为VP的申请可以被审核，业务实现考虑验证此状态
     * @param request
     * @param response
     * @param uid
     * @param applies applyids 逗号分割
     */
    @ResponseBody()
    @RequestMapping(value = "/verify_applies", method = {RequestMethod.POST})
    public void verify_applies(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String applies,
            @RequestParam(required = true) boolean passed
    		) {
		/*RpcResponseDTO<TailPage<ModuleStyleVTO>> rpcResult = vapRpcService.pagesVapStyles(uid,pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));*/
    }
}
