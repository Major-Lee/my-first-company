package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

@Service
public class AsyncMsgHandleCommdityService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleCommdityService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	/**
	 * 订单支付成功后续处理
	 * @param message
	 */
/*	public void orderPaySuccessedHandle(String message){
		logger.info(String.format("AsyncMsgHandleCommdityService orderPaySuccessedHandle message[%s]", message));
		OrderPaySuccessedDTO dto = JsonHelper.getDTO(message, OrderPaySuccessedDTO.class);
		String orderId = dto.getOrderid();
		if(StringUtils.isNotEmpty(orderId)){
			Order order = orderService.getById(orderId);
			if(order != null){
				//判断订单状态为支付完成
				if(OrderHelper.paysuccessed(order.getStatus())){
					//TODO:通知app发货
					{
						//如果通知成功 则更新状态
						Integer changed_status = OrderStatus.DeliverCompleted.getKey();
						Integer changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
						orderFacadeService.orderStatusChanged(order, changed_status, changed_process_status);
					}
				}
			}
		}
		logger.info(String.format("AsyncMsgHandleCommdityService orderPaySuccessedHandle message[%s] successful", message));
	}*/
}
