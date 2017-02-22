package com.bhu.vas.web.service;

import static com.bhu.vas.web.payments.util.SampleConstants.clientID;
import static com.bhu.vas.web.payments.util.SampleConstants.clientSecret;
import static com.bhu.vas.web.payments.util.SampleConstants.mode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.ds.payment.service.PaymentParameterService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.bhu.vas.web.payments.servlet.PaymentWithPayPalServlet;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.localunit.RandomPicker;
/**
 * Paypal消息处理流程逻辑类
 * @author pamchen-1
 *
 */
@Service
public class PayPalService {
	private int paypalWarningCount;
	private String paypalWarningTime = "";
	private String paypalErrorTime = "";
	
	@Autowired
	PayLogicService payLogicService;
	@Autowired
    PayHttpService payHttpService;
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;
	@Resource
	PaymentOutimeErrorLogService paymentOutimeErrorLogService;
	@Resource
	PaymentParameterService paymentParameterService;
	
	private static final Logger logger = Logger.getLogger(PaymentWithPayPalServlet.class);
//	public static final String WebhookId = "2ND47987AC600853R";
	public static final String WebhookId = "8HC53929NS051904L";  ///live pays.bhuwifi.com
	
	
	
	public PaymentTypeVTO doPaypal(HttpServletResponse response,
    		HttpServletRequest request,
    		String version,
    		String total_fee,
    		String out_trade_no,
    		String ip,
    		String return_url,
    		String usermac,
    		String paymentName,
    		String appid) {
    	PaymentTypeVTO result = new PaymentTypeVTO();
    	if(ip == "" || ip == null){
    		ip = "213.42.3.24";
    	}
    	if(usermac.equals("")){
    		usermac = RandomPicker.randString(BusinessHelper.letters, 8);
    	}
    	String total_fee_fen = BusinessHelper.getMoney(total_fee);
    	long get_midas_reckoning_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    	String reckoningId = payLogicService.createPaymentId(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_PAYPAL.i18n(),usermac,paymentName,appid);
    	long get_midas_reckoning_end = System.currentTimeMillis() - get_midas_reckoning_begin; // 这段代码放在程序执行后
    	logger.info(out_trade_no+"paypal支付获取支付流水号耗时：" + get_midas_reckoning_end + "毫秒");

		if (StringUtils.isNotBlank(return_url)) {
			logger.info(String.format("get paypal location [%s] ",return_url));
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(return_url);
			long insert_midas_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
			paymentAlipaylocationService.insert(orderLocation);
			long insert_midas_url_end = System.currentTimeMillis() - insert_midas_url_begin; // 这段代码放在程序执行后
			logger.info(out_trade_no+"paypal插入支付完成URL耗时：" + insert_midas_url_end + "毫秒");
			logger.info(String.format("apply paypal set location reckoningId [%s] location [%s]  insert finished.",reckoningId,return_url));
			if(return_url.contains("payokurl=")){
				String[] Str =return_url.split("payokurl=");
				if( Str.length > 0){
					return_url = Str[1];
				}
			}
		}
		
		return_url = PayHttpService.PAYPAL_RETURN_URL;
		long get_midas_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		String results = createPayment(reckoningId,paymentName,total_fee, return_url);
		long get_midas_url_end = System.currentTimeMillis() - get_midas_url_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"请求paypal获取支付URL耗时：" + get_midas_url_end + "毫秒");
		int nowOutTimeLevel = payHttpService.getOt();
		
		try{
			if(get_midas_url_end > nowOutTimeLevel){
				
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "Midas",get_midas_url_end+"");
				if(get_midas_url_end > 7000){
					paypalWarningCount++;
				}
				if(get_midas_url_end > 30000){
					String sendMsg = payLogicService.updatePaymentParam(2,smsg,paypalErrorTime);
					if(!StringUtils.isBlank(sendMsg)){
						paypalErrorTime =sendMsg;
					}
				}	
				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
				paymentNowpayErrorLog.setId(reckoningId);
				paymentNowpayErrorLog.setOrder_id(out_trade_no);
				paymentNowpayErrorLog.setOt(new Long(get_midas_url_end).intValue());
				paymentNowpayErrorLog.setC_type("Midas");
				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
			}
			
			switch (paypalWarningCount) {
			case 3:
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "Midas",get_midas_url_end+"");
				String resp = "ture";
				String acc = PayHttpService.Internal_level2_warning_man;
				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
				if(!paypalWarningTime.equals(curDate)){
					paypalWarningTime = curDate;
					SendMailHelper.doSendMail(2,smsg);
					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
				}
				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
				paypalWarningCount = 0;
				break;
			default:
				break;
			}
		}catch(Exception e){
			logger.info(String.format("apply midas catch exception [%s]",e.getMessage()+e.getCause()));
		}
		
		logger.info(String.format("apply paypal results [%s]",results));
		if("error".equalsIgnoreCase(results)){
			result.setType("FAIL");
			result.setUrl("支付请求失败");
			return result;
		}else{
			result.setType("http");
			result.setUrl(results);
			return result;
		}
    }
	public String createPayment(String recokoningId ,String name,String pay_amount,String returnUrl) {
		String result = "error";
		
		Payment createdPayment = null;
		// ### Api Context
		APIContext apiContext = new APIContext(clientID, clientSecret, mode);
		Details details = new Details();
		//details.setShipping("0");
		details.setSubtotal(pay_amount);
		//details.setTax("0");
		Amount amount = new Amount();
//		amount.setCurrency("USD");
		amount.setCurrency("SGD");
		amount.setTotal(pay_amount);
		amount.setDetails(details);

		// ###Transaction
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDescription("BHU service");
		transaction.setInvoiceNumber(recokoningId);
		// ### Items
		Item item = new Item();
		item.setName("BHU Reward").setQuantity("1").setCurrency("SGD").setPrice(pay_amount);
		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<Item>();
		items.add(item);
		itemList.setItems(items);
		
		transaction.setItemList(itemList);
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		// ###Payer
		// A resource representing a Payer that funds a payment
		// Payment Method
		// as 'paypal'
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		// ###Payment
		// A Payment Resource; create one using
		// the above types and intent as 'sale'
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		// ###Redirect URLs
		RedirectUrls redirectUrls = new RedirectUrls();
		String cancelUrl = PayHttpService.WEB_NOTIFY_URL;
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(returnUrl);
		payment.setRedirectUrls(redirectUrls);


		String accessToken = "";
		try {
			createdPayment = payment.create(apiContext);
			String paymentId = createdPayment.getId();
			logger.info("Created payment with id = "+ paymentId + " and status = "	+ createdPayment.getState());
			// ###Payment Approval Url
			Iterator<Links> links = createdPayment.getLinks().iterator();
			while (links.hasNext()) {
				Links link = links.next();
				if (link.getRel().equalsIgnoreCase("approval_url")) {
					result = link.getHref();
					accessToken = BusinessHelper.getTokenStr(result.trim());
				}
			}
			//update out_trade_no and accessToken
			logger.info(String.format("get paypal paymentId [%s] accessToken [%s]",paymentId,accessToken));
			payLogicService.updateReckoningByThirdInfos(recokoningId,paymentId,accessToken);
			
		} catch (PayPalRESTException e) {
			System.out.println("PayPalREST catch Exception"+e.getMessage() +e.getCause());
		}
	
		return result;
	}
	
	// Simple helper method to help you extract the headers from HttpServletRequest object.
		public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

			Map<String, String> map = new HashMap<String, String>();

			@SuppressWarnings("rawtypes")
			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				map.put(key, value);
			}

			return map;
		}

		// Simple helper method to fetch request data as a string from HttpServletRequest object.
		public static String getBody(HttpServletRequest request) throws IOException {

		    String body;
		    StringBuilder stringBuilder = new StringBuilder();
		    BufferedReader bufferedReader = null;

		    try {
		        InputStream inputStream = request.getInputStream();
		        if (inputStream != null) {
		            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		            char[] charBuffer = new char[128];
		            int bytesRead = -1;
		            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		                stringBuilder.append(charBuffer, 0, bytesRead);
		            }
		        } else {
		            stringBuilder.append("");
		        }
		    } catch (IOException ex) {
		        throw ex;
		    } finally {
		        if (bufferedReader != null) {
		            try {
		                bufferedReader.close();
		            } catch (IOException ex) {
		                throw ex;
		            }
		        }
		    }

		    body = stringBuilder.toString();
		    return body;
		}
	
	public static void main(String[] args) {

//	String inputString = String.format("%s|%s|%s|%s", transmissionId, timeStamp, webhookId, crc32(eventBody));
//	// Get the signatureAlgorithm from the PAYPAL-AUTH-ALGO HTTP header
//	Signature signatureAlgorithm = Signature.getInstance("signatureAlgorithm");
//	// Get the certData from the URL provided in the HTTP headers and cache it
//	Certificate certificate = X509Certificate.getInstance(certData);
//	signatureAlgorithm.initVerify(certificate.getPublicKey());
//	signatureAlgorithm.update(inputString.getBytes());
//	// Actual signature is base 64 encoded and available in the HTTP headers
//	byte[] actualSignature = Base64.decodeBase64(actualSignatureEncoded.getBytes());
//	boolean isValid = signatureAlgorithm.verify(actualSignature);

	}
}
