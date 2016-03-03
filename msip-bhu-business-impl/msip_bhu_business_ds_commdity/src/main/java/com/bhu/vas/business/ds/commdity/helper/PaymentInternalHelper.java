package com.bhu.vas.business.ds.commdity.helper;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.HttpHelper;
/**
 * 支付系统调用类
 * @author tangzichao
 *
 */
public class PaymentInternalHelper {
	public static final String COMMUNICATION_APPID = "appid";
	public static final String COMMUNICATION_APPSECRET = "appsecret";
	//支付系统获取订单支付url的api地址
	public static final String CREATE_PAYMENTURL_COMMUNICATION_API = "http://upay.bhuwifi.com/api/ucloud/pay";
	
	/**
	 * 调用支付系统获取订单支付url
	 * @param payment_type 支付方式
	 * @param total_fee 支付金额
	 * @param exter_invoke_ip 客户端ip
	 * @param goods_no 订单id
	 * @return
	 */
	public static String createPaymentUrlCommunication(String payment_type, String total_fee, 
			String exter_invoke_ip, String goods_no){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("payment_type", payment_type);
		api_params.put("total_fee", total_fee);
		api_params.put("exter_invoke_ip", exter_invoke_ip);
		api_params.put("goods_no", goods_no);
		
		String response = null;
		try {
			response = HttpHelper.postUrlAsString(CREATE_PAYMENTURL_COMMUNICATION_API, api_params);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return response;
	}
	
	public static Map<String, String> generatePaymentApiParamMap(){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("appid", COMMUNICATION_APPID);
		api_params.put("appsecret", COMMUNICATION_APPSECRET);
		return api_params;
	}
}
