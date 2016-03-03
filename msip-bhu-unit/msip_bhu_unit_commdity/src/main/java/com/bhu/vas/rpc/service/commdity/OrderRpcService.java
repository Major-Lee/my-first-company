package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
	@Override
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityid[%s] appid[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityid, appid, mac, mac, uid, context));
		return orderUnitFacadeService.createOrder(commdityid, appid, mac, umac, uid, context);
	}
	
	@Override
	public RpcResponseDTO<String> createOrderPaymentUrl(String orderid, String create_payment_url_response) {
		logger.info(String.format("createOrderPaymentUrl with orderid[%s] cpu_r[%s]", orderid, create_payment_url_response));
		return orderUnitFacadeService.createOrderPaymentUrl(orderid, create_payment_url_response);
	}
	
/*	@Override
	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid) {
		logger.info(String.format("notifyOrderPaymentSuccessed with orderid[%s]", orderid));
		return orderUnitFacadeService.notifyOrderPaymentSuccessed(orderid);
	}*/
	
	@Override
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderid) {
		logger.info(String.format("validateOrderPaymentUrl with orderid[%s]", orderid));
		return orderUnitFacadeService.validateOrderPaymentUrl(orderid);
	}

}
