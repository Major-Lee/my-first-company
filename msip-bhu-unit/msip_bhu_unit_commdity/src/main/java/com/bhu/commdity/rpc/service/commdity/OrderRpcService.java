package com.bhu.commdity.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.commdity.rpc.facade.OrderUnitFacadeService;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
	@Override
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityid[%s] appid[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityid, appid, mac, mac, uid, context));
		return orderUnitFacadeService.createOrder(commdityid, appid, mac, umac, uid, context);
	}
	
	@Override
	public RpcResponseDTO<String> createOrderPaymentUrl(String orderid) {
		logger.info(String.format("createOrderPaymentUrl with orderid[%s]", orderid));
		return orderUnitFacadeService.createOrderPaymentUrl(orderid);
	}
	
	@Override
	public RpcResponseDTO<Boolean> notifyOrderPaymentCompleted(String orderid) {
		logger.info(String.format("notifyOrderPaymentCompleted with orderid[%s]", orderid));
		//return orderUnitFacadeService.notifyOrderPaymentCompleted(orderid);
		return null;
	}

}
