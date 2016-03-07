package com.bhu.vas.web.commdity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.smartwork.msip.cores.web.mvc.WebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController{
	
	@Resource
	private IOrderRpcService orderRpcService;
	
	/**
	 * 生成订单 如果是限时上网商品的订单 会返回上一个未支付的订单以保证随机金额不变
	 * @param request
	 * @param response
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param uid 用户uid
	 * @param context 业务上下文
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
	public void create(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer commdityId,
			@RequestParam(required = true) Integer appId,
			@RequestParam(required = false) String mac,
			@RequestParam(required = false) String umac,
			@RequestParam(required = false) Integer uid,
			@RequestParam(required = false) String context) {

		RpcResponseDTO<OrderCreatedRetDTO> rpcResult = orderRpcService.createOrder(commdityId, appId, mac, 
				umac, uid, context);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 获取订单的支付url
	 * 1:验证rpc请求 验证订单并返回订单信息
	 * 2:请求支付系统返回支付url
	 * 3:根据返回数据进行验证和订单状态更新
	 * @param request
	 * @param response
	 * @param orderId 订单id
	 * @param payment_type 支付方式
	 */
	@ResponseBody()
	@RequestMapping(value="/query/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
	public void query_paymenturl(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String orderId,
			@RequestParam(required = true) Integer appId,
			@RequestParam(required = true) String payment_type
			) {
		//1:验证rpc请求 验证订单并返回订单信息
		RpcResponseDTO<OrderDTO> validateResult = orderRpcService.validateOrderPaymentUrl(orderId, appId);
		if(validateResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(validateResult));
			return;
		}
		//2:请求支付系统返回支付url
		OrderDTO order_dto = validateResult.getPayload();
		String order_amount = order_dto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(payment_type, 
				order_amount, requestIp, orderId);
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID)));
			return;
		}
		//3:根据返回数据进行验证和订单状态更新
		RpcResponseDTO<String> rpcResult = orderRpcService.orderPaymentUrlCreated(orderId, rcp_dto);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 根据umac查询订单状态
	 * @param request
	 * @param response
	 * @param umac 用户mac
	 * @param orderId 订单id
	 * @param appId 应用id
	 */
	@ResponseBody()
	@RequestMapping(value="/query/umac/status",method={RequestMethod.GET,RequestMethod.POST})
	public void query_umac_status(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String umac,
			@RequestParam(required = true) String orderId,
			@RequestParam(required = true) Integer appId
			) {

		RpcResponseDTO<OrderDTO> rpcResult = orderRpcService.orderStatusByUmac(umac, orderId, appId);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
