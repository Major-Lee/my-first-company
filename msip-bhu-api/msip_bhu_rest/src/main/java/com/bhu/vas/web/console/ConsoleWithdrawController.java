package com.bhu.vas.web.console;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
//@RequestMapping("/console/withdraw")
public class ConsoleWithdrawController extends BaseController {
	@Resource
	private IUserWalletRpcService userWalletRpcService;
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
    //@RequestMapping(value = "/fetch_applies", method = {RequestMethod.POST})
    public void fetch_applies(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "tuid") int tuid,
            @RequestParam(required = false,defaultValue = "", value = "status") String withdraw_status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
		RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> rpcResult = userWalletRpcService.pagesWithdrawApplies(uid, tuid, withdraw_status, pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    
    /**
     * 审核提现申请，只有状态为VP的申请可以被审核，业务实现考虑验证此状态
     * @param request
     * @param response
     * @param uid
     * @param applies applyids 逗号分割
     */
    @ResponseBody()
    //@RequestMapping(value = "/verify_applies", method = {RequestMethod.POST})
    public void verify_applies(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String applyid,
            @RequestParam(required = true,defaultValue="false") boolean passed
    		) {
		RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.verifyApplies(uid, applyid, passed);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
}
