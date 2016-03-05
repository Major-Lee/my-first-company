package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class AsyncOrderPaymentNotifyService {
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	/**
	 * 支付系统支付成功的通知处理
	 * 1:订单处理逻辑
	 * 2:分成处理逻辑
	 * @param message
	 */
	public void orderPaymentNotifyHandle(String message){
		logger.info(String.format("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyHandle: message[%s]", message));
		try{
			ResponsePaymentCompletedNotifyDTO rpcn_dto = JsonHelper.getDTO(message, ResponsePaymentCompletedNotifyDTO.class);
			if(rpcn_dto != null){
				//订单处理逻辑
				Order order = orderFacadeService.orderPaymentNotify(rpcn_dto);
				//分成处理逻辑
				String orderid = order.getId();
				String dmac = order.getMac();
				double amount = Double.parseDouble(order.getAmount());
				userWalletFacadeService.sharedealCashToUserWallet(dmac, amount, orderid);
			}
			logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyHandle Exception", ex);
		}
	}
}
