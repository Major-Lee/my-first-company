package com.bhu.vas.business.ds.commdity.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.service.OrderService;

@Service
public class OrderFacadeService {
	
	@Resource
	private OrderService orderService;
	
	/**
	 * 更新订单的状态和流程状态
	 * @param order 订单实体
	 * @param status 订单状态
	 * @param process_status 订单流程状态
	 */
	public void orderStatusChanged(Order order, Integer status, Integer process_status){
		if(order != null){
			order.setStatus(status);
			order.setProcess_status(process_status);
			orderService.update(order);
		}
	}
}
