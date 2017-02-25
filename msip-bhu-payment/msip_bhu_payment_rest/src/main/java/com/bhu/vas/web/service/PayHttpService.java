package com.bhu.vas.web.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.helper.JsonUtil;
import com.bhu.vas.business.helper.MD5Util;
import com.bhu.vas.web.http.HttpResponseUtil;
import com.bhu.vas.web.http.response.AppUnifiedOrderResponse;
import com.bhu.vas.web.http.response.GenerateQCCodeUrlResponse;
import com.bhu.vas.web.http.response.GetAccessTokenResponse;
import com.bhu.vas.web.http.response.GetJsapiTicketResponse;
import com.bhu.vas.web.http.response.GetOpenIdResponse;
import com.bhu.vas.web.http.response.JSAPIUnifiedOrderResponse;
import com.bhu.vas.web.http.response.UnifiedOrderResponse;
import com.bhu.vas.web.http.response.WithDrawNotifyResponse;

/**
 * 功能：调用第三方支付web服务
 * Pengyu on 2016/5/03.
 */
@Service
public class PayHttpService {
    static String payRequestBaseUrl="https://api.mch.weixin.qq.com";
    static String payRequestApiBaseUrl="https://api.weixin.qq.com/cgi-bin";
    static String  withdrawalsRequestApiBaseUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    
    
    //重定向地址
    public static String PAY_HOST_URL = "http://pays.bhuwifi.com/msip_bhu_payment_rest/payment";
    //重定向地址
    public static String REDIRECT_URL = PAY_HOST_URL+"/weixinPay";
    //异步回调地址
    public static String WEIXIN_NOTIFY_URL = PAY_HOST_URL+"/wxPayNotifySuccess";
    
    public static String HEE_NOTIFY_URL = PAY_HOST_URL+"/heepayNotifySuccess";
    
    public static String ALIPAY_NOTIFY_URL = PAY_HOST_URL+"/alipayNotifySuccess";
    
    public static String NOWIPAY_NOTIFY_URL = PAY_HOST_URL+"/nowpayNotifySuccess";
    
	//打赏页面跳转同步通知页面路径
    public static String ALIPAY_RETURN_URL = PAY_HOST_URL+"/alipayReturn";
  //打赏页面跳转同步通知页面路径
    public static String NOWPAY_RETURN_URL = PAY_HOST_URL+"/nowpayReturn";
	//充值页面跳转同步通知页面路径
    public static String ALIPAY_PREPAID_RETURN_URL = PAY_HOST_URL+"/alipayPrepaidReturn";
  //充值页面跳转同步通知页面路径
    public static String NOWPAY_PREPAID_RETURN_URL = PAY_HOST_URL+"/nowpayPrepaidReturn";
	//打赏页面跳转同步通知页面路径
    public static String HEE_RETURN_URL = PAY_HOST_URL+"/heeReturn";
	//充值页面跳转同步通知页面路径
    public static String HEE_PREPAID_RETURN_URL = PAY_HOST_URL+"/heePrepaidReturn";
    
    //打赏页面跳转同步通知页面路径
    public static String MIDAS_RETURN_URL = "/msip_bhu_payment_rest/payment/midasReturn";
	
    public static String PAYPAL_RETURN_URL = PAY_HOST_URL+"/paypalReturn";
    
  //打赏成功web回调地址
    public static String WEB_RETURN_URL = "http://ucloudtest.bhuwifi.com:9158/portal/success.html?payokurl=http://bbs.bhuwifi.com/jump.php";
    
    //打赏失败web回调地址
//    public static String WEB_NOTIFY_URL = "http://ucloud.bhuwifi.com:9158/portal/unsuccess.html";
    public static String WEB_NOTIFY_URL = "http://101.200.183.44:9158/portal/unsuccess.html";
//    public static String WEB_NOTIFY_URL = "http://192.168.66.197:9158/portal/unsuccess.html";
    //充值失败web回调地址
//     public static String PREPAID_NOTIFY_URL = "http://ucloud.bhuwifi.com:9158/portal/unsuccess.html";
    public static String PREPAID_NOTIFY_URL = "http://101.200.183.44:9158/portal/unsuccess.html";
//    public static String PREPAID_NOTIFY_URL = "http://192.168.66.197:9158/portal/unsuccess.html";
  
   //短信预警通知联系人list
   public static String Internal_level1_error_man = "18515465766,13911210204,18601267579,18910735595,13911716572,15910526881,15901090128";
   public static String Internal_level2_warning_man = "18515465766,13911210204";
   public static String MIDAS_LEVEL1_RATE = "80-100";
   public static String NOW_LEVEL1_RATE = "20-100";
   public static String ORDER_AGENT = "Now-Hee";
   public static int ORDER_ALLOCATION_LEVEL = 2;
   
   
   
   //证书地址
    public static String WITHDRAW_URL = "/home";
    //本地生成微信二维码支付，所需的公司logo地址
    public static String PRUE_LOGO_URL = "E:"+File.separator+"logo_big.png";
    public static String QR_CODE_URL = "E:"+File.separator+"picture.png";
//    public static String PRUE_LOGO_URL = "/home/payment/qrcode"+File.separator+"logo_big.png";
//    public static String QR_CODE_URL = "/home/payment/qrcode"+File.separator+"picture.png";

    private  Logger log = LoggerFactory.getLogger(PayHttpService.class);

    @Value("#{p['pay.appid']}")
    String appId;
    @Value("#{p['pay.appsecret']}")
    String appSecret;
    @Value("#{p['pay.mchid']}")
    String mchId;
    @Value("#{p['pay.mchkey']}")
    String mchKey;
    @Value("#{p['pay.testAppId']}")
    String testAppId;
    @Value("#{p['pay.testAppSecret']}")
    String testAppSecret;
    @Value("#{p['pay.testMchId']}")
    String testMchId;
    @Value("#{p['pay.testMchKey']}")
    String testMchKey;
    
    //BHU WiFi reward app Weixin info
    @Value("#{p['pay.appAppId']}")
    String appAppId;
    @Value("#{p['pay.appAppSecret']}")
    String appAppSecret;
    @Value("#{p['pay.appMchId']}")
    String appMchId;
    @Value("#{p['pay.appMchKey']}")
    String appMchKey;
    
  //BHU WiFi tools app Weixin info
    @Value("#{p['pay.appDSAppId']}")
    String appDSAppId;
    @Value("#{p['pay.appDSAppSecret']}")
    String appDSAppSecret;
    @Value("#{p['pay.appDSMchId']}")
    String appDSMchId;
    @Value("#{p['pay.appDSMchKey']}")
    String appDSMchKey;
    
    @Value("#{p['pay.env']}")
    String env;


    @Value("#{p['pay.outTime']}")
    int outTime;
    
    String accessToken;
    String ticket;
    
    public void initPayHttpService(){
    	String env = this.env;
    	 if(env.equalsIgnoreCase("inner")){ //判断是内网环境
    		this.appId = this.testAppId;
    		this.appSecret = this.testAppSecret;
    		this.mchId = this.testMchId;
    		this.mchKey = this.testMchKey;
    	}else if(env.equalsIgnoreCase("test")){//判断是测试环境
    		this.appId = this.testAppId;
    		this.appSecret = this.testAppSecret;
    		this.mchId = this.testMchId;
    		this.mchKey = this.testMchKey;
    	}
    }
    
    public UnifiedOrderResponse unifiedorder(String orderId,String commodityName, double totalPrice,String localIp,String payCallUrl,String openId ) {
    	initPayHttpService();
    	if("0:0:0:0:0:0:0:1".equals(localIp)){
            localIp="10.96.5.235";
        }
        /** 总金额(分为单位) */
        int total = (int) (totalPrice * 100);

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("appid", appId);
        /** 商户号 */
        parameters.put("mch_id", mchId);
        /** 随机字符串 */
        parameters.put("nonce_str", getNonceStr());
        /** 商品名称 */
        parameters.put("body", commodityName);

        /** 当前时间 yyyyMMddHHmmss */
        String currTime = getCurrTime();
        /** 8位日期 */
        String strTime = currTime.substring(8, currTime.length());
        /** 四位随机数 */
        String strRandom = buildRandom(4) + "";
        /** 订单号 */
        parameters.put("out_trade_no", strTime + strRandom);

        /** 订单金额以分为单位，只能为整数 */
        parameters.put("total_fee", total);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", localIp);
        /** 支付回调地址 */
        parameters.put("notify_url", payCallUrl);
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "NATIVE");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign(mchKey, "UTF-8", parameters);
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);
        log.info("requestXML：" + requestXML);

        UnifiedOrderResponse unifiedOrderResponse;
        try {
            unifiedOrderResponse = HttpResponseUtil.post(payRequestBaseUrl + "/pay/unifiedorder", requestXML, UnifiedOrderResponse.class);
        } catch (IOException e) {
            unifiedOrderResponse=new UnifiedOrderResponse();
            unifiedOrderResponse.setResultSuccess(false);
            e.printStackTrace();
        }
        return unifiedOrderResponse;
    }
    
    public UnifiedOrderResponse unifiedorder(String trade_type,String orderId,String commodityName, int totalPrice,String localIp,String payCallUrl,String openId ) {
    	initPayHttpService();
    	if("0:0:0:0:0:0:0:1".equals(localIp)){
            localIp="182.92.83.59";
        }
        /** 总金额(分为单位) */

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("appid", appId);
        /** 商户号 */
        parameters.put("mch_id", mchId);
        /** 随机字符串 */
        String nonceStr = getNonceStr();
        parameters.put("nonce_str", nonceStr);
        /** 商品名称 */
        parameters.put("body", commodityName);

        if(orderId.equals("")) {
            /** 当前时间 yyyyMMddHHmmss */
            String currTime = getCurrTime();
            /** 8位日期 */
            String strTime = currTime.substring(8, currTime.length());
            /** 四位随机数 */
            String strRandom = buildRandom(4) + "";
            orderId=strTime + strRandom;

        }
        /** 订单号 */
        parameters.put("out_trade_no", orderId);

        /** 订单金额以分为单位，只能为整数 */
        parameters.put("total_fee", totalPrice);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", localIp);
        /** 支付回调地址 */
        parameters.put("notify_url", payCallUrl);
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type",trade_type);
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign(mchKey, "UTF-8", parameters);
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);
        log.info("requestXML：" + requestXML);

        UnifiedOrderResponse unifiedOrderResponse;
        try {
            unifiedOrderResponse = HttpResponseUtil.post(payRequestBaseUrl + "/pay/unifiedorder", requestXML, UnifiedOrderResponse.class);
            unifiedOrderResponse.setNonce_str(nonceStr);
            unifiedOrderResponse.setSign(sign);
        } catch (IOException e) {
            unifiedOrderResponse=new UnifiedOrderResponse();
            unifiedOrderResponse.setResultSuccess(false);
            e.printStackTrace();
        }
        return unifiedOrderResponse;
    }
    
    public JSAPIUnifiedOrderResponse unifiedorderJSAPI(String orderId,String commodityName, String totalPrice,String localIp,String payCallUrl,String openId ) {
    	initPayHttpService();
    	if("0:0:0:0:0:0:0:1".equals(localIp)){
            localIp="10.96.5.235";
        }
        /** 总金额(分为单位) */

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("appid", appId);
        /** 商户号 */
        parameters.put("mch_id", mchId);
        /** 随机字符串 */
        parameters.put("nonce_str", getNonceStr());
        /** 商品名称 */
        parameters.put("body", commodityName);

        /** 订单号 */
        parameters.put("out_trade_no", orderId);

        /** 订单金额以分为单位，只能为整数 */
        parameters.put("total_fee", totalPrice);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", localIp);
        /** 支付回调地址 */
        parameters.put("notify_url", payCallUrl);
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "JSAPI");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign(mchKey, "UTF-8", parameters);
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);
        log.info("requestXML：" + requestXML);

        JSAPIUnifiedOrderResponse unifiedOrderResponse=null;
        try {
        	unifiedOrderResponse = HttpResponseUtil.post(payRequestBaseUrl + "/pay/unifiedorder", requestXML, JSAPIUnifiedOrderResponse.class);
        } catch (IOException e) {
            unifiedOrderResponse= new JSAPIUnifiedOrderResponse();
            unifiedOrderResponse.setResultSuccess(false);
            e.printStackTrace();
        }
        return unifiedOrderResponse;
    }


    //
    /**
     *https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx1d5aa1344e0c3384&secret=4593800c8a2b3491ee5cd25d56d805db
     * @return
     */
    public GetAccessTokenResponse getAccessToken() throws IOException {
        return getAccessToken(false);
    }
    public GetAccessTokenResponse getAccessToken(boolean forced) throws IOException {
    	initPayHttpService();
    	GetAccessTokenResponse response;
        if(accessToken!=null && !"".equals(forced)){
            if(!forced){
                response=new GetAccessTokenResponse();
                response.setAccess_token(accessToken);
                return response;
            }
        }
        String url =payRequestApiBaseUrl+ "/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
        response =HttpResponseUtil.get(url, GetAccessTokenResponse.class);
        this.accessToken=response.getAccess_token();
        return response;
    }

    public GetOpenIdResponse getOpenId(String code) throws IOException {
    	initPayHttpService();
        GetOpenIdResponse response;

        // 根据用户code 拿到用户openId,下面就到了获取openid,这个代表用户id.
        String openParam = "appid=" + appId + "&secret="
                + appSecret + "&code=" + code
                + "&grant_type=authorization_code";


        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"+ openParam;
        response =HttpResponseUtil.get(url, GetOpenIdResponse.class);
        return response;
    }

    public GetJsapiTicketResponse GetJsapiTicket() throws IOException {
    	getAccessToken();
        //https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=7uGxXoZbaujOAboHGNR5BIIbI2JOOjURexWlhzp3oHU0QEYeNAauuFCsuJBKqBPa3K6qTUMZxVNQ9ajTsNe_yLJ0Su7VN-E3gWWWDZRlsMw&type=jsapi
        String url =payRequestApiBaseUrl+ "/ticket/getticket?access_token="+accessToken+"&&type=jsapi";
        GetJsapiTicketResponse response = HttpResponseUtil.get(url, GetJsapiTicketResponse.class);
        this.ticket=response.getTicket();
        return response;
    }

    public GenerateQCCodeUrlResponse generateQCCodeUrl(String content) throws IOException {
    	getAccessToken();
        //https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
        String url =payRequestApiBaseUrl+ "/qrcode/create?access_token="+accessToken;

        Map<String,Object> params=new LinkedHashMap<String,Object>();
        params.put("action_name","QR_SCENE");
        params.put("expire_seconds",String.valueOf(60*10));
        Map<String,Object> info=new LinkedHashMap<String,Object>();
        Map<String,Object> scene=new LinkedHashMap<String,Object>();
        content=URLEncoder.encode(content,"UTF-8");
        scene.put("scene_str",content);
        info.put("scene",scene);
        params.put("action_info", info);

        String postContent = JsonUtil.toJson(params);

//        postContent= URLEncoder.encode(postContent,"UTF-8");
        GenerateQCCodeUrlResponse response = HttpResponseUtil.post(url, postContent, GenerateQCCodeUrlResponse.class);
        return response;
    }
    /**
     * sign签名
     *
     * 作者: zhoubang 日期：2015年6月10日 上午9:31:24
     *
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public  String createSign(String mchKey, String characterEncoding, SortedMap<Object, Object> parameters) {
    	StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            /** 如果参数为key或者sign，则不参与加密签名 */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        /** 支付密钥必须参与加密，放在字符串最后面 */
        sb.append("key=" + mchKey);
        /** 记得最后一定要转换为大写 */
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    //jsapi_ticket=bxLdikRXVbTPdHSM05e5u5r0sC4Mhn84zRMFAY044IAUOhHoBnzk6MguVBdFZTOVeJTJwcwm8JyC5hEN5ARxaA&noncestr=a9df2255ad642b923d95503b9a7958d8&timetamp=1440695548&url=http://test.gjpay.jdguanjia.com/order/jpay/weixinPay?code=03168f45283f13cd132c17896fc8d2ap&state=12332
    //jsapi_ticket=bxLdikRXVbTPdHSM05e5u5r0sC4Mhn84zRMFAY044IAUOhHoBnzk6MguVBdFZTOVeJTJwcwm8JyC5hEN5ARxaA&noncestr=a9df2255ad642b923d95503b9a7958d8&timestamp=1440695548&url=http://test.gjpay.jdguanjia.com/order/jpay/weixinPay?code=03168f45283f13cd132c17896fc8d2ap&state=12332

    /**
     * 取得签名signature
     * @param timestamp
     * @param nonceStr
     * @param url
     * @return
     */
    public  String getSignature(String ticket, long timestamp, String nonceStr, String url){
    	initPayHttpService();
    	String signature=null;
        String str = "jsapi_ticket="+ticket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
        // 对string1进行sha1签名，得到signature
        try {
            MessageDigest reset = MessageDigest.getInstance("SHA-1");
            reset.update(str.getBytes("utf-8"));
            byte[] hash=reset.digest();
            Formatter formatter = new Formatter();
            for(byte b:hash){
                formatter.format("%02x", b);
            }
            signature = formatter.toString();
            formatter.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }
    /**
     * 取得签名signature
     * @param timestamp
     * @param nonceStr
     * @param url
     * @return
     */
    public  String getPrePaySignature(String appId, long timestamp, String nonceStr, String packages,String signType){
    	initPayHttpService();
    	String signature=null;
        String str = "appId="+appId+"&noncestr="+nonceStr+"&package="+packages+"&signType="+signType+"&timeStamp="+timestamp;
        // 对string1进行sha1签名，得到signature
        try {
            MessageDigest reset = MessageDigest.getInstance("SHA-1");
            reset.update(str.getBytes("utf-8"));
            byte[] hash=reset.digest();
            Formatter formatter = new Formatter();
            for(byte b:hash){
                formatter.format("%02x", b);
            }
            signature = formatter.toString();
            formatter.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }
    /**
     * 将请求参数转换为xml格式的string
     *
     * 作者: zhoubang 日期：2015年6月10日 上午9:25:51
     *
     * @param parameters
     * @return
     */
    public  String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            String v = entry.getValue() + "";
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }


    //<editor-fold desc="function">
    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public  String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
    /**
     * 获取32位随机字符串
     *
     * 作者: zhoubang
     * 日期：2015年6月26日 下午3:51:44
     * @return
     */
    public  String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }

    /**
     * 时间戳
     *
     * 作者: zhoubang
     * 日期：2015年6月26日 下午3:52:08
     * @return
     */
    public  String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length
     *            int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public  int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }
    //</editor-fold>


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public  String getPayRequestBaseUrl() {
        return payRequestBaseUrl;
    }

    public  void setPayRequestBaseUrl(String payRequestBaseUrl) {
        PayHttpService.payRequestBaseUrl = payRequestBaseUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }
    
	 public String getTestAppId() {
		return testAppId;
	}

	public void setTestAppId(String testAppId) {
		this.testAppId = testAppId;
	}

	public String getTestAppSecret() {
		return testAppSecret;
	}

	public void setTestAppSecret(String testAppSecret) {
		this.testAppSecret = testAppSecret;
	}

	public String getTestMchId() {
		return testMchId;
	}

	public void setTestMchId(String testMchId) {
		this.testMchId = testMchId;
	}

	public String getTestMchKey() {
		return testMchKey;
	}

	public void setTestMchKey(String testMchKey) {
		this.testMchKey = testMchKey;
	}

	public String getAppAppId() {
		return appAppId;
	}

	public void setAppAppId(String appAppId) {
		this.appAppId = appAppId;
	}

	public String getAppAppSecret() {
		return appAppSecret;
	}

	public void setAppAppSecret(String appAppSecret) {
		this.appAppSecret = appAppSecret;
	}

	public String getAppMchId() {
		return appMchId;
	}

	public void setAppMchId(String appMchId) {
		this.appMchId = appMchId;
	}

	public String getAppMchKey() {
		return appMchKey;
	}

	public void setAppMchKey(String appMchKey) {
		this.appMchKey = appMchKey;
	}

	public String getAppDSAppId() {
		return appDSAppId;
	}

	public void setAppDSAppId(String appDSAppId) {
		this.appDSAppId = appDSAppId;
	}

	public String getAppDSAppSecret() {
		return appDSAppSecret;
	}

	public void setAppDSAppSecret(String appDSAppSecret) {
		this.appDSAppSecret = appDSAppSecret;
	}

	public String getAppDSMchId() {
		return appDSMchId;
	}

	public void setAppDSMchId(String appDSMchId) {
		this.appDSMchId = appDSMchId;
	}

	public String getAppDSMchKey() {
		return appDSMchKey;
	}

	public void setAppDSMchKey(String appDSMchKey) {
		this.appDSMchKey = appDSMchKey;
	}
	
	public int getOt() {
		return outTime;
	}

	public void setOt(int ot) {
		this.outTime = ot;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}
	
	

	public UnifiedOrderResponse unifiedorder(String out_trade_no,String commodityName, String totalPrice,String localIp,String payCallUrl,String openId ) {
		initPayHttpService();
		if("0:0:0:0:0:0:0:1".equals(localIp)){
            localIp="10.96.5.235";
        }
        /** 总金额(分为单位) */
        //totalPrice;

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("appid", appId);
        /** 商户号 */
        parameters.put("mch_id", mchId);
        /** 随机字符串 */
        parameters.put("nonce_str", getNonceStr());
        /** 商品名称 */
        parameters.put("body", commodityName);
//        GoodDetailEntity goods = new GoodDetailEntity();
//        goods.setGoods_id(out_trade_no);
//        goods.setQuantity(1);
//        goods.setGoods_name(commodityName);
//        goods.setBody(commodityName+"-"+out_trade_no);
//        List<GoodDetailEntity> detailArray = new ArrayList<GoodDetailEntity>(); 
//        detailArray.add(goods);
//        String detail = JsonHelper.getJSONString(detailArray);
//        System.out.println(detail);
//        /** 商品详情 */
//        parameters.put("detail", detail);

        /** 订单号 */
        parameters.put("out_trade_no", out_trade_no);

        /** 订单金额以分为单位，只能为整数 */
        parameters.put("total_fee", totalPrice);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", localIp);
        /** 支付回调地址 */
        parameters.put("notify_url", payCallUrl);
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "NATIVE");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign(mchKey, "UTF-8", parameters);
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);
        log.info("requestXML：" + requestXML);

        UnifiedOrderResponse unifiedOrderResponse = null;
        try {
            unifiedOrderResponse = HttpResponseUtil.post(payRequestBaseUrl + "/pay/unifiedorder", requestXML, UnifiedOrderResponse.class);
        }catch (IOException e) {
    	   unifiedOrderResponse=new UnifiedOrderResponse();
           unifiedOrderResponse.setResultSuccess(false);
    	   log.info("native weixin catch error：" + e.getMessage()+e.getCause());
	}
        return unifiedOrderResponse;
    }

	 public AppUnifiedOrderResponse unifiedorderForApp(String appId,String mchId,String mchKey,String out_trade_no,String commodityName, String totalPrice,String localIp,String payCallUrl,String openId ) {
		 initPayHttpService();  
		 if("0:0:0:0:0:0:0:1".equals(localIp)){
	            localIp="10.96.5.235";
	        }
	        /** 总金额(分为单位) */
	        //totalPrice;
	        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
	        /** 公众号APPID */
	        parameters.put("appid", appId);
	        /** 商户号 */
	        parameters.put("attach", "bhu_app");
	        /** 商户号 */
	        parameters.put("mch_id", mchId);
	        /** 随机字符串 */
	        parameters.put("nonce_str", getNonceStr());
	        /** 商品名称 */
	        parameters.put("body", commodityName);

	        /** 订单号 */
	        parameters.put("out_trade_no", out_trade_no);

	        /** 订单金额以分为单位，只能为整数 */
	        parameters.put("total_fee", totalPrice);
	        /** 客户端本地ip */
	        parameters.put("spbill_create_ip", localIp);
	        /** 支付回调地址 */
	        parameters.put("notify_url", payCallUrl);
	        /** 支付方式为JSAPI支付 */
	        parameters.put("trade_type", "APP");
	        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
	        parameters.put("openid", openId);

	        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
	        String sign = createSign(mchKey, "UTF-8", parameters);
	        parameters.put("sign", sign);

	        /** 生成xml结构的数据，用于统一下单接口的请求 */
	        String requestXML = getRequestXml(parameters);
	        log.info("requestXML：" + requestXML);
	        
	        AppUnifiedOrderResponse unifiedOrderResponse=null;
	        try {
	        	unifiedOrderResponse = HttpResponseUtil.post(payRequestBaseUrl + "/pay/unifiedorder", requestXML, AppUnifiedOrderResponse.class);
	        } catch (Exception e) {
	            unifiedOrderResponse= new AppUnifiedOrderResponse();
	            unifiedOrderResponse.setResultSuccess(false);
	            log.info("app weixin catch error：" + e.getMessage()+e.getCause());
	        }
	        return unifiedOrderResponse;
	    }
	 
	public WithDrawNotifyResponse sendWithdraw(String out_trade_no, String commodityName, String totalPrice,
			String localIp, String nOTIFY_URL, String openid, String userName) {
		initPayHttpService();
		if("0:0:0:0:0:0:0:1".equals(localIp)){
			localIp="123.57.52.205";
        }
        /** 总金额(分为单位) */
        //totalPrice;

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("mch_appid", appId);
        /** 商户号 */
        parameters.put("mchid", mchId);
        /** 随机字符串 */
        parameters.put("nonce_str", getNonceStr());
        /** 商品名称 */
        parameters.put("desc", commodityName);
        /** 订单号 */
        parameters.put("partner_trade_no", out_trade_no);
        /** 订单金额以分为单位，只能为整数 */
        parameters.put("amount", totalPrice);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", localIp);
        parameters.put("check_name", "NO_CHECK");
        parameters.put("openid", openid);

        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign(mchKey, "UTF-8", parameters);
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);
        log.info("requestXML：" + requestXML);

        WithDrawNotifyResponse unifiedOrderResponse = new WithDrawNotifyResponse();
        
        try {
        	unifiedOrderResponse = HttpResponseUtil.httpRequest(mchId,withdrawalsRequestApiBaseUrl,nOTIFY_URL, requestXML, WithDrawNotifyResponse.class);
		} catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
				| CertificateException | IOException e) {
			unifiedOrderResponse.setResultSuccess(false);
			unifiedOrderResponse.setResultErrorCode("FAIL");
			unifiedOrderResponse.setResultMessage("微信请求接口捕获异常证书路径有误");
			log.error("提交提现请求失败");
		}
        
        return unifiedOrderResponse;
	}

	public static void main(String[] args) {
		//ClientCustomSSL.gethttpRequests();
	}

}
