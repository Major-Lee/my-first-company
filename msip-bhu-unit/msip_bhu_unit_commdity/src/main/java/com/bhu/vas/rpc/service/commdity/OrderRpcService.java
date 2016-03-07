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
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
	@Override
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityId, Integer appId, String appSerect, 
			String mac, String umac, Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityId[%s] appId[%s] appSerect[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityId, appId, appSerect, mac, umac, uid, context));
		return orderUnitFacadeService.createOrder(commdityId, appId, appSerect, mac, umac, uid, context);
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
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderId, Integer appId, String appSerect) {
		logger.info(String.format("validateOrderPaymentUrl with orderId[%s] appId[%s] appSerect[%s]", orderId, appId, appSerect));
		return orderUnitFacadeService.validateOrderPaymentUrl(orderId, appId, appSerect);
	}
	
	@Override
	public RpcResponseDTO<OrderDTO> orderStatusByUmac(String umac, String orderId, Integer appId, String appSerect) {
		logger.info(String.format("orderStatusByUmac with umac[%s] orderId[%s] appId[%s] appSerect[%s]", umac, orderId, appId, appSerect));
		return orderUnitFacadeService.orderStatusByUmac(umac, orderId, appId, appSerect);
	}
	
	@Override
	public RpcResponseDTO<TailPage<OrderDTO>> orderPagesByMac(Integer uid, String mac, Integer status, 
			int pageNo, int pageSize) {
		logger.info(String.format("orderPagesByMac with uid[%s] mac[%s] status[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, status, pageNo, pageSize));
		return orderUnitFacadeService.orderPagesByMac(uid, mac, status, pageNo, pageSize);
	}

}
