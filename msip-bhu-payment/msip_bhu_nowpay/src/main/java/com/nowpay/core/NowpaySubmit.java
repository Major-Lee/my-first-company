package com.nowpay.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nowpay.config.NowpayConfig;
import com.nowpay.sign.MD5Facade;
import com.nowpay.util.PostRequestUtil;


/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class NowpaySubmit {
    
    /**
     * 支付宝提供给商户的服务接入网关URL(新)https://pay.ipaynow.cn(new) http://api.ipaynow.cn/(old)
     */
    private static final String NOWPAY_GATEWAY_NEW = "https://pay.ipaynow.cn";
	
    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMysign(Map<String, String> sPara) {
    	String prestr = NowpayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if(NowpayConfig.mhtSignType.equals("MD5") ) {
        	mysign = MD5Facade.getFormDataParamMD5(sPara, NowpayConfig.appKey, "UTF-8");
        }
        return mysign;
    }
	
    /**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = NowpayCore.paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", NowpayConfig.mhtSignType);

        return sPara;
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
    	String result = "";
    	//待请求参数数组
       // Map<String, String> sPara = buildRequestPara(sParaTemp);
        List<String> keys = new ArrayList<String>(sParaTemp.keySet());

        result = "";
        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sParaTemp.get(name);
            result += "&"+name+"="+value;
        }
       String s =  result.substring(1, result.length());
       s =  PostRequestUtil.sendPost(NOWPAY_GATEWAY_NEW, s);
       if(s.contains("responseCode=A001&tn=")){
    	   result = s.substring(21, s.length());
    	  try {
    		  result = URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
       }
        return result;
        
    }
    
}
