package com.bhu.vas.api.rpc.commdity.helper;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class OrderHelper {
	/**
	 * 生成订单id
	 * 订单id规则 
	 * 4位应用id+8位日期+8位扩展占位+autoid
	 * @param appid 应用id
	 * @param ext_segment 8位扩展占位
	 * @return
	 */
	public static String generateOrderId(Integer appid, String ext_segment, Long autoId){
		//应用id验证
		if(appid == null || appid < 0 || appid > 9999){
			throw new RuntimeException(String.format("Generate Order Id Appid [%s] Illegal ", appid));
		}
		//如果扩展占位为空 则生成8位扩展占位字符串补零
		if(StringUtils.isEmpty(ext_segment)){
			ext_segment = String.format("%08d", 0);
		}
		//扩展占位验证
		if(ext_segment.length() != 8){
			throw new RuntimeException(String.format("Generate Order Id Ext_segment [%s] Illegal ", ext_segment));
		}
		//流水id验证
		//Long autoId = SequenceService.getInstance().getNextId(Order.class.getName());
		if(autoId == null || String.valueOf(autoId).length() > 12){
			throw new RuntimeException(String.format("Generate Order Id AutoId [%s] Illegal ", autoId));
		}
		
		StringBuffer orderId = new StringBuffer();
		//4位应用id
		orderId.append(String.format("%04d", appid));
		//8位日期 yyyyMMdd
		orderId.append(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern7));
		//8位扩展占位
		orderId.append(ext_segment);
		//12位autoId
		orderId.append(String.format("%012d", autoId));
		
		return orderId.toString();
	}
	
	public static String generateOrderId(Integer appid, Long autoId){
		return generateOrderId(appid, null, autoId);
	}
	
	/**
	 * 生成订单id的扩展业务占位
	 * @param ext_items 按占位顺序传入扩展占位 (没有传入的补零占位)
	 * @return
	 */
	public static String generateOrderExtSegment(int... ext_items){
		if(ext_items == null || ext_items.length == 0 || ext_items.length > 8){
			return String.format("%08d", 0);
		}
		StringBuffer ext_segment = new StringBuffer();
		for(int ext_item : ext_items){
			ext_segment.append(ext_item);
		}
		int ext_segment_int = Integer.parseInt(ext_segment.toString());
		return String.format("%08d", ext_segment_int);
	}
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
