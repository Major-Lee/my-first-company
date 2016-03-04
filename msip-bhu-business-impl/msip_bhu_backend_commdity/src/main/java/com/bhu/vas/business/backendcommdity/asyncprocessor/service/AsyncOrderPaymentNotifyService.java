package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

@Service
public class AsyncOrderPaymentNotifyService {
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyService.class);
	
	public void notifyOrderPayment(String message){
		System.out.println("notifyOrderPayment");
	}
/*	*//**
	 * 订单支付成功后续处理
	 * @param message
	 *//*
	public void orderPaySuccessedHandle(String message){
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
