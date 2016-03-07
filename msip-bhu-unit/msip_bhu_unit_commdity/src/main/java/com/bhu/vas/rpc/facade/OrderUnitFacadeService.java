package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.asyn.spring.activemq.service.CommdityMessageService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class OrderUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(OrderUnitFacadeService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private CommdityService commdityService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private CommdityMessageService commdityMessageService;
	
	
	/**
	 * 生成订单
	 * @param commdityid
	 * @param appid
	 * @param mac
	 * @param umac
	 * @param uid
	 * @param context
	 * @return
	 */
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityId, Integer appId, String mac, String umac, 
			Integer uid, String context){
		try{
			orderFacadeService.validateAppId(appId);
			
			//验证用户mac和uid同时为空
			if(uid == null && StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_UID_ILLEGAL);
			}

			//生成订单
			Order order = orderFacadeService.createOrder(commdityId, appId, mac, umac, uid, context);
			OrderCreatedRetDTO orderCreatedRetDto = new OrderCreatedRetDTO();
			orderCreatedRetDto.setId(order.getId());
			orderCreatedRetDto.setAmount(order.getAmount());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderCreatedRetDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 生成订单支付url之前的订单验证
	 * @param orderId
	 * @return
	 */
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderId, Integer appId) {
		try{
			Order order = orderFacadeService.validateOrder(orderId, appId);
			//验证订单状态是否小于等于未支付
			Integer order_status = order.getStatus();
			if(!OrderHelper.lte_notpay(order_status)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID, new String[]{String.valueOf(order_status)});
			}
			
			OrderDTO orderDto = new OrderDTO();
			BeanUtils.copyProperties(order, orderDto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("ValidateOrderPaymentUrl Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 调用支付系统获取订单支付url之后
	 * @param orderId 订单id
	 * @param rcp_dto 支付系统返回的数据dto
	 * @return
	 */
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderId, ResponseCreatePaymentUrlDTO rcp_dto) {
		try{
			//ResponseCreatePaymentUrlDTO rcp_dto = JsonHelper.getDTO(create_payment_url_response, ResponseCreatePaymentUrlDTO.class);
			String paymentUrlInfo = orderFacadeService.orderPaymentUrlCreated(orderId, rcp_dto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(paymentUrlInfo);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderPaymentUrlCreated Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据用户终端mac地址进行订单的状态查询
	 * @param umac 用户mac
	 * @param orderId 订单id
	 * @param appId 应用id
	 * @return
	 */
	public RpcResponseDTO<OrderDTO> orderStatusByUmac(String umac, String orderId, Integer appId) {
		try{
			Order order = orderFacadeService.validateOrder(orderId, appId);
			
			if(!umac.equals(order.getUmac())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_INVALID);
			}
			
			OrderDTO orderDto = new OrderDTO();
			BeanUtils.copyProperties(order, orderDto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
