package com.bhu.vas.api.rpc.commdity.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
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
	//订单已经支付成功
	public static final String ERRORCODE_PAYMENT_STATUS_PAYSUCCESSED = "101";
	/**
	 * 调用支付系统获取订单支付url
	 * @param payment_type 支付方式
	 * @param total_fee 支付金额
	 * @param exter_invoke_ip 客户端ip
	 * @param goods_no 订单id
	 * @return
	 */
	public static ResponseCreatePaymentUrlDTO createPaymentUrlCommunication(String payment_type, String amount, 
			String requestIp, String orderId){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("payment_type", payment_type);
		api_params.put("total_fee", amount);
		api_params.put("exter_invoke_ip", requestIp);
		api_params.put("goods_no", orderId);
		
		ResponseCreatePaymentUrlDTO rcp_dto = null;
		try {
			String response = HttpHelper.postUrlAsString(CREATE_PAYMENTURL_COMMUNICATION_API, api_params);
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreatePaymentUrlDTO.class);
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static Map<String, String> generatePaymentApiParamMap(){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("appid", COMMUNICATION_APPID);
		api_params.put("appsecret", COMMUNICATION_APPSECRET);
		return api_params;
	}
}
