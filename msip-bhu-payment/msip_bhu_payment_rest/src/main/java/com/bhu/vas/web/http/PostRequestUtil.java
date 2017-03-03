package com.bhu.vas.web.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * java发送post请求
 * 
 * @author zhaoxueyuan
 */
public class PostRequestUtil {
	public static void main(String[] args) {

		// String sr = sendPost("http://localhost:8080/hac/submitOrder", param);
		// String sr = sendPost("http://localhost:8080/queryItemServiceRanges",
		// null);
//		String par = "methodName=updateOrderStatus&orderId=2099021509170013&status=4&sign=1BA5FC1A19824109A6688339C0CF57F1";
	//	Object sr = sendPost("http://m.api.51ping.com/tohome/openapi/jiadianguanjia/", par);
//		String cur = System.currentTimeMillis()+"";
//		String par = "secret=1F915A8DA370422582CBAC1DB6A806DD&appid=1000&payment_type=WapWeixin&goods_no="+cur+"&total_fee=0.01&umac=ws:ww:22&version=v1";
		//Object sr = sendPost("http://localhost:8080/msip_bhu_payment_rest/payment/wxPayNotifySuccess", "aa=qq");
		
		//huaxinlianchuang   100032
		//http://sms.chanzor.com:8001/sms.aspx?action=send&userid=&account=账号&password=密码&mobile=手机号&sendTime=&content=内容
//		String aa = "action=send&userid=&account=huaxinlianchuang&password=100032&mobile=15127166171&sendTime=&content=验证码123455请勿将验证码泄露给他人【必虎】";
		
//		for (int i = 0; i < 10000; i++) {
////			String goodNo = RandomPicker.randString(BusinessHelper.letters, 10);
////			String aa = "secret=1F915A8DA370422582CBAC1DB6A806DD&appid=1000&"
////					+ "payment_type=PcWeixin&total_fee=0.01&umac=ws:ww:22&goods_no="+goodNo;
////			Object sr = sendPost("http://pays.bhuwifi.com/msip_bhu_payment_rest/payment/submitPayment", aa);
////			System.out.println( sr);
//			
//		}
		String aa = "appid=1002&secret=1F915A8DA370422582CBAC1DB6A806UU&withdraw_no=1hhd0ttgmmmbbn&withdraw_type=weixin&total_fee=10&userId=ohdupuGI3vkfnQSMFsPMlnQPdQvg&userName=张鹏宇";
		Object response = sendPost("http://upay.bhuwifi.com/msip_bhu_payment_rest/payment/submitWithdrawals", aa);
//		String aa = "openid=ohdupuGI3vkfnQSMFsPMlnQPdQvg&callback=sss";
//		Object response = sendPost("http://localhost:8080/wxpay/pay", aa);

		System.out.println(response);
		//		ResponsePaymentChannelSatDTO ss = JsonHelper.getDTO(response+"", ResponsePaymentChannelSatDTO.class);
//		List<PaymentChannelStatVTO>  paymentChannelList = ss.getResult();
//		//System.out.println( paymentChannelList.get(1).getAmount()+"info:"+paymentChannelList.get(1).getInfo());
//		String info = paymentChannelList.get(1).getInfo();
//		
//		JSONObject json;
//		try {
//			json = new JSONObject(info);
//			JSONObject now =  (JSONObject) json.get("hee");
//			//JSONObject jsons = new JSONObject(now);
//			System.out.println(now.get("amount"));
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			JSONObject dds = json.optJSONObject(info);
//			System.out.println(dds.get("hee"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Object[] ddd = JsonHelper.getObjectArrayFromJson(info);
//		System.out.println("info OK"+ddd);
//		ResponsePaymentInfoDTO dd = JsonHelper.getDTO(info, ResponsePaymentInfoDTO.class);
////		ResponsePaymentInfoDTO dd = (ResponsePaymentInfoDTO) JSONObject.stringToValue(info);
//		System.out.println(dd.getAlipay().get(0).getCount());;
//		info = JsonHelper.getJSONString(info);
//		System.out.println("info String" + info);
//		
//		List<ResponsePaymentInfoDetailDTO> heeInfoDeList = infoO.getHee();
//		ResponsePaymentInfoDetailDTO heeDetail = heeInfoDeList.get(0);
//		System.out.println("heeDetail amount"+heeDetail.getAmount());
//		String aa = "action=send&userid=&account=huaxinlianchuang&password=100032&mobile=18515465766&sendTime=&content=验证码123455请勿将验证码泄露给他人【必虎】";
//		Object sr = sendPost("http://sms.chanzor.com:8001/sms.aspx", aa);
		//Object sr = sendPost("http://m.api.dianping.com/tohome/openapi/jiadianguanjia/", par);

	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Accept-Encoding", "utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setConnectTimeout(2000);  
			conn.setReadTimeout(3000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
