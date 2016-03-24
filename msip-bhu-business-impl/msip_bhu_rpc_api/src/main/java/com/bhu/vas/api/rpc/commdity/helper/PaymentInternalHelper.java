package com.bhu.vas.api.rpc.commdity.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
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
	
	public static final String CREATE_WITHDRAWURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/withdraw";///api/ucloud/withdrawpay";
	
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
			String requestip, String umac, String orderid, String payment_completed_url){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("payment_type", payment_type);
		api_params.put("total_fee", amount);
		api_params.put("exter_invoke_ip", requestip);
		api_params.put("umac", umac);
		api_params.put("goods_no", orderid);
		api_params.put("payment_completed_url", payment_completed_url);
		
		ResponseCreatePaymentUrlDTO rcp_dto = null;
		try {
			String response = HttpHelper.postUrlAsString(CREATE_PAYMENTURL_COMMUNICATION_API, api_params);
			//logger.info(String.format(format, args)"CreatePaymentUrlCommunication Response [%s]");
/*			System.out.println(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] req[%s]", orderid, payment_type, amount, requestip, response));*/
			logger.info(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] umac[%s] pcd_url[%s] req[%s]", orderid, payment_type, amount, requestip, umac, payment_completed_url, response));
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreatePaymentUrlDTO.class);
			}
		} catch (Exception ex) {
			logger.error(String.format("CreatePaymentUrlCommunication Response  orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] umac[%s] pcd_url[%s] Exception ", orderid, payment_type, amount, requestip, umac, payment_completed_url), ex);
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
	
	
	
	public static ResponseCreateWithdrawDTO createWithdrawUrlCommunication(
			String withdraw_type,
			String withdraw_no,
			String userId,String userName,String requestip, 
			String amount, String transcost,String taxcost,String total){
		Map<String, String> api_params = generateWithdrawApiParamMap();
		api_params.put("withdraw_type", withdraw_type);
		api_params.put("total_fee", amount);
		api_params.put("exter_invoke_ip", requestip);
		api_params.put("withdraw_no", withdraw_no);
		api_params.put("userId", userId);
		api_params.put("userName", userName);
		api_params.put("transcost", transcost);
		api_params.put("taxcost", taxcost);
		api_params.put("total", total);
		ResponseCreateWithdrawDTO rcp_dto = null;
		try {
			logger.info(String.format("CreateWithdrawUrlCommunication Start Request withdraw_no[%s] withdraw_type[%s] "
					+ "amount[%s] transcost[%s] taxcost[%s] total[%s] ip[%s]", 
					withdraw_no, 
					withdraw_type, 
					amount, 
					transcost, 
					taxcost, 
					total, 
					requestip));
			String response = HttpHelper.postUrlAsString(CREATE_WITHDRAWURL_COMMUNICATION_API, api_params);
			logger.info(String.format("CreateWithdrawUrlCommunication Response withdraw_no[%s] withdraw_type[%s] "
					+ "req[%s]", withdraw_no, withdraw_type, response));
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreateWithdrawDTO.class);
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
	
	public static void simulateWithdrawSuccessCommunication(String orderid){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("wid", orderid);
		api_params.put("status", "success");
		try {
			String response = HttpHelper.getUrlAsString("http://192.168.66.88:8005/api/ucloud/withdraw-call", api_params);
			System.out.println(response);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	public static void main(String[] args) throws Exception{
		String orderid = "10012016031700000000000000000244";
		
/*		Map<String, String> api_params = generateWithdrawApiParamMap();
		api_params.put("userName", "网文龙咔咔咔咔");
		String response = HttpHelper.postUrlAsString("http://192.168.66.88:8005/api/ucloud/withdraw", api_params, null, "utf-8");
		System.out.println(new String("\u8bf7\u63d0\u4f9b\u63d0\u73b0\u7c7b\u578b"));
*/		//simulatePaysuccessCommunication(orderid);
		//simulateWithdrawSuccessCommunication(orderid);
		//System.out.println(new String("\u53c2\u6570\u9519\u8bef:openid\u5b57\u6bb5\u4e0d\u6b63\u786e,\u8bf7\u68c0\u67e5\u662f\u5426\u5408\u6cd5"));
		//ResponseCreatePaymentUrlDTO rcp_dto = createPaymentUrlCommunication("PcWeixin","0.8","192.168.66.162",orderid);
		//String params = rcp_dto.getParams();
	}
}
