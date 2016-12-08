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
		String aa = "result=1&pay_message=&agent_id=2069082&jnet_bill_no=H1612068664835AM&agent_bill_id=PROWPWX1480986364582ieja&pay_type=30&pay_amt=0.35&remark=&sign=55982346017a69747c4ca53042d0c8de";
		Object sr = sendPost("http://localhost:8080/msip_bhu_payment_rest/payment/heepayNotifySuccess", aa);
//		String aa = "action=send&userid=&account=huaxinlianchuang&password=100032&mobile=18515465766&sendTime=&content=验证码123455请勿将验证码泄露给他人【必虎】";
//		Object sr = sendPost("http://sms.chanzor.com:8001/sms.aspx", aa);
		//Object sr = sendPost("http://m.api.dianping.com/tohome/openapi/jiadianguanjia/", par);

		System.out.println( sr);
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
