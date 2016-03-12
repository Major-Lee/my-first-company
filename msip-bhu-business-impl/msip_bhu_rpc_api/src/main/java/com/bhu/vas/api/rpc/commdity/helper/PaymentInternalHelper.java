package com.bhu.vas.api.rpc.commdity.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
/**
 * 支付系统调用类
 * @author tangzichao
 *
 */
public class PaymentInternalHelper {
	private final static Logger logger = LoggerFactory.getLogger(PaymentInternalHelper.class);
//	public static final String COMMUNICATION_APPID = "appid";
//	public static final String COMMUNICATION_APPSECRET = "appsecret";
	//支付系统获取订单支付url的api地址
	//public static final String CREATE_PAYMENTURL_COMMUNICATION_API = "http://upay.bhuwifi.com/api/ucloud/pay";
	public static final String CREATE_PAYMENTURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/pay";
	
	public static final String CREATE_WITHDRAWURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/pay";///api/ucloud/withdrawpay";
	
	//模拟支付系统支付成功触发api
	//public static final String SIMULATE_PAYSUCCESS_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/pay-call";
	
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
			String requestip, String orderid){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("payment_type", payment_type);
		api_params.put("total_fee", amount);
		api_params.put("exter_invoke_ip", requestip);
		api_params.put("goods_no", orderid);
		
		ResponseCreatePaymentUrlDTO rcp_dto = null;
		try {
			String response = HttpHelper.postUrlAsString(CREATE_PAYMENTURL_COMMUNICATION_API, api_params);
			//logger.info(String.format(format, args)"CreatePaymentUrlCommunication Response [%s]");
/*			System.out.println(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] req[%s]", orderid, payment_type, amount, requestip, response));*/
			logger.info(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] req[%s]", orderid, payment_type, amount, requestip, response));
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreatePaymentUrlDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreatePaymentUrlCommunication Response Exception", ex);
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static Map<String, String> generatePaymentApiParamMap(){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("appid", String.valueOf(CommdityApplication.Default.getKey()));
		api_params.put("secret", CommdityApplication.Default.getSecret());
		return api_params;
	}
	
	
	
	public static ResponseCreatePaymentUrlDTO createWithdrawUrlCommunication(String payment_type, String amount, 
			String requestip, String orderid){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("payment_type", payment_type);
		api_params.put("total_fee", amount);
		api_params.put("exter_invoke_ip", requestip);
		api_params.put("goods_no", orderid);
		
		ResponseCreatePaymentUrlDTO rcp_dto = null;
		try {
			String response = HttpHelper.postUrlAsString(CREATE_WITHDRAWURL_COMMUNICATION_API, api_params);
			logger.info(String.format("CreateWithdrawUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] req[%s]", orderid, payment_type, amount, requestip, response));
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreatePaymentUrlDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateWithdrawUrlCommunication Response Exception", ex);
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static Map<String, String> generateWithdrawApiParamMap(){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("appid", String.valueOf(CommdityApplication.Default.getKey()));
		api_params.put("secret", CommdityApplication.Default.getSecret());
		return api_params;
	}
	
	
	/**
	 * 模拟支付系统支付成功触发api
	 * @param orderid
	 */
	public static void simulatePaysuccessCommunication(String orderid){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("goods_no", orderid);
		api_params.put("status", "success");
		
		try {
			String response = HttpHelper.getUrlAsString("http://192.168.66.88:8005/api/ucloud/pay-call", api_params);
			System.out.println(response);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	public static void main(String[] args){
		String orderid = "10012016031000000000000000000050";
		simulatePaysuccessCommunication(orderid);
		System.out.println(new String("\u8be5\u5546\u54c1\u8ba2\u5355\u53f7\u4e0d\u5b58\u5728"));
		//ResponseCreatePaymentUrlDTO rcp_dto = createPaymentUrlCommunication("PcWeixin","0.8","192.168.66.162",orderid);
		//String params = rcp_dto.getParams();
	}
}
