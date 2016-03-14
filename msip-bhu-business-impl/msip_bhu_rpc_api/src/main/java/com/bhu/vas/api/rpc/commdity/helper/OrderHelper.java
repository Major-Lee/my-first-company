package com.bhu.vas.api.rpc.commdity.helper;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
	/**
	 * 构建order status dto
	 * @param order
	 * @return
	 */
	public static OrderStatusDTO buildOrderStatusDTO(Order order){
		if(order == null) return null;
		
		OrderStatusDTO orderStatusDto = new OrderStatusDTO();
		BeanUtils.copyProperties(order, orderStatusDto);
		return orderStatusDto;
	}
	
	
	public static void supportedAppId(Integer appid){
		//认证appid
		if(!CommdityApplication.supported(appid)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_APPID_INVALID, new String[]{String.valueOf(appid)});
		}
	}
}
