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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.helper.BusinessHelper;
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
/**
 * Paypal消息处理流程逻辑类
 * @author pamchen-1
 *
 */
@Service
public class PaypalService {
	
	@Resource
	PayLogicService payLogicService;
	
	private static final Logger logger = Logger.getLogger(PaymentWithPayPalServlet.class);
	public static final String WebhookId = "2ND47987AC600853R";
	
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
