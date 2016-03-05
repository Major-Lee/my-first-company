package com.bhu.vas.api.rpc.commdity.helper;

import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;

public class OrderHelper {

	/**
	 * 判断订单状态是否为未支付
	 * @param status
	 * @return
	 */
	public static boolean notpay(Integer status){
		if(status == null) return false;
		if(OrderStatus.NotPay.getKey().equals(status)) return true;
		return false;
	}
	/**
	 * 判断订单状态是否为支付成功
	 * @param status
	 * @return
	 */
	public static boolean paysuccessed(Integer status){
		if(status == null) return false;
		if(OrderStatus.PaySuccessed.getKey().equals(status)) return true;
		return false;
	}
	
	/**
	 * 当前状态是否小于等于未支付状态
	 * @param status
	 * @return
	 */
	public static boolean lte_notpay(Integer status){
		if(status == null) return false;
		return OrderStatus.NotPay.getKey() - status >= 0 ? true : false;
	}
}
