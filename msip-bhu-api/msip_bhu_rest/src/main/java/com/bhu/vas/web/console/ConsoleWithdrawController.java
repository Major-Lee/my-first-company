package com.bhu.vas.web.console;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/withdraw")
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
    @RequestMapping(value = "/fetch_applies", method = {RequestMethod.POST})
    public void fetch_applies(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "tuid") int tuid,
            @RequestParam(required = false,defaultValue = "", value = "status") String withdraw_status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
		RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> rpcResult = userWalletRpcService.pageWithdrawApplies(uid, tuid, withdraw_status, pageNo, pageSize);
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
/*    @ResponseBody()
    @RequestMapping(value = "/verify_applies", method = {RequestMethod.POST})
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
    }*/
    
    /**
     * 微信是直接调用远端uPay url进行支付成功或失败
     * 支付宝是返回支付url由用户打开进行支付，支付结果有支付宝callback uPay,uPay再通知我们
     * @param request
     * @param response
     * @param uid
     * @param applyid
     */
    @ResponseBody()
    @RequestMapping(value = "/apply_payment", method = {RequestMethod.POST})
    public void apply_payment(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String applyid
    		) {
		RpcResponseDTO<RequestWithdrawNotifyDTO> rpcResult = userWalletRpcService.doStartPaymentWithdrawApply(uid, applyid);
		if(!rpcResult.hasError()){
			RequestWithdrawNotifyDTO withdrawNotify = rpcResult.getPayload();
			if(!withdrawNotify.validate()){
				//TODO:log info
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR));
			}
			String orderid = withdrawNotify.getOrderid();
			String order_amount = String.valueOf(withdrawNotify.getWithdraw().getCash());
			String requestIp = WebHelper.getRemoteAddr(request);
			ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createWithdrawUrlCommunication(withdrawNotify.getAccount().getType(), 
					order_amount, requestIp, orderid);
			System.out.println("apply_payment step 1:"+JsonHelper.getJSONString(rcp_dto));
			if(rcp_dto == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_INVALID)));
				return;
			}
			if(!rcp_dto.isSuccess()){
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_FALSE)));
				return;
			}
			System.out.println("apply_payment step 2: successed");
			if(ThirdpartiesPaymentType.Weichat.getType().equals(withdrawNotify.getAccount().getType())){
				//TODO:直接url访问uPay进行支付并得到成功和失败的结果,rpc写入数据结果并返回结果集
				RpcResponseDTO<UserWithdrawApplyVTO> rpcInnerResult = userWalletRpcService.doWithdrawNotifyFromLocal(uid, applyid, rcp_dto.isSuccess());
				if(!rpcInnerResult.hasError()){
					SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcInnerResult));
				}else{
					SpringMVCHelper.renderJson(response, ResponseError.embed(rpcInnerResult));
				}
			}else{
				//TODO:直接url访问uPay获取支付连接返回给客户端进行支付，客户端支付成功后，支付宝会会callback
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rcp_dto.getParams()));
			}
			System.out.println("apply_payment step 3: done!");
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
}
