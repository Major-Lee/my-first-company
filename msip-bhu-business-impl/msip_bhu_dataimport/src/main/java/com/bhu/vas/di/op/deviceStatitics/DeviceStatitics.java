package com.bhu.vas.di.op.deviceStatitics;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStatisticsHashService;
import com.smartwork.msip.cores.helper.JsonHelper;

public class DeviceStatitics {
	private static final String REQUEST_URL = "http://vap.bhunetworks.com/bhu_api/v1/dashboard/device/statistics";
	public static void main(String[] args) {
		//共享网络开启状态  1 为开启 0为关闭
		String params = "d_snk_turnstate=1&d_snk_type=SafeSecure&sk=PzdfTFJSUEBHG0dcWFcLew==";
		String result = StringUtils.EMPTY;
		//请求接口获取设备总数以及设备总数
		result = sendPost(REQUEST_URL, params);
		 //设备数量
		int dc = 0;
		//在线设备数量
		int doc = 0;
		//解析参数
		Map<String,Object> helper = JsonHelper.getMapFromJson(result);
		System.out.println(helper);
		//JSONObject object = JSONObject.fromObject(result);
		if(StringUtils.equals((String) helper.get("code"), "200")){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) helper.get("result");
			dc = (Integer) map.get("dc");
			doc = (Integer) map.get("doc");
			System.out.println(dc);
			System.out.println(doc);
			//存储当前日期的设备数以及在线设备数至redis
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("dc", dc);
			resultMap.put("doc", doc);
			System.out.println(currDay());
			DeviceStatisticsHashService.getInstance().deviceMacHset(currDay(), "equipment", JsonHelper.getJSONString(resultMap));
			//String str = BhuCache.getInstance().getEquipment(DataUtils.currDay(), "equipment");
		}
		
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

	/**
	 * 取得当前日
	 * 
	 * @return 2015-09-14
	 */
	public static String currDay() {		
		Date d = new Date();  
        System.out.println(d);  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(d);  
        return dateNowStr;
	}
}