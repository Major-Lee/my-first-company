package com.bhu.vas.web.sample;

import static com.bhu.vas.web.payments.util.SampleConstants.clientID;
import static com.bhu.vas.web.payments.util.SampleConstants.clientSecret;
import static com.bhu.vas.web.payments.util.SampleConstants.mode;

import java.util.ArrayList;
import java.util.List;

import com.paypal.api.payments.EventType;
import com.paypal.api.payments.Webhook;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class Test {

	public static void main(String[] args) {
		List eventTypes = new ArrayList(); 
		   
		EventType eventType1 = new EventType(); 
		eventType1.setName("PAYMENT.AUTHORIZATION.CREATED"); 

		EventType eventType2 = new EventType(); 
		eventType2.setName("PAYMENT.AUTHORIZATION.VOIDED"); 

		eventTypes.add(eventType1); 
		eventTypes.add(eventType2);

		Webhook webhook = new Webhook(); 
		webhook.setUrl("https://upay.bhuwifi.com/msip_bhu_payment_rest/payment/paypalNotifySuccess"); 
		webhook.setEventTypes(eventTypes);
		
		try{
		// ### Api Context
			APIContext apiContext = new APIContext(clientID, clientSecret, mode);
//		   webhook.delete(apiContext, "3F102262XP205412U");
			Webhook createdWebhook = webhook.create(apiContext, webhook);
			System.out.println("Webhook successfully created with ID " + createdWebhook.getId());
		} catch (PayPalRESTException e) {
		  System.err.println(e.getDetails());
		}
		
		// Obtain User's Consent
		// Obtain the redirect URL as shown below:
//		APIContext context = new APIContext(clientID, clientSecret, "sandbox");
//
//		List<String> scopes = new ArrayList<String>() {{
//		    /**
//		    * 'openid'
//		    * 'profile'
//		    * 'address'
//		    * 'email'
//		    * 'phone'
//		    * 'https://uri.paypal.com/services/paypalattributes'
//		    * 'https://uri.paypal.com/services/expresscheckout'
//		    * 'https://uri.paypal.com/services/invoicing'
//		    */
//		    add("openid");
//		    add("profile");
//		    add("email");
//		}};
//		String redirectUrl = Session.getRedirectURL("UserConsent", scopes, context);
//		System.out.println(redirectUrl);
		//https://www.sandbox.paypal.com/signin/authorize?client_id=&response_type=code&scope=openid+profile+email+&redirect_uri=UserConsent

		//获取用户code然后调用下边接口刷新用户的Token

//		// Replace the code with the code value returned from the redirect on previous step.
//		Tokeninfo info = Tokeninfo.createFromAuthorizationCode(context, code);
//		String accessToken = info.getAccessToken();
//		String refreshToken = info.getRefreshToken();
//		
//		/**
//		 *  Obtain User Info
//		 *  The refresh token can be used to retrieve user information as shown below:
//		 */
//		APIContext userAPIContext = new APIContext(clientID, clientSecret, "sandbox").setRefreshToken(info.getRefreshToken());
//
//		Userinfo userinfo = Userinfo.getUserinfo(userAPIContext);
//		System.out.println(userinfo);
	}
}
