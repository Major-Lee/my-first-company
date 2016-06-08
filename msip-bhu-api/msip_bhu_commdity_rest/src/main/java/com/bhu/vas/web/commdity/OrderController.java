package com.bhu.vas.web.commdity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderPaymentUrlDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.dto.commdity.UserRechargeVCurrencyOrderDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.web.mvc.WebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController{
	private final static Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Resource
	private IOrderRpcService orderRpcService;

	/**
	 * 获取打赏订单的支付url
	 * 1:生成订单
	 * 2:请求支付系统返回支付url
	 * @param request
	 * @param response
	 * @param orderId 订单id
	 * @param payment_type 支付方式
	 */
	@ResponseBody()
	@RequestMapping(value="/query/umac/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
	public void query_paymenturl(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = false) String context,
			@RequestParam(required = false, defaultValue = "2") Integer umactype,
			@RequestParam(required = false, defaultValue = "1") Integer commdityid,
			@RequestParam(required = true) String payment_type,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		long start = System.currentTimeMillis();
		
		//1:生成订单
		RpcResponseDTO<OrderDTO> rpcResult = orderRpcService.createRewardOrder(commdityid, mac, umac, umactype,
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
		Integer appid = order_dto.getAppid();
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, payment_type, 
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
		
		//special dispose
		String third_payinfo_json = rcp_dto.getParams();
		Map<String, Object> third_payinfo_json_map = JsonHelper.getMapFromJson(third_payinfo_json);
		String third_pay_url = (String)third_payinfo_json_map.get("url");
		String proxy_third_pay_url = null;
		try {
			proxy_third_pay_url = "http://192.168.66.7/test/proxy_pay.html?param="+URLEncoder.encode(third_pay_url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			proxy_third_pay_url = third_pay_url;
		}
		third_payinfo_json_map.put("url", proxy_third_pay_url);
		String params = JsonHelper.getJSONString(third_payinfo_json_map);
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_dto.getId());
		retDto.setThird_payinfo(params);
		//retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
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
			@RequestParam(required = true) String orderid
			) {

		RpcResponseDTO<OrderStatusDTO> rpcResult = orderRpcService.orderStatusByUmac(umac, orderid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 根据订单参数查询打赏订单分页列表
	 * @param request
	 * @param response
	 * @param uid 用户id
	 * @param mac 设备mac
	 * @param umac 支付订单的用户mac
	 * @param status 订单状态 默认发货完成
	 * @param pageNo 页码
	 * @param pageSize 每页数量
	 */
	@ResponseBody()
	@RequestMapping(value="/query/uid/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void query_uid_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String mac,
			@RequestParam(required = false) String umac,
			@RequestParam(required = false, defaultValue = "10") Integer status,
			@RequestParam(required = false) String dut,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {

		RpcResponseDTO<TailPage<UserOrderDTO>> rpcResult = orderRpcService.rewardOrderPagesByUid(uid, mac, umac, 
				status, dut, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
	/**
	 * 获取充值虎钻订单的支付url
	 * 1:生成订单
	 * 2:请求支付系统返回支付url
	 * @param request
	 * @param response
	 * @param uid
	 * @param appid
	 * @param commdityid
	 * @param payment_type
	 * @param payment_completed_url
	 */
	@ResponseBody()
	@RequestMapping(value="/query/vcurrency/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
	public void query_vcurrency_paymenturl(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer commdityid,
			@RequestParam(required = true) String payment_type,
			@RequestParam(required = false, defaultValue = "2") Integer umactype,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		long start = System.currentTimeMillis();
		
		//1:生成订单
		RpcResponseDTO<OrderRechargeVCurrencyDTO> rpcResult = orderRpcService.createRechargeVCurrencyOrder(uid, commdityid, payment_type, umactype);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			return;
		}
		//2:请求支付系统返回支付url
		OrderRechargeVCurrencyDTO order_dto = rpcResult.getPayload();
		String orderid = order_dto.getId();
		String order_amount = order_dto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		Integer appid = order_dto.getAppid();
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, payment_type, 
				order_amount, requestIp, null, orderid, payment_completed_url);
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
		
		logger.info(String.format("Rest VCurrency Paymenturl Response Success orderid[%s] payment_type[%s] commdityid[%s]"
				+ "ip[%s] rep_time[%s]", orderid, payment_type, commdityid, requestIp,
				(System.currentTimeMillis() - start)+"ms"));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_dto.getId());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}
	
	/**
	 * 根据订单参数查询充值虎钻订单分页列表
	 * @param request
	 * @param response
	 * @param uid 用户id
	 * @param status 订单状态 默认发货完成
	 * @param pageNo 页码
	 * @param pageSize 每页数量
	 */
	@ResponseBody()
	@RequestMapping(value="/query/vcurrency/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void query_vcurrency_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, defaultValue = "10") Integer status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {

		RpcResponseDTO<TailPage<UserRechargeVCurrencyOrderDTO>> rpcResult = orderRpcService.rechargeVCurrencyOrderPagesByUid(uid, 
				status, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 根据uid查询订单状态
	 * @param request
	 * @param response
	 * @param umac 用户mac
	 * @param orderId 订单id
	 * @param appId 应用id
	 */
	@ResponseBody()
	@RequestMapping(value="/query/uid/status",method={RequestMethod.GET,RequestMethod.POST})
	public void query_uid_status(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String orderid
			) {

		RpcResponseDTO<OrderStatusDTO> rpcResult = orderRpcService.orderStatusByUid(uid, orderid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
