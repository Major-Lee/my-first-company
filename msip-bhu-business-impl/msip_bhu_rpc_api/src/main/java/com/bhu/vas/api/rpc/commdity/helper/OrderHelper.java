package com.bhu.vas.api.rpc.commdity.helper;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Order;

public class OrderHelper {
	
	/**
	 * 当前状态是否小于等于未支付状态
	 * @param status
	 * @return
	 */
	public static boolean lte_notpay(Integer status){
		if(status == null) return false;
		return OrderStatus.NotPay.getKey() - status >= 0 ? true : false;
	}
	
	/**
	 * 构建orderDTO
	 * @param order
	 * @return
	 */
	public static OrderDTO buildOrderDTO(Order order){
		if(order == null) return null;
		
		OrderDTO orderDto = new OrderDTO();
		BeanUtils.copyProperties(order, orderDto);
		
		Date createdAt = order.getCreated_at();
		if(createdAt != null){
			orderDto.setCreated_ts(createdAt.getTime());
		}
		Date paymentedAt = order.getPaymented_at();
		if(paymentedAt != null){
			orderDto.setPaymented_ts(paymentedAt.getTime());
		}
		return orderDto;
	}
}
