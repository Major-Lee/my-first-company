package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.activemq.service.CommdityMessageService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserWalletConfigsService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;
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
	
	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	
	@Resource
	private UserWalletConfigsService userWalletConfigsService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
/*	*//**
	 * 生成订单
	 * @param commdityid
	 * @param appid
	 * @param mac
	 * @param umac
	 * @param uid
	 * @param context
	 * @return
	 *//*
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context){
		try{
			orderFacadeService.supportedAppId(appid);
			
			//验证用户mac和uid同时为空
			if(uid == null && StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_UID_ILLEGAL);
			}

			//生成订单
			Order order = orderFacadeService.createOrder(commdityid, appid, mac, umac, uid, context);
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
	
	*//**
	 * 生成订单支付url之前的订单验证
	 * @param orderId
	 * @param appId
	 * @param appSecret
	 * @return
	 *//*
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderid, Integer appid) {
		try{
			Order order = orderFacadeService.validateOrder(orderid, appid);
			//验证订单状态是否小于等于未支付
			Integer order_status = order.getStatus();
			if(!OrderHelper.lte_notpay(order_status)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID, new String[]{orderid, String.valueOf(order_status)});
			}
			
			OrderDTO orderDto = OrderHelper.buildOrderDTO(order);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("ValidateOrderPaymentUrl Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	*//**
	 * 调用支付系统获取订单支付url之后
	 * @param orderId 订单id
	 * @param rcp_dto 支付系统返回的数据dto
	 * @return
	 *//*
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderid, ResponseCreatePaymentUrlDTO rcp_dto) {
		try{
			//ResponseCreatePaymentUrlDTO rcp_dto = JsonHelper.getDTO(create_payment_url_response, ResponseCreatePaymentUrlDTO.class);
			String paymentUrlInfo = orderFacadeService.orderPaymentUrlCreated(orderid, rcp_dto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(paymentUrlInfo);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderPaymentUrlCreated Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	*/
	
	/**
	 * 生成订单
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param payment_type 支付方式
	 * @param context 业务上下文
	 * @return
	 */
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer umactype, String payment_type, String context){
		try{
			orderFacadeService.supportedAppId(appid);
			
			//验证mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			String mac_lower = mac.toLowerCase();
			String umac_lower = umac.toLowerCase();
			//检查设备是否接入过
			WifiDevice wifiDevice = wifiDeviceService.getById(mac_lower);
			if(wifiDevice == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			}
			//生成订单
			String mac_dut = WifiDeviceHelper.dutDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.createOrder(commdityid, appid, mac_lower, mac_dut, umac_lower, umactype, context);
			OrderDTO orderDto = new OrderDTO();
			BeanUtils.copyProperties(order, orderDto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateOrder Exception:", ex);
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
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid, Integer appid) {
		try{
			Order order = orderFacadeService.validateOrder(orderid, appid);
			
			if(!umac.equals(order.getUmac())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_INVALID);
			}
			
			OrderStatusDTO orderStatusDto = OrderHelper.buildOrderStatusDTO(order);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderStatusDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据设备mac查询订单分页列表
	 * @param uid 用户id
	 * @param mac 用户绑定的设备mac
	 * @param umac 订单支付用户的终端mac
	 * @param status 订单状态
	 * @param dut 设备业务线
	 * @param pageNo 页码
	 * @param pageSize 分页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserOrderDTO>> orderPagesByUid(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize) {
		try{
			List<UserOrderDTO> retDtos = Collections.emptyList();
			int order_count = orderFacadeService.countOrderByParams(uid, mac, umac, status, dut);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, mac, umac, status, dut,
						pageNo, pageSize);
				if(orderList != null && !orderList.isEmpty()){
					retDtos = new ArrayList<UserOrderDTO>();
					UserOrderDTO userOrderDto = null;
					for(Order order : orderList){
						userOrderDto = new UserOrderDTO();
						userOrderDto.setId(order.getId());
						userOrderDto.setMac(order.getMac());
						userOrderDto.setUmac(order.getUmac());
						userOrderDto.setUid(order.getUid());
						userOrderDto.setUmactype(order.getUmactype());
						userOrderDto.setUmac_mf(MacDictParserFilterHelper.prefixMactch(order.getUmac(),true,false));
						userOrderDto.setAmount(order.getAmount());
						double share_amount = userWalletConfigsService.calculateSharedeal(uid, Double.parseDouble(order.getAmount()));
						userOrderDto.setShare_amount(String.valueOf(share_amount));
						if(order.getCreated_at() != null){
							userOrderDto.setCreated_ts(order.getCreated_at().getTime());
						}
						if(order.getPaymented_at() != null){
							userOrderDto.setPaymented_ts(order.getPaymented_at().getTime());
						}
						retDtos.add(userOrderDto);
					}
				}
			}
			TailPage<UserOrderDTO> returnRet = new CommonPage<UserOrderDTO>(pageNo, pageSize, order_count, retDtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderPagesByMac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
