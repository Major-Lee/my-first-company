package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IOrderRpcService {
//	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityId, Integer appId, 
//			String mac, String umac, Integer uid, String context);
	
//	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderId, ResponseCreatePaymentUrlDTO rcp_dto);
	
//	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid);
	
//	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderId, Integer appId);
	
	public RpcResponseDTO<OrderDTO> createOrder(Integer commdityid, Integer appid, String mac, String umac, 
			String payment_type, String context);
	
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid, Integer appid);
	
	public RpcResponseDTO<TailPage<UserOrderDTO>> orderPagesByUid(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize);
}
