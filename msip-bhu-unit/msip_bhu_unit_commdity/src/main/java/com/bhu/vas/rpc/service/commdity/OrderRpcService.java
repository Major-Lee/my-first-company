package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
	@Override
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityId, Integer appId, String mac, String umac, 
			Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityId[%s] appId[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityId, appId, mac, mac, uid, context));
		return orderUnitFacadeService.createOrder(commdityId, appId, mac, umac, uid, context);
	}
	
	@Override
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderId, ResponseCreatePaymentUrlDTO rcp_dto) {
		logger.info(String.format("orderPaymentUrlCreated with orderId[%s] rcp_dto[%s]", orderId, rcp_dto));
		return orderUnitFacadeService.orderPaymentUrlCreated(orderId, rcp_dto);
	}
	
/*	@Override
	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid) {
		logger.info(String.format("notifyOrderPaymentSuccessed with orderid[%s]", orderid));
		return orderUnitFacadeService.notifyOrderPaymentSuccessed(orderid);
	}*/
	
	@Override
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderId, Integer appId) {
		logger.info(String.format("validateOrderPaymentUrl with orderId[%s] appId[%s]", orderId, appId));
		return orderUnitFacadeService.validateOrderPaymentUrl(orderId, appId);
	}
	
	@Override
	public RpcResponseDTO<OrderDTO> orderStatusByUmac(String umac, String orderId, Integer appId) {
		logger.info(String.format("orderStatusByUmac with umac[%s] orderId[%s] appId[%s]", umac, orderId, appId));
		return orderUnitFacadeService.orderStatusByUmac(umac, orderId, appId);
	}

}
