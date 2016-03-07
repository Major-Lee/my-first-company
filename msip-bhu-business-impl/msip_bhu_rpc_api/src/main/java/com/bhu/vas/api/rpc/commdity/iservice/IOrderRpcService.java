package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;


public interface IOrderRpcService {
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityId, Integer appId, String mac, String umac, 
			Integer uid, String context);
	
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderId, ResponseCreatePaymentUrlDTO rcp_dto);
	
//	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid);
	
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderId, Integer appId);
	
	public RpcResponseDTO<OrderDTO> orderStatusByUmac(String umac, String orderId, Integer appId);
}
