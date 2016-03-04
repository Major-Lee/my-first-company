package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;

@Service
public class AsyncOrderPaymentNotifyService {
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	/**
	 * 支付系统支付成功的通知处理
	 * @param message
	 */
	public void notifyOrderPaymentHandle(String message){
		logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s]", message));
		Integer changed_status = OrderStatus.PaySuccessed.getKey();
		Integer changed_process_status = OrderProcessStatus.PaySuccessed.getKey();
		Order order = null;
		try{
			order = orderService.getById(message);
			if(order == null)
				throw new RuntimeException(String.format("NotifyOrderPaymentHandle Order NotExist [%s]", message));
			
			//TODO:通知应用发货 如果通知成功 更新status为PaySuccessed
			
			logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle Exception", ex);
		}finally{
			orderFacadeService.orderStatusChanged(order, changed_status, changed_process_status);
		}
	}
}
