package com.bhu.vas.web.payment;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderPaymentUrlDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.business.ds.payment.service.PaymentOrderService;
import com.smartwork.msip.cores.web.mvc.WebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/payment")
public class PaymentController extends BaseController{
	
	@Resource
	private PaymentOrderService paymentOrderService;
	
	@ResponseBody()
	@RequestMapping(value="/order/{orderid}",method={RequestMethod.GET,RequestMethod.POST})
	public void query_paymenturl(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long orderid
			) {
		long start = System.currentTimeMillis();
		
		PaymentOrder order = paymentOrderService.getById(orderid);
		
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
		
		//1:生成订单
		RpcResponseDTO<OrderDTO> rpcResult = orderRpcService.createOrder(commdityid, appid, mac, umac, umactype,
				payment_type, context);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			return;
		}
		//2:请求支付系统返回支付url
		OrderDTO order_dto = rpcResult.getPayload();
		String orderid = order_dto.getId();
		String order_amount = order_dto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(payment_type, 
				order_amount, requestIp, umac, orderid, payment_completed_url);
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID)));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE)));
			return;
		}
		
		logger.info(String.format("Rest Paymenturl Response Success orderid[%s] payment_type[%s] commdityid[%s]"
				+ "ip[%s] mac[%s] umac[%s] rep_time[%s]", orderid, payment_type, commdityid, requestIp, mac, umac,
				(System.currentTimeMillis() - start)+"ms"));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_dto.getId());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}
}
