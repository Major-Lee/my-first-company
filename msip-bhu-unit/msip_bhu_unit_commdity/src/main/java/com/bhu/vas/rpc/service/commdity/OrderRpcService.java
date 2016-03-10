package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
/*	@Override
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityid, Integer appid, 
			String mac, String umac, Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityid[%s] appid[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityid, appid, mac, umac, uid, context));
		return orderUnitFacadeService.createOrder(commdityid, appid, mac, umac, uid, context);
	}*/
	
/*	@Override
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderid, ResponseCreatePaymentUrlDTO rcp_dto) {
		logger.info(String.format("orderPaymentUrlCreated with orderid[%s] rcp_dto[%s]", orderid, rcp_dto));
		return orderUnitFacadeService.orderPaymentUrlCreated(orderid, rcp_dto);
	}*/
	
/*	@Override
	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid) {
		logger.info(String.format("notifyOrderPaymentSuccessed with orderid[%s]", orderid));
		return orderUnitFacadeService.notifyOrderPaymentSuccessed(orderid);
	}*/
	
/*	@Override
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderid, Integer appid) {
		logger.info(String.format("validateOrderPaymentUrl with orderid[%s] appid[%s]", orderid, appid));
		return orderUnitFacadeService.validateOrderPaymentUrl(orderid, appid);
	}*/
	@Override
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			String payment_type, String context){
		logger.info(String.format("createOrder with commdityid[%s] appid[%s] mac[%s] umac[%s] payment_type[%s] context[%s]",
				commdityid, appid, mac, umac, payment_type, context));
		return orderUnitFacadeService.createOrder(commdityid, appid, mac, umac, payment_type, context);
	}
	
	@Override
	public RpcResponseDTO<OrderDTO> orderStatusByUmac(String umac, String orderid, Integer appid) {
		logger.info(String.format("orderStatusByUmac with umac[%s] orderid[%s] appid[%s]", umac, orderid, appid));
		return orderUnitFacadeService.orderStatusByUmac(umac, orderid, appid);
	}
	
	@Override
	public RpcResponseDTO<TailPage<UserOrderDTO>> orderPagesByUid(Integer uid, String mac, String umac, 
			Integer status, int pageNo, int pageSize) {
		logger.info(String.format("orderPagesByUid with uid[%s] mac[%s] umac[%s] status[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, umac, status, pageNo, pageSize));
		return orderUnitFacadeService.orderPagesByUid(uid, mac, umac, status, pageNo, pageSize);
	}

}
