package com.bhu.commdity.rpc.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.helper.CommdityHelper;
import com.bhu.vas.business.ds.commdity.helper.OrderHelper;
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
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context){
		try{
			//验证用户mac和uid同时为空
			if(uid == null && StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_UID_ILLEGAL);
			}
			
			/**
			 * TODO:验证appid的有效性
			 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UUID_VALID_SELFOTHER_HANDSET_CHANGED);
			 */
			
			//商品信息验证
			Commdity commdity = commdityService.getById(commdityid);
			if(commdity == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
			}
			if(!CommdityHelper.onsale(commdity.getStatus())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_NOT_ONSALE);
			}
			
			//订单金额处理
			String commdity_price = commdity.getPrice();
			String amount = null;
			if(CommdityHelper.priceInterval(commdity_price)){
				Double randomPrice = CommdityHelper.randomPriceInterval(commdity_price);
				if(randomPrice == null){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_PRICE_RANDOM_INVALID);
				}
				amount = String.valueOf(randomPrice);
			}else{
				amount = commdity_price;
			}
			//订单生成
			Order order = new Order();
			order.setCommdityid(commdityid);
			order.setAppid(appid);
			order.setMac(mac);
			order.setUmac(umac);
			order.setUid(uid);
			order.setContext(context);
			order.setStatus(OrderStatus.Original.getKey());
			order.setProcess_status(OrderProcessStatus.Pending.getKey());
			order.setAmount(amount);
			orderService.insert(order);
			
			OrderDTO orderDto = new OrderDTO();
			orderDto.setOrderid(order.getId());
			orderDto.setAmount(order.getAmount());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 生成订单支付url
	 * @param orderid
	 * @return
	 */
	public RpcResponseDTO<String> createOrderPaymentUrl(String orderid) {
		Integer step_status = OrderStatus.NotPay.getKey();
		Integer step_process_status = OrderProcessStatus.NotPay.getKey();
		String pay_orderid = null;
		Order order = null;
		try{
			order = orderService.getById(orderid);
			if(order == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST);
			}
			/**
			 * TODO:调用支付系统获取支付url
			 * INTERNAL_COMMUNICATION_PAYMENTURL_FAILED
			 */
			
			//如果调用支付系统成功
			{
				//pay_orderid 赋值
				step_process_status = OrderProcessStatus.Paying.getKey();
			}
			
			return null;
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}finally{
			if(order != null){
				order.setStatus(step_status);
				order.setProcess_status(step_process_status);
				order.setPay_orderid(pay_orderid);
				orderService.update(order);
			}
		}
	}
	
	/**
	 * 支付系统通知订单支付完成
	 * @param orderid
	 * @param successed 支付是否成功
	 * @return
	 */
	public RpcResponseDTO<Boolean> notifyOrderPaymentCompleted(String orderid, boolean successed) {
		Integer step_status = OrderStatus.PaySuccessed.getKey();
		Integer step_process_status = OrderProcessStatus.PaySuccessed.getKey();
		Order order = null;
		try{
			order = orderService.getById(orderid);
			if(order == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST);
			}
			//如果订单状态为未支付,才继续进行
			if(OrderHelper.notpay(order.getStatus())){
				
			}
			
			if(successed){
				
			}
			return null;
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
