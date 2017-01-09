package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.commdity.OrderCreatedDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.OrdersFinishCountStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.UserOrderDetailsHashService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.JsonHelper;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

@Service
public class AsyncMsgHandleCommdityService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleCommdityService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 订单创建成功后续处理
	 * @param message
	 */
	public void orderCreatedHandle(String message){
		logger.info(String.format("AsyncMsgHandleCommdityService orderCreatedHandle message[%s]", message));
		OrderCreatedDTO dto = JsonHelper.getDTO(message, OrderCreatedDTO.class);
		String orderId = dto.getOrderid();
		if(StringUtils.isNotEmpty(orderId)){
			Order order = orderService.getById(orderId);
			WifiDevice wifiDevice = wifiDeviceService.getById(order.getMac());
			if(order != null){
				OrderUserAgentDTO userAgentDto = null;
				CommdityCategory commdityCategory = CommdityCategory.fromKey(order.getType());
				if(commdityCategory != null){
					switch(commdityCategory){
//						case RechargeVCurrency:
//							if(order.getUid() != null){
//								User user = userService.getById(order.getUid());
//								if(user != null){
//									userAgentDto = OrderUserAgentDTO.builder(order.getId(), order.getUmac(), order.getUmactype(), 
//											order.getUid(), user.getMobileno(), order.getType(), order.getMac(), order.getUid(), order.getUser_agent(), order.getCreated_at(),wifiDevice.getWan_ip(),wifiDevice.getIp());
//								}
//							}
//							break;
						case SMSInternetLimit:
								userAgentDto = OrderUserAgentDTO.builder(order.getId(), order.getUmac(), order.getUmactype(), 
										order.getUid(), order.getContext(), order.getType(), order.getMac(), order.getUid(), order.getUser_agent(), order.getCreated_at(),wifiDevice.getWan_ip(),wifiDevice.getIp());
							break;
						case RewardInternetLimit:
								userAgentDto = OrderUserAgentDTO.builder(order.getId(), order.getUmac(), order.getUmactype(), 
										order.getUid(), null, order.getType(), order.getMac(), order.getUid(), order.getUser_agent(), order.getCreated_at(),wifiDevice.getWan_ip(),wifiDevice.getIp());
							break;
						default:
							break;
					}
					if (userAgentDto != null) {
						UserOrderDetailsHashService.getInstance().addUserOrderDetail(userAgentDto.getMac(),userAgentDto.getUmac() , userAgentDto);
					}
				}
			}
		}
		logger.info(String.format("AsyncMsgHandleCommdityService orderCreatedHandle message[%s] successful", message));
	}
	
	public void orderPaySuccessedHandle(String message){
		logger.info(String.format("AsyncMsgHandleCommdityService orderPaySuccessedHandle message[%s] start.", message));
		long result = OrdersFinishCountStringService.getInstance().incrOrdersRecent7DaysKey();
		logger.info(String.format("AsyncMsgHandleCommdityService orderPaySuccessedHandle result[%s]", result));
		logger.info(String.format("AsyncMsgHandleCommdityService orderPaySuccessedHandle message[%s] end.", message));
	}
}
