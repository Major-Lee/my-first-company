package com.bhu.vas.api.rpc.commdity.helper;

import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;

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
}
