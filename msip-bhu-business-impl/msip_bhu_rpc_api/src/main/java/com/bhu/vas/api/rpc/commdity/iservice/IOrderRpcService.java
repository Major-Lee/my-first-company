package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;


public interface IOrderRpcService {
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			Integer uid, String context);
	
	public RpcResponseDTO<String> createOrderPaymentUrl(String orderid);
	
	public RpcResponseDTO<Boolean> notifyOrderPaymentCompleted(String orderid);
}
