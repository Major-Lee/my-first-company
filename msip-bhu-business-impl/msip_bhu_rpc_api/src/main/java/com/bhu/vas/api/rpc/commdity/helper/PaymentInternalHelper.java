package com.bhu.vas.api.rpc.commdity.helper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.commdity.PaymentSceneChannelDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentFeeType;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
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
	//public static final String CREATE_PAYMENTURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/pay";
	//public static final String CREATE_PAYMENTURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+BusinessRuntimeConfiguration.PaymentApiPayUri;
	public static final String CREATE_PAYMENTURL_JAVA_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentJavaApiDomain+BusinessRuntimeConfiguration.PaymentJavaApiPayUri;
	//public static final String CREATE_WITHDRAWURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/withdraw";///api/ucloud/withdrawpay";
	public static final String CREATE_WITHDRAWURL_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentJavaApiDomain+BusinessRuntimeConfiguration.PaymentApiWithdrawUri;
	//模拟支付系统支付成功触发api
	//public static final String SIMULATE_PAYSUCCESS_COMMUNICATION_API = BusinessRuntimeConfiguration.PaymentApiDomain+"/api/ucloud/pay-call";
	
	//订单已经支付成功
	public static final String ERRORCODE_PAYMENT_STATUS_PAYSUCCESSED = "101";
	
	
	
	public static String getGoodsName(Locale locale, String name, String name_key){
		String ret = null;
		if(StringUtils.isNotEmpty(name_key)){
			ret = LocalI18NMessageSource.getInstance().getMessage(name_key, locale);
		}
		if(StringUtils.isNotEmpty(ret))
			return ret;
		return name;
	}
	
	
	
	/**
	 * 调用支付系统获取订单支付url
	 * @param payment_type 支付方式
	 * @param total_fee 支付金额
	 * @param exter_invoke_ip 客户端ip
	 * @param goods_no 订单id
	 * @param channel 订单渠道默认为0,1为名片打赏,2为他人代付
	 * @param orderTimeout 订单超时时间,20m代表20分钟
	 * @return
	 */
	public static ResponseCreatePaymentUrlDTO createPaymentUrlCommunication(Integer appid, String payment_type, 
			String amount, String fee_type, String requestip, String umac, String orderid, String payment_completed_url,
			String channel,String version, String payment_name, String orderTimeout){
		Map<String, String> api_params = generatePaymentApiParamMap(appid);
		if(api_params == null){
			logger.info(String.format("CreatePaymentUrlCommunication generate params error orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] umac[%s] pcd_url[%s] appid[%s] channel[%s] version[%s]", orderid, payment_type, 
					amount, requestip, umac, payment_completed_url, appid,channel,version));
			return null;
		}
		api_params.put("payment_type", payment_type);
		if (BusinessEnumType.OrderPaymentType.WapPayPal.getKey().equals(payment_type)){
			api_params.put("total_fee", fetchFinallyAmountByCurrency(fee_type, amount));
			api_params.put("fee_type", fee_type);
		}else{
			api_params.put("total_fee", amount);
		}
		api_params.put("exter_invoke_ip", requestip);
		api_params.put("umac", umac);
		api_params.put("goods_no", orderid);
		api_params.put("payment_completed_url", payment_completed_url);
		api_params.put("channel", channel);
		api_params.put("version", version);
		api_params.put("payment_name", payment_name);
		api_params.put("ot", orderTimeout);
		
		ResponseCreatePaymentUrlDTO rcp_dto = null;
		try {
/*			String apiurl = CREATE_PAYMENTURL_COMMUNICATION_API;
			if(!OrderPaymentType.Midas.getKey().equals(payment_type)){
				apiurl = CREATE_PAYMENTURL_JAVA_COMMUNICATION_API;
			}*/
			String response = HttpHelper.postUrlAsString(CREATE_PAYMENTURL_JAVA_COMMUNICATION_API, api_params);
			//logger.info(String.format(format, args)"CreatePaymentUrlCommunication Response [%s]");
/*			System.out.println(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] req[%s]", orderid, payment_type, amount, requestip, response));*/
			logger.info(String.format("CreatePaymentUrlCommunication Response orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] umac[%s] pcd_url[%s] appid[%s] channel[%s] req[%s]", orderid, payment_type, 
					amount, requestip, umac, payment_completed_url, appid, channel,response));
			if(StringUtils.isNotEmpty(response)){
				return JsonHelper.getDTO(response, ResponseCreatePaymentUrlDTO.class);
			}
		} catch (Exception ex) {
			logger.error(String.format("CreatePaymentUrlCommunication Response  orderid[%s] payment_type[%s] "
					+ "amount[%s] ip[%s] umac[%s] pcd_url[%s] appid[%s] channel[%s] version[%s] Exception ", orderid, payment_type, 
					amount, requestip, umac, payment_completed_url, appid,channel,version), ex);
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static Map<String, String> generatePaymentApiParamMap(Integer appid){
		Map<String, String> api_params = null;
		
		CommdityApplication commdityApplication = CommdityApplication.fromKey(appid);
		if(commdityApplication != null){
			api_params = new HashMap<String, String>();
			//api_params.put("appid", String.valueOf(CommdityApplication.Default.getKey()));
			api_params.put("appid", String.valueOf(commdityApplication.getKey()));
			api_params.put("secret", commdityApplication.getSecret());
		}
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
			logger.info(String.format("CreateWithdrawUrlCommunication Start Request withdraw_no[%s] userId[%s] userName[%s] withdraw_type[%s] "
					+ "amount[%s] transcost[%s] taxcost[%s] total[%s] ip[%s]", 
					withdraw_no, 
					userId,
					userName,
					withdraw_type, 
					amount, 
					transcost, 
					taxcost, 
					total, 
					requestip));
			String response = HttpHelper.postUrlAsString(CREATE_WITHDRAWURL_COMMUNICATION_API, api_params);
			System.out.println("*********微信支付提交返回值：【"+response+"】*******");
			logger.info(String.format("CreateWithdrawUrlCommunication [%s] Response withdraw_no[%s] withdraw_type[%s] "
					+ "req[%s]", CREATE_WITHDRAWURL_COMMUNICATION_API,withdraw_no, withdraw_type, response));
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
		api_params.put("appid", String.valueOf(CommdityApplication.DEFAULT.getKey()));
		api_params.put("secret", CommdityApplication.DEFAULT.getSecret());
		return api_params;
	}
	
	
	/**
	 * 模拟支付系统支付成功触发api
	 * @param orderid
	 */
/*	public static void simulatePaysuccessCommunication(String orderid){
		Map<String, String> api_params = generatePaymentApiParamMap();
		api_params.put("goods_no", orderid);
		api_params.put("status", "success");
		
		try {
			String response = HttpHelper.getUrlAsString("http://192.168.66.88:8005/api/ucloud/pay-call", api_params);
			System.out.println(response);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}*/
	
	public static void simulateWithdrawSuccessCommunication(String orderid){
		Map<String, String> api_params = generateWithdrawApiParamMap();
		api_params.put("wid", orderid);
		api_params.put("status", "success");
		try {
			String response = HttpHelper.getUrlAsString("http://192.168.66.88:8005/api/ucloud/withdraw-call", api_params);
			System.out.println(response);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	public static PaymentSceneChannelDTO formatPaymentTypeAndChannel(String payment_type, Integer channel){
		return PaymentSceneChannelDTO.builder(payment_type, channel);
	}
	
	public static String fetchFinallyAmountByCurrency(String fee_type, String amount){
		PaymentFeeType feeType = BusinessEnumType.PaymentFeeType.fromKey(fee_type);
		String result = amount;
		switch (feeType) {
		case SGD:
			result = ArithHelper.getCuttedCurrency(round(ArithHelper.mul(FeeType_Rate_SGD, Double.parseDouble(amount)),3)+"");
			break;
		case USD:
			result = ArithHelper.getCuttedCurrency(round(ArithHelper.mul(FeeType_Rate_USD, Double.parseDouble(amount)),3)+"");
			break;
		case CNY:
		default:
			break;
		}
		return result;
	}
	
	public static double round(double v, int scale){
		if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
	    BigDecimal b = new BigDecimal(Double.toString(v));
	    BigDecimal one = new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_UP).doubleValue();
	}
	  
	public static double FeeType_Rate_SGD = 0.2049;
	public static double FeeType_Rate_USD = 0.1450;
}
