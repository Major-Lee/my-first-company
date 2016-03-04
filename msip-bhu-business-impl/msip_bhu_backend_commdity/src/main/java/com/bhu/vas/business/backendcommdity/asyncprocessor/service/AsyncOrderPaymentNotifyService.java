package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;


import com.bhu.vas.api.dto.commdity.internal.pay.OrderPaymentNotifyDTO;
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
	 * @param message
	 */
	public void orderPaymentNotifyHandle(String message){
		logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s]", message));
		try{
			OrderPaymentNotifyDTO opn_dto = JsonHelper.getDTO(message, OrderPaymentNotifyDTO.class);
			if(opn_dto != null){
				Order order = orderService.getById(opn_dto.getOrderid());
				if(order == null)
					throw new RuntimeException(String.format("orderPaymentNotifyHandle Order NotExist orderid[%s]", opn_dto.getOrderid()));
				
				orderFacadeService.orderPaymentNotify(order, opn_dto);
				//TODO:userWalletFacadeService.sharedealCashToUserWallet(String dmac,double cash,String orderid)
			}
			logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle Exception", ex);
		}
	}
}
