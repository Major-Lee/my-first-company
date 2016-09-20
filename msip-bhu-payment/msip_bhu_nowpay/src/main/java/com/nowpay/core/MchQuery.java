package com.nowpay.core;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.nowpay.config.NowpayConfig;
import com.nowpay.sign.MD5Facade;
import com.nowpay.util.FormDateReportConvertor;
import com.nowpay.util.HttpClientUtil;

/**
 * 现在支付-商户支付订单查询Demo
 * User: NowPay
 * Date: 14-8-19
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class MchQuery {

    private String orderQuery() throws Exception {
        HashMap<String, String> req=new HashMap<String, String>();
        req.put("funcode","MQ001");

        String appId = NowpayConfig.appId;
        String securityKey = NowpayConfig.appKey;
        	
        req.put("appId", appId);
        req.put("mhtOrderNo", "TESTNOWX1467290141034ohbc");
        req.put("mhtCharset", NowpayConfig.mhtCharset);
        req.put("mhtSignType", NowpayConfig.mhtSignType);
        req.put("mhtSignature", MD5Facade.getFormDataParamMD5(req, securityKey, "UTF-8"));

        Map<String,String> queryResultMap = doQuery(req);

        queryResultMap.remove("signType");
        String signature = queryResultMap.remove("signature");

        boolean isValidSignature = MD5Facade.validateFormDataParamMD5(queryResultMap,securityKey,signature);

        System.out.println("验签结果："+isValidSignature);

        if(isValidSignature)
            System.out.println(queryResultMap.size());
        else
            System.out.println("验签失败");

        return null;
    }

    private Map<String,String> doQuery(HashMap<String, String> req) throws Exception {
        String message=buildReq(req);
        System.out.println("插件->接口:"+message);

        String NOWPAY_SERVER_URL = NowpayConfig.Server_url;
        String response= HttpClientUtil.sendHttp(NOWPAY_SERVER_URL, message, "UTF-8");
        System.out.println("现在支付服务器->插件:"+ URLDecoder.decode(response, "UTF-8"));

        return FormDateReportConvertor.parseFormDataPatternReportWithDecode(response,"UTF-8","UTF-8");
    }

    private String buildReq(HashMap<String, String> req){
        return FormDateReportConvertor.postFormLinkReportWithURLEncode(req, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        new MchQuery().orderQuery();
    }
}