package com.bhu.vas.web.commdity;

import java.util.Locale;

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

import com.bhu.vas.api.dto.commdity.CommdityOrderCommonVTO;
import com.bhu.vas.api.dto.commdity.HotPlayOrderVTO;
import com.bhu.vas.api.dto.commdity.OrderDetailDTO;
import com.bhu.vas.api.dto.commdity.OrderPaymentUrlDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.PaymentSceneChannelDTO;
import com.bhu.vas.api.dto.commdity.RewardCreateMonthlyServiceVTO;
import com.bhu.vas.api.dto.commdity.RewardQueryPagesDetailVTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
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
			@RequestParam(required = false, defaultValue = "0") Integer channel,
			@RequestParam(required = false, defaultValue = "0") String version,
			@RequestParam(required = true) String payment_type,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		long start = System.currentTimeMillis();
		
		String user_agent = request.getHeader("User-Agent");
		Locale locale = BusinessWebHelper.getLocale(request);
		//1:生成订单
		RpcResponseDTO<OrderRewardVTO> rpcResult = orderRpcService.createRewardOrder(commdityid, mac, umac, umactype,
				payment_type, context, user_agent, channel);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}

		//2:请求支付系统返回支付url
		OrderRewardVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getId();
		String order_amount = order_vto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		Integer appid = order_vto.getAppid();
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		
		
		String goods_name = PaymentInternalHelper.getGoodsName(locale, order_vto.getGoods_name(), order_vto.getName_key());
		
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, umac, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),version,goods_name,null);
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		
		logger.info(String.format("Rest Paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] mac[%s] umac[%s] rep_time[%s]", orderid, payment_type, channel, commdityid, requestIp, mac, umac,
				(System.currentTimeMillis() - start)+"ms"));
		logger.info(String.format("Rest Paymenturl Response Success orderid[%s] rcp_dto[%s]",orderid,rcp_dto.toString()));
		
		//special dispose
		/******************   注释掉此段代码为不包含代理的支付url   start *****************/
/*		String third_payinfo_json = rcp_dto.getParams();
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
		String params = JsonHelper.getJSONString(third_payinfo_json_map);*/
		/******************   注释掉此段代码为不包含代理的支付url   end *****************/
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getId());
		//retDto.setThird_payinfo(params);
		retDto.setThird_payinfo(rcp_dto.getParams());
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
	@Deprecated
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

		RpcResponseDTO<TailPage<OrderRewardVTO>> rpcResult = orderRpcService.rewardOrderPages(uid, mac, umac, 
				status, dut, 0, 0, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
	@RequestMapping(value="/reward/query/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void reward_query_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String mac,
			@RequestParam(required = false) String umac,
			@RequestParam(required = false, defaultValue = "10") Integer status,
			@RequestParam(required = false) String dut,
			@RequestParam(required = false, defaultValue = "0") long start_created_ts,
			@RequestParam(required = false, defaultValue = "0") long end_created_ts,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {

		RpcResponseDTO<TailPage<OrderRewardVTO>> rpcResult = orderRpcService.rewardOrderPages(uid, mac, umac, 
				status, dut, start_created_ts, end_created_ts, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
			@RequestParam(required = false, defaultValue = "0") Integer channel,
			@RequestParam(required = false, defaultValue = "2") Integer umactype,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		long start = System.currentTimeMillis();
		
		String user_agent = request.getHeader("User-Agent");
		//1:生成订单
		RpcResponseDTO<OrderRechargeVCurrencyVTO> rpcResult = orderRpcService.createRechargeVCurrencyOrder(uid, 
				commdityid, payment_type, umactype, user_agent);
		
		Locale locale = BusinessWebHelper.getLocale(request);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}
		//2:请求支付系统返回支付url
		OrderRechargeVCurrencyVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getId();
		String order_amount = order_vto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		Integer appid = order_vto.getAppid();
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, null, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),"0",null,null);
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		
		logger.info(String.format("Rest VCurrency Paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] rep_time[%s]", orderid, payment_type, channel, commdityid, requestIp,
				(System.currentTimeMillis() - start)+"ms"));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getId());
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
	@RequestMapping(value="/vcurrency/query/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void vcurrency_query_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, defaultValue = "10") Integer status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {

		RpcResponseDTO<TailPage<OrderRechargeVCurrencyVTO>> rpcResult = orderRpcService.rechargeVCurrencyOrderPages(uid, 
				status, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	/**
	 * 返回订单的详细信息
	 * @param request
	 * @param response
	 * @param uid
	 * @param orderid
	 */
	@ResponseBody()
	@RequestMapping(value="/query/uid/detail",method={RequestMethod.GET,RequestMethod.POST})
	public void query_uid_detail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String orderid
			) {

		RpcResponseDTO<OrderDetailDTO> rpcResult = orderRpcService.orderDetailByUid(uid, orderid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	
	/**
	 * 根据订单参数查询短信认证订单分页列表
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
	@RequestMapping(value="/sms/query/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void sms_query_pages(
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

		RpcResponseDTO<TailPage<OrderSMSVTO>> rpcResult = orderRpcService.smsOrderPages(uid, mac, umac, 
				status, dut, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/reward/newly/data",method={RequestMethod.GET,RequestMethod.POST})
	public void reward_newly_data(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid
			) {

		RpcResponseDTO<OrderRewardNewlyDataVTO> rpcResult = orderRpcService.rewardOrderNewlyDataByUid(uid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	



/**
 * 根据筛选条件返回打赏订单数据
 * @param request
 * @param response
 * @param uid 用户id
 * @param mac 设备mac
 * @param umac 支付订单的用户mac
 * @param status 订单状态 默认发货完成
 * @param pageNo 页码
 * @param pageSize 每页数量
 * @param start_created_ts 起始时间戳
 * @param end_created_ts 结束时间戳
 */
@ResponseBody()
@RequestMapping(value="/reward/query/pagesdetail",method={RequestMethod.GET,RequestMethod.POST})
public void reward_query_pages_detail(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam(required = true) Integer uid,
		@RequestParam(required = false) String mac,
		@RequestParam(required = false) String umac,
		@RequestParam(required = false, defaultValue = "10") Integer status,
		@RequestParam(required = false) String dut,
		@RequestParam(required = false, defaultValue = "0") long start_created_ts,
		@RequestParam(required = false, defaultValue = "0") long end_created_ts,
        @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
        @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
		) {
	
		RpcResponseDTO<RewardQueryPagesDetailVTO> rpcResult = orderRpcService.rewardOrderPagesDetail(uid, mac, umac, 
				status, dut, start_created_ts, end_created_ts, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}

/**
 * 创建包月订单
 * 1:生成订单
 * 2：放行用户
 * @param request
 * @param response
 * @param orderId 订单id
 * @param payment_type 支付方式
 */
@ResponseBody()
@RequestMapping(value="/physical/mini/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
public void physical_mini_paymenturl(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam(required = true) String mac,
		@RequestParam(required = true) String umac,
		@RequestParam(required = true) String uname,
		@RequestParam(required = true) String acc,
		@RequestParam(required = true) String address,
		@RequestParam(required = true) String payment_type,
		@RequestParam(required = false, defaultValue = "1") int count,
		@RequestParam(required = false) String context,
		@RequestParam(required = false, defaultValue = "false") boolean needInvoice,
		@RequestParam(required = false, defaultValue = "") String invoiceDetail,
		@RequestParam(required = false, value = "pcd_url") String payment_completed_url,
		@RequestParam(required = false, defaultValue = "2") Integer umactype,
		@RequestParam(required = false, defaultValue = "14") Integer commdityid,
		@RequestParam(required = false, defaultValue = "0") Integer channel,
		@RequestParam(required = false, defaultValue = "0") String version
		) {
		long start = System.currentTimeMillis();
		
		String user_agent = request.getHeader("User-Agent");
		Locale locale = BusinessWebHelper.getLocale(request);
		//1:生成订单
		RpcResponseDTO<RewardCreateMonthlyServiceVTO> rpcResult = orderRpcService.rewardCreateMonthlyService(commdityid, mac, umac, 
				context, umactype, channel, user_agent,uname,acc,address,count,needInvoice,invoiceDetail);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}
		
		//2:请求支付系统返回支付url
		RewardCreateMonthlyServiceVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getOrderid();
		String order_amount = order_vto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		Integer appid = order_vto.getAppid();
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		String goods_name = PaymentInternalHelper.getGoodsName(locale, order_vto.getGoods_name(), order_vto.getName_key());

		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, umac, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),version,goods_name,null);
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		
		logger.info(String.format("Rest Paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] mac[%s] umac[%s] goods_name[%s]rep_time[%s]", orderid, payment_type, channel, commdityid, requestIp, mac, umac, goods_name,
				(System.currentTimeMillis() - start)+"ms"));
		logger.info(String.format("Rest Paymenturl Response Success orderid[%s] rcp_dto[%s]",orderid,rcp_dto.toString()));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getOrderid());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}


/**
 * 创建全城热播订单
 * 1:生成订单
 * @param request
 * @param response
 * @param orderId 订单id
 * @param hpid 热播订单id
 * @param payment_type 支付方式
 */
@ResponseBody()
@RequestMapping(value="/hotplay/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
public void hot_play_paymenturl(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam(required = false, defaultValue = "17") Integer commdityid,
		@RequestParam(required = true) Integer uid,
		@RequestParam(required = true) String hpid,
		@RequestParam(required = false, defaultValue = "2") Integer umactype,
		@RequestParam(required = true) String payment_type,
		@RequestParam(required = false, value = "pcd_url") String payment_completed_url,
		@RequestParam(required = false, defaultValue = "0") Integer channel,
		@RequestParam(required = false, defaultValue = "0") String version
		) {
		long start = System.currentTimeMillis();
		String user_agent = request.getHeader("User-Agent");
		Locale locale = BusinessWebHelper.getLocale(request);
		//1:生成订单
		RpcResponseDTO<HotPlayOrderVTO> rpcResult = orderRpcService.createHotPlayOrder(commdityid, hpid, 
				umactype, payment_type, channel, user_agent);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}
		//2:请求支付系统返回支付url
		HotPlayOrderVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getOrderid();
		String order_amount = order_vto.getAmount();
		String requestIp = WebHelper.getRemoteAddr(request);
		Integer appid = order_vto.getAppid();
		if (channel == BusinessEnumType.PaymentChannelType.BHUWIFIAPP.getChannel()){
			appid = BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getKey();
		}
		long restMin = order_vto.getRestMin();
		String goods_name = PaymentInternalHelper.getGoodsName(locale, order_vto.getGoods_name(), order_vto.getName_key());
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, null, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),version,goods_name,restMin+"m");
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		logger.info(String.format("hot_play_paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] rep_time[%s] goods_name[%s]", orderid, payment_type, channel, commdityid, requestIp,
				(System.currentTimeMillis() - start)+"ms",goods_name));
		logger.info(String.format("hot_play_paymenturl Response Success orderid[%s] rcp_dto[%s]",orderid,rcp_dto.toString()));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getOrderid());
		retDto.setAdCommdityVTO(order_vto.getAdCommdityVTO());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}
	
	/**
	 * 开通软件服务请求支付url
	 * @param request
	 * @param response
	 * @param commdityid 商品id
	 * @param uid 用户id
	 * @param macs 需要开通设备的mac,多台用逗号分隔
	 * @param payment_type 支付类型
	 * @param payment_completed_url 完成支付跳转url
	 * @param channel	支付渠道
	 * @param version	支付版本
	 */
	@ResponseBody()
	@RequestMapping(value="/saas/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
	public void tech_service_paymenturl(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam(required = false, defaultValue = "25") Integer commdityid,
		@RequestParam(required = true) Integer uid,
		@RequestParam(required = true) String macs,
		@RequestParam(required = true) String payment_type,
		@RequestParam(required = false, value = "pcd_url") String payment_completed_url,
		@RequestParam(required = false, defaultValue = "5") Integer channel,
		@RequestParam(required = false, defaultValue = "0") String version
		){
		long start = System.currentTimeMillis();
		String user_agent = request.getHeader("User-Agent");
		Locale locale = BusinessWebHelper.getLocale(request);	
		//生成订单
		RpcResponseDTO<CommdityOrderCommonVTO> rpcResult = orderRpcService.createTechServiceOrder(commdityid, uid, macs, 
				payment_type, channel, user_agent);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}	
		
		CommdityOrderCommonVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getOrderid();
		String order_amount = order_vto.getAmount();
		Integer appid = order_vto.getAppid();
		String goods_name = PaymentInternalHelper.getGoodsName(locale, order_vto.getGoods_name(), order_vto.getName_key());
		String requestIp = WebHelper.getRemoteAddr(request);
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, null, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),version,goods_name,"20m");
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		logger.info(String.format("saas_paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] rep_time[%s] goods_name[%s]", orderid, payment_type, channel, commdityid, requestIp,
				(System.currentTimeMillis() - start)+"ms",goods_name));
		logger.info(String.format("saas_paymenturl Response Success orderid[%s] rcp_dto[%s]",orderid,rcp_dto.toString()));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getOrderid());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}
	
	

	@ResponseBody()
	@RequestMapping(value="/query/balance/paymenturl",method={RequestMethod.GET,RequestMethod.POST})
	public void query_balance_paymenturl(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer commdityid,
			@RequestParam(required = true) String payment_type,
			@RequestParam(required = false) String amount,
			@RequestParam(required = false, defaultValue = "0") Integer channel,
			@RequestParam(required = false) String context,
			@RequestParam(required = false, defaultValue = "0") String version,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		long start = System.currentTimeMillis();
		
		String user_agent = request.getHeader("User-Agent");
		Locale locale = BusinessWebHelper.getLocale(request);	
		//生成订单
		RpcResponseDTO<CommdityOrderCommonVTO> rpcResult = orderRpcService.createRechargeCashOrder(commdityid, uid, 
				payment_type, amount, channel, context, user_agent);
		if(rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, locale));
			return;
		}
		
		CommdityOrderCommonVTO order_vto = rpcResult.getPayload();
		String orderid = order_vto.getOrderid();
		String order_amount = order_vto.getAmount();
		Integer appid = order_vto.getAppid();
		String goods_name = PaymentInternalHelper.getGoodsName(locale, order_vto.getGoods_name(), order_vto.getName_key());
		String requestIp = WebHelper.getRemoteAddr(request);
		PaymentSceneChannelDTO paymentSceneChannelDTO = PaymentInternalHelper.formatPaymentTypeAndChannel(payment_type, channel);
		ResponseCreatePaymentUrlDTO rcp_dto = PaymentInternalHelper.createPaymentUrlCommunication(appid, paymentSceneChannelDTO.getPayment_type(), 
				order_amount, requestIp, null, orderid, payment_completed_url,paymentSceneChannelDTO.getChannel(),version,goods_name,"20m");
		if(rcp_dto == null){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID), locale));
			return;
		}
		if(!rcp_dto.isSuccess()){
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE), locale));
			return;
		}
		logger.info(String.format("query_balance_paymenturl Response Success orderid[%s] payment_type[%s] channel[%s] commdityid[%s]"
				+ "ip[%s] rep_time[%s] goods_name[%s]", orderid, payment_type, channel, commdityid, requestIp,
				(System.currentTimeMillis() - start)+"ms",goods_name));
		logger.info(String.format("query_balance_paymenturl Response Success orderid[%s] rcp_dto[%s]",orderid,rcp_dto.toString()));
		
		OrderPaymentUrlDTO retDto = new OrderPaymentUrlDTO();
		retDto.setId(order_vto.getOrderid());
		retDto.setThird_payinfo(rcp_dto.getParams());
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retDto));
	}
	
	@ResponseBody()
	@RequestMapping(value="/spend/balance",method={RequestMethod.GET,RequestMethod.POST})
	public void spend_balance(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer commdityid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = false, defaultValue = "2") Integer umactype,
			@RequestParam(required = true) String payment_type,
			@RequestParam(required = false, defaultValue = "3") Integer channel,
			@RequestParam(required = false) String context,
			@RequestParam(required = false, defaultValue = "0") String version,
			@RequestParam(required = false, value = "pcd_url") String payment_completed_url
			) {
		String user_agent = request.getHeader("User-Agent");
		//1:生成订单
		RpcResponseDTO<CommdityOrderCommonVTO> rpcResult = orderRpcService.spendBalanceOrder(commdityid, uid, mac, umac, umactype,
				payment_type, context, user_agent, channel);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
}