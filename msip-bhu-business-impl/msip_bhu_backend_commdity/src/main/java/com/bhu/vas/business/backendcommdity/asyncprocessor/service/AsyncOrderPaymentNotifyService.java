package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;



import com.bhu.vas.api.dto.commdity.id.StructuredExtSegment;
import com.bhu.vas.api.dto.commdity.id.StructuredId;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.rpc.commdity.helper.StructuredIdHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserDeviceFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

@Service
public class AsyncOrderPaymentNotifyService {
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private CommdityService commdityService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	/**
	 * 支付系统支付完成的通知处理
	 * @param message
	 */
	public void orderPaymentNotifyCompletedHandle(String message){
		logger.info(String.format("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyHandle: message[%s]", message));
		try{
			ResponsePaymentCompletedNotifyDTO rpcn_dto = JsonHelper.getDTO(message, ResponsePaymentCompletedNotifyDTO.class);
			if(rpcn_dto != null){
				String orderid = rpcn_dto.getOrderid();

				if(StringUtils.isEmpty(orderid)){
					throw new RuntimeException(String.format("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyHandle "
							+ "param illegal orderid[%s]", orderid));
				}
				StructuredId structuredId = StructuredIdHelper.generateStructuredId(orderid);
				StructuredExtSegment structuredExtSegment = structuredId.getExtSegment();
				//判断支付模式为收入模式(商品订单支付 如出现其他情况可扩展下一位业务占位)
				if(StructuredIdHelper.isPaymodeReceipt(structuredExtSegment)){
					orderPaymentNotifyPaymodeReceiptHandle(rpcn_dto);
				}
				//判断支付模式为支出模式(提现 如出现其他情况可扩展下一位业务占位)
				else if(StructuredIdHelper.isPaymodeExpend(structuredExtSegment)){
					orderPaymentNotifyPaymodeExpendHandle(rpcn_dto);
				}
			}
			logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyOrderPaymentHandle: message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyHandle Exception", ex);
		}
	}
	
	/**
	 * 支付模式为收入模式(商品订单支付 如出现其他情况可扩展下一位业务占位)
	 * @param rpcn_dto
	 */
	public void orderPaymentNotifyPaymodeReceiptHandle(ResponsePaymentCompletedNotifyDTO rpcn_dto){
		String orderid = rpcn_dto.getOrderid();
		boolean success = rpcn_dto.isSuccess();
		String paymented_ds = rpcn_dto.getPaymented_ds();
		//订单处理逻辑 
		Order order = orderFacadeService.validateOrderId(orderid);
		//支付完成时进行设备的uid获取并设置订单
		User bindUser = userDeviceFacadeService.getBindUserByMac(order.getMac());
		
		order = orderFacadeService.orderPaymentCompletedNotify(success, order, bindUser, paymented_ds);
		//判断订单状态为支付成功或发货成功
		Integer order_status = order.getStatus();
		if(OrderStatus.isPaySuccessed(order_status) || OrderStatus.isDeliverCompleted(order_status)){
			//判断是否为限时上网商品
			Integer commdityid = order.getCommdityid();
			Commdity commdity = commdityService.getById(commdityid);
			if(commdity != null && CommdityCategory.isInternetLimit(commdity.getCategory())){
				//进行订单分成处理逻辑
				//String dmac = order.getMac();
				double amount = Double.parseDouble(order.getAmount());
				//userWalletFacadeService.sharedealCashToUserWallet(order.getUid(), amount, orderid);
				OrderUmacType uMacType = OrderUmacType.fromKey(order.getUmactype());
				StringBuilder sb_description = new StringBuilder();
				if(uMacType != null){
					sb_description.append(uMacType.getName());
				}
				if(StringUtils.isNotEmpty(order.getPayment_type())){
					if(sb_description.length()>0)	
						sb_description.append(StringHelper.MINUS_CHAR_GAP);
					sb_description.append(order.getPayment_type());
				}
				userWalletFacadeService.sharedealCashToUserWalletWithBindUid(order.getUid(), amount, orderid,sb_description.toString());
			}
		}
	}
	
	/**
	 * 支付模式为支出模式(提现 如出现其他情况可扩展下一位业务占位)
	 * @param rpcn_dto
	 */
	public void orderPaymentNotifyPaymodeExpendHandle(ResponsePaymentCompletedNotifyDTO rpcn_dto){
		String orderId = rpcn_dto.getOrderid();
		boolean successed = rpcn_dto.isSuccess();
		userWalletFacadeService.doWithdrawNotifyFromRemote(orderId, successed);
	}
	
}
