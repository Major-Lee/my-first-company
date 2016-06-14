package com.midas.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

//import org.apache.http.client.utils.DateUtils;
import com.midas.commons.Config;
import com.midas.util.OpensnsException;
import com.midas.util.SnsSigCheck;

/* *
 *类名：MidasUtils
 *功能：基础API类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class MidasUtils {

	public static String submitOrder(String reckoningId, String total_fee, String ip,String subject,String openId)  {
		String result = "";
		Config.price = total_fee;
        Config.SerialNumber = reckoningId;
        Config.goodsmeta = subject;
        Config.openid = openId;
        Config.payitem = Config.SerialNumber + '*' +(Config.price) + '*' + Config.pay_num;
        String url_path = Config.url_path;
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("session_id", Config.session_id);
        parameters.put("session_type", Config.session_type);
        parameters.put("openid", Config.openid);
        parameters.put("openkey", Config.openkey);
        parameters.put("payUrl", Config.pay_url);
        parameters.put("money", Config.price);
        parameters.put("pay_token", Config.pay_token);
        parameters.put("appid", Config.appid);
        parameters.put("ts", getTimeStamp());
        parameters.put("payitem", Config.payitem);
        parameters.put("goodsmeta",Config.goodsmeta+"*"+"必虎服务" );
        parameters.put("pf", Config.pf);
        parameters.put("pfkey", Config.pfkey);
        parameters.put("zoneid", Config.zoneid+"");
        String secret = Config.secret+ "&";

        try {
			Config.sig = SnsSigCheck.makeSig("get",url_path,parameters,secret);
		} catch (OpensnsException e) {
			e.printStackTrace();
		}

        String prestr = createLinkString(parameters);

        String results = SendGET(Config.token_url+Config.url_path,prestr+"&sig="+Config.sig);
        //{"ret":0,"token":"0A666C05BB76758931A7A3112771F0E815342","url_params":"/v1/m29/1450006356/mobile_goods_info?token_id=0A666C05BB76758931A7A3112771F0E815342","attach":""}
        try {
        	JSONObject json = new JSONObject(results);
			Integer ret = (Integer) json.get("ret");
			if(ret != 0){
				System.out.println(results);
				return "error";
			}
			
			Config.url_params = (String) json.get("url_params");
			SortedMap<Object, Object> params = new TreeMap<Object, Object>();
			params.put("goodstokenurl", Config.url_params);
        	params.put("type", "goods");
        	params.put("appid", Config.appid);
        	params.put("zoneid", Config.zoneid);
        	params.put("pf", Config.pf);
        	params.put("pfkey", Config.pfkey);
        	params.put("session_id", Config.session_id);
        	params.put("session_type", Config.session_type);
        	params.put("openid", Config.openid);
        	params.put("openkey", Config.openkey);
        	params.put("payUrl", Config.pay_url);
        	params.put("money", Config.price);
			result = JSONObject.valueToString(params);
		} catch (JSONException e) {
			System.out.println("midas error:"+e.getMessage());
		}
       return result;
	}

	/**
	 * 校验签名
	 * @param params
	 * @param sig
	 * @return
	 */
	public static boolean verifySig(HashMap<String,String> params, String sig) {
		boolean result = false;
		String method = "";
		String url_path = Config.url_path;
		try {
			result = SnsSigCheck.verifySig(method, url_path, params, Config.secret, sig);
		} catch (OpensnsException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     * @throws UnsupportedEncodingException 
     */
    public static String createLinkString(Map<String, String> params){

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
	
    public static String SendGET(String url, String param) {
        String result = "";// 访问返回结果
        BufferedReader read = null;// 读取访问结果

        try {
            // 创建url
            URL realurl = new URL(url + "?" + param);
            System.out.println("HTTP [GET] "+realurl);
            // 打开连接
            URLConnection connection = realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立连接
            connection.connect();
            read = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;// 循环读取
            while ((line = read.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (read != null) {// 关闭流
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * 时间戳
     *
     * 作者: zhoubang
     * 日期：2015年6月26日 下午3:52:08
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
    
    public static void main(String[] args) {
    	String results = "{'ret':0,'token':'0A666C05BB76758931A7A3112771F0E815342','url_params':'/v1/m29/1450006356/mobile_goods_info?token_id=0A666C05BB76758931A7A3112771F0E815342','attach':''}";
        JSONObject aa;
		try {
			aa = new JSONObject(results);
			Integer ret = (Integer) aa.get("ret");
			System.out.println(ret);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
	}
}

