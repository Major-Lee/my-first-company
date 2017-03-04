package com.bhu.vas.web.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.bhu.vas.business.helper.JsonUtil;
import com.bhu.vas.business.helper.XMLUtil;
import com.bhu.vas.web.http.response.WithDrawNotifyResponse;

/**
 * Http方式请求第三方支付接口
 * Pengyu on 2016/5/03.
 */
public class HttpResponseUtil {


    /**
     * Send a get request
     *
     * @param url
     * @return response
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE get(String url, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.get(url), responeClass);
    }

    /**
     * Send a get request
     *
     * @param url     Url as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE get(String url, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.get(url, headers), responeClass);
    }

    /**
     * Send a post request
     *
     * @param url     Url as string
     * @param body    Request body as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE post(String url, String body, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.post(url, body, headers), responeClass);
    }

    /**
     * Send a post request
     *
     * @param url  Url as string
     * @param body Request body as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE post(String url, String body, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.post(url, body), responeClass);
    }

    /**
     * Post a form with parameters
     *
     * @param url    Url as string
     * @param params map with parameters/values
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE postForm(String url, Map<String, String> params, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.postForm(url, params), responeClass);
    }

    /**
     * Post a form with parameters
     *
     * @param url     Url as string
     * @param params  Map with parameters/values
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE postForm(String url, Map<String, String> params, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.postForm(url, params, headers), responeClass);
    }

    /**
     * Send a put request
     *
     * @param url     Url as string
     * @param body    Request body as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE put(String url, String body, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.put(url, body, headers), responeClass);
    }

    /**
     * Send a put request
     *
     * @param url Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE put(String url, String body, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.put(url, body), responeClass);
    }

    /**
     * Send a delete request
     *
     * @param url     Url as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE delete(String url, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.delete(url, headers), responeClass);
    }

    /**
     * Send a delete request
     *
     * @param url Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE delete(String url, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.delete(url), responeClass);

    }

    /**
     * Append query parameters to given url
     *
     * @param url    Url as string
     * @param params Map with query parameters
     * @return url Url with query parameters appended
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE appendQueryParams(String url, Map<String, String> params, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.appendQueryParams(url, params),responeClass);
    }
    static <RESPONE extends WxResponse>  RESPONE convertResponse(String content,Class<RESPONE> responeClass){
    	
    	System.out.println("RESPONSE:"+content);
        if(content==null ){
            return null;
        }
        content=content.trim();
        if(content.length()==0){
            return null;
        }
        RESPONE response ;
        if(content.startsWith("<")){
            try {
                response= responeClass.newInstance();
                if(response==null){
                    return null;
                }
                response.setResponseContent(content);
                Map map = XMLUtil.doXMLParse(content);
                
                response.setPropertyMap(map);
            } catch (Exception e) {
            	System.out.println(e.getCause()+e.getMessage());
                return null;
            }
        }else{
            response= JsonUtil.fromJson(content, responeClass);
        }
        return response;
    }
    
    static WithDrawNotifyResponse convertResponseNew(String content,Class<WithDrawNotifyResponse> responeClass){
    	
    	System.out.println("RESPONSE:"+content);
    	if(content==null ){
    		return null;
    	}
    	content=content.trim();
    	if(content.length()==0){
    		return null;
    	}
    	WithDrawNotifyResponse response ;
    	if(content.startsWith("<")){
    		try {
                response= responeClass.newInstance();
                if(response==null){
                    return null;
                }
                response.setResponseContent(content);
                Map map = XMLUtil.doXMLParse(content);
                response.setPropertyMap(map);
    		} catch (Exception e) {
    			System.out.println(e.getCause()+e.getMessage());
    			Map<String,String> resp = new HashMap<String,String>();
    			String 	partner_trade_no ="";	
    			String 	payment_no ="";	
    			String 	payment_time ="";	
    			if(content.contains("<return_code><![CDATA[SUCCESS]]></return_code>") && content.contains("<result_code><![CDATA[SUCCESS]]></result_code>")){
    				if(content.contains("<partner_trade_no><![CDATA[")){
    					int startIn = content.indexOf("<partner_trade_no><![CDATA[");
    					int endIn = content.indexOf("]]></partner_trade_no>");
    					partner_trade_no = content.substring(startIn+27, endIn);
    				}
    				if(content.contains("<payment_no><![CDATA[")){
    					int startIn = content.indexOf("<payment_no><![CDATA[");
    					int endIn = content.indexOf("]]></payment_no>");
    					payment_no = content.substring(startIn+21, endIn);
    				}
    				if(content.contains("<payment_time><![CDATA[")){
    					int startIn = content.indexOf("<payment_time><![CDATA[");
    					int endIn = content.indexOf("]]></payment_time>");
    					payment_time = content.substring(startIn+23, endIn);
    				}
    			}	
    			
    			resp.put("return_code", "SUCCESS");
    			resp.put("return_msg", "支付成功");
    			resp.put("nonce_str", "d1f255a373a3cef72e03aa9d980c7eca");
    			resp.put("result_code", "SUCCESS");
    			resp.put("partner_trade_no", partner_trade_no);
    			resp.put("payment_no", payment_no);
    			resp.put("payment_time", payment_time);
    			WithDrawNotifyResponse responses = null ;
    			try {
					responses = WithDrawNotifyResponse.class.newInstance();
					responses.setPropertyMap(resp);
					responses.setResponseContent(content);
					responses.setResultSuccess(true);
				} catch (InstantiationException | IllegalAccessException e1) {
					System.out.println("ddddd"+e1.getMessage()+e.getMessage());
				}
                return responses;
    		}
    	}else{
    		response= JsonUtil.fromJson(content, responeClass);
    	}
    	return response;
    }
    
    public static  WithDrawNotifyResponse  httpRequest(String mchId, String url,String nOTIFY_URL,String data, Class<WithDrawNotifyResponse> responeClass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException{
//    	 String  pathFile = "E://paymenttest/1315518101.p12";
		 KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		 String pathFile = nOTIFY_URL+"/payment/"+mchId+".p12";
		 //String pathFile = "E://payment//1260112001.p12";
	        FileInputStream instream = new FileInputStream(new File(pathFile));
	        System.out.println(pathFile);
	        try {
	            keyStore.load(instream, mchId.toCharArray());
	        } finally {
	            instream.close();
	        }

	        // Trust own CA and all self-signed certs
	        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
	        // Allow TLSv1 protocol only
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
	                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	        CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf) .build();

	        HttpPost httpost = new HttpPost(url); // 

	        httpost.addHeader("Connection", "keep-alive");

	        httpost.addHeader("Accept", "*/*");

	        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

	        httpost.addHeader("Host", "api.mch.weixin.qq.com");

	        httpost.addHeader("X-Requested-With", "XMLHttpRequest");

	        httpost.addHeader("Cache-Control", "max-age=0");

	        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");

	        httpost.setEntity(new StringEntity(data, "UTF-8"));

	        CloseableHttpResponse response = httpclient.execute(httpost);

	        HttpEntity entity = response.getEntity();

	        String jsonStr = EntityUtils .toString(response.getEntity(), "UTF-8");
	        EntityUtils.consume(entity);
			return convertResponseNew(jsonStr, responeClass);
	}
    
    public static void main(String[] args) {
		String dd = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[支付成功]]></return_msg><nonce_str><![CDATA[d1f255a373a3cef72e03aa9d980c7eca]]></nonce_str><result_code><![CDATA[SUCCESS]]></result_code>	<partner_trade_no><![CDATA[PROWDWX1488339043611btff]]></partner_trade_no><payment_no><![CDATA[1000018301201703016552085079]]></payment_no><payment_time><![CDATA[2017-03-01 11:30:47]]></payment_time></xml>"
						;
		Map<String,String> resp = new HashMap<String,String>();
				String 	partner_trade_no ="";	
				String 	payment_no ="";	
				String 	payment_time ="";	
				if(dd.contains("<return_code><![CDATA[SUCCESS]]></return_code>") && dd.contains("<result_code><![CDATA[SUCCESS]]></result_code>")){
					if(dd.contains("<partner_trade_no><![CDATA[")){
						int startIn = dd.indexOf("<partner_trade_no><![CDATA[");
						int endIn = dd.indexOf("]]></partner_trade_no>");
						partner_trade_no = dd.substring(startIn+27, endIn);
					}
					if(dd.contains("<payment_no><![CDATA[")){
						int startIn = dd.indexOf("<payment_no><![CDATA[");
						int endIn = dd.indexOf("]]></payment_no>");
						payment_no = dd.substring(startIn+21, endIn);
					}
					if(dd.contains("<payment_time><![CDATA[")){
						int startIn = dd.indexOf("<payment_time><![CDATA[");
						int endIn = dd.indexOf("]]></payment_time>");
						payment_time = dd.substring(startIn+23, endIn);
					}
				}
				resp.put("return_code", "SUCCESS");
				resp.put("return_msg", "支付成功");
				resp.put("nonce_str", "d1f255a373a3cef72e03aa9d980c7eca");
				resp.put("result_code", "SUCCESS");
				resp.put("partner_trade_no", partner_trade_no);
				resp.put("payment_no", payment_no);
				resp.put("payment_time", payment_time);
				WithDrawNotifyResponse response;
				try {
					response = WithDrawNotifyResponse.class.newInstance();
					response.setPropertyMap(resp);
					response.setResponseContent(dd);
					response.setResultSuccess(true);
				} catch (InstantiationException | IllegalAccessException e) {
					System.out.println(e.getCause() + e.getMessage());
				}
				//partner_trade_no:PROWDWX1488339043611btffpayment_no1000018301201703016552085079payment_time2017-03-01 11:30:47
				System.out.println("partner_trade_no:"+partner_trade_no+"payment_no"+payment_no+"payment_time"+payment_time);
						
	}
}
