package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.asyn.spring.model.commdity.OrderCreatedDTO;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.JsonHelper;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

@Service
public class AsyncMsgHandleCommdityService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleCommdityService.class);
	
	@Resource
	private OrderService orderService;
	
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
/*		OrderCreatedDTO dto = JsonHelper.getDTO(message, OrderCreatedDTO.class);
		String orderId = dto.getOrderid();
		if(StringUtils.isNotEmpty(orderId)){
			Order order = orderService.getById(orderId);
			if(order != null){
				OrderUserAgentDTO userAgentDto = null;
				CommdityCategory commdityCategory = CommdityCategory.fromKey(order.getType());
				if(commdityCategory != null){
					switch(commdityCategory){
						case RechargeVCurrency:
							if(order.getUid() != null){
								User user = userService.getById(order.getUid());
								if(user != null){
									userAgentDto = OrderUserAgentDTO.builder(order.getId(), order.getUmac(), order.getUmactype(), 
											order.getUid(), user.getMobileno(), type, mac, uid, user_agent, created_at);
								}
							}
							break;
						default:
							break;
					}
				}
			}
		}*/
		logger.info(String.format("AsyncMsgHandleCommdityService orderCreatedHandle message[%s] successful", message));
	}
}
