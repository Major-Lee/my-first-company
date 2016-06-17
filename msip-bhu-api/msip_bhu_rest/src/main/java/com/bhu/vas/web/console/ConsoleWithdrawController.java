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
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.statistics.FincialStatisticsVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
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
	 * 
	 * /**
	 * update by dongrui 2016-06-14 start
	 * 接口增加参数payment_type 提现类型【微信&对公账号】
	 * update by dongtui 2016-06-14 E N D
	 */
    @ResponseBody()
    @RequestMapping(value = "/fetch_applies", method = {RequestMethod.POST})
    public void fetch_applies(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "tuid") int tuid,
            @RequestParam(required = false,defaultValue = "", value = "status") String withdraw_status,
            @RequestParam(required = false, defaultValue = "weixin", value = "payment_type") String payment_type,
            @RequestParam(required = false, defaultValue = "", value = "startTime") String startTime,
            @RequestParam(required = false, defaultValue = "", value = "endTime") String endTime,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
    	ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> rpcResult = userWalletRpcService.pageWithdrawApplies(uid, tuid, withdraw_status,payment_type,startTime,endTime,pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    @ResponseBody()
    @RequestMapping(value = "/apply_status", method = {RequestMethod.POST})
    public void fetch_apply(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String applyid
    		) {
		RpcResponseDTO<String> rpcResult = userWalletRpcService.withdrawApplyStatus(uid, applyid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}
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
    	System.out.println("###########uid为："+uid);
    	System.out.println("###########applyid为："+applyid);
		RpcResponseDTO<RequestWithdrawNotifyDTO> rpcResult = userWalletRpcService.doStartPaymentWithdrawApply(uid, applyid);
		System.out.println("******rpcResult**********"+rpcResult);
		System.out.println("******level1**********"+rpcResult.hasError());
		//add by dongrui 2016-06-15 start
		if(!rpcResult.hasError()){
			RequestWithdrawNotifyDTO requestWithdrawNotifyDTO = rpcResult.getPayload();
			if(requestWithdrawNotifyDTO.getWithdraw().getPayment_type().equals("public")){
				RpcResponseDTO<UserWithdrawApplyVTO> rpcResponseDTO = userWalletRpcService.doWithdrawNotifyFromLocal(uid, applyid, true);
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponseDTO));
				return;
			}
		}
		//add by dongrui 2016-06-15 E N D 
		
		if(!rpcResult.hasError()){
			RequestWithdrawNotifyDTO withdrawNotify = rpcResult.getPayload();
			/*if(!withdrawNotify.validate()){
				//TODO:log info
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR));
				return;
			}*/
			String orderid = withdrawNotify.getOrderid();
			String order_amount = String.valueOf(withdrawNotify.getWithdraw().getRealCash());
			String order_transcost = String.valueOf(withdrawNotify.getWithdraw().getTranscost());
			String order_taxcost = String.valueOf(withdrawNotify.getWithdraw().getTaxcost());
			String order_totalamount = String.valueOf(withdrawNotify.getWithdraw().getCash());
			String requestIp = WebHelper.getRemoteAddr(request);
			ResponseCreateWithdrawDTO rcp_dto = PaymentInternalHelper.createWithdrawUrlCommunication(
					withdrawNotify.getAccount().getIdentify(),//.getType(), 
					orderid,
					OAuthType.Weichat.getType().equals(withdrawNotify.getAccount().getIdentify())?
							withdrawNotify.getAccount().getOpenid():withdrawNotify.getAccount().getAuid(),
					withdrawNotify.getAccount().getNick(),
					requestIp,
					order_amount, order_transcost, order_taxcost,order_totalamount);
			System.out.println("apply_payment step 1:"+JsonHelper.getJSONString(rcp_dto));
			if(rcp_dto == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_INVALID)));
				return;
			}
			if(!rcp_dto.isSuccess()){
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_FALSE,new String[]{new String(rcp_dto.getMsg())})));
				return;
			}
			System.out.println("apply_payment step 2 from uPay: successed"+JsonHelper.getJSONString(rcp_dto));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rcp_dto));
			/*if(ThirdpartiesPaymentType.Weichat.getType().equals(withdrawNotify.getAccount().getType())){
				//TODO:直接url访问uPay进行支付并得到成功和失败的结果,rpc写入数据结果并返回结果集
				RpcResponseDTO<UserWithdrawApplyVTO> rpcInnerResult = userWalletRpcService.doWithdrawNotifyFromLocal(uid, applyid, rcp_dto.isSuccess());
				if(!rpcInnerResult.hasError()){
					SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcInnerResult));
				}else{
					SpringMVCHelper.renderJson(response, ResponseError.embed(rpcInnerResult));
				}
			}else{
				//TODO:直接url访问uPay获取支付连接返回给客户端进行支付，客户端支付成功后，支付宝会进行callback
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rcp_dto.getParams()));
			}*/
			System.out.println("apply_payment step 3: done!");
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    @ResponseBody()
	@RequestMapping(value="/fincialStatistics", method={RequestMethod.GET,RequestMethod.POST})
	public void fincialStatistics(
			HttpServletResponse response, 
			@RequestParam(required = true) int uid,
			@RequestParam(required=true) String time){
		try{
			RpcResponseDTO<FincialStatisticsVTO> rpcResult = userWalletRpcService.fincialStatistics(time);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
