package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderCreatedRetDTO;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;


public interface IOrderRpcService {
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context);
	
	public RpcResponseDTO<String> createOrderPaymentUrl(String orderid, String create_payment_url_response);
	
//	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid);
	
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderid);
}
