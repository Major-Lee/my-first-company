package com.bhu.statistics.util.um;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.bhu.statistics.util.OpenApi;


/**
 * 带有重试功能的open api示例.
 * 
 * 将OpenApi.java复制到自己的项目中即可.
 * 
 * @author xuqian
 *
 */
public class RetryableOpenApiSample {
	public static final String api_key = "0454f1ccb749c988de1558162a1b9bac";
	public static final String api_secret = "d1313b0831952d3477218622d9fef2b6";

	public static void main(String[] args) {

		// 执行自己的请求
		runRequest();

		// 代码的最后，关闭openapi连接池.
		OpenApi.getInstance().shutdown();
	}

	/**
	 * 执行自己的一个请求
	 */
	private static void runRequest() {
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		argMap.put("api_key", api_key);
		argMap.put("systemtime",
				String.valueOf(System.currentTimeMillis() / 1000));
		argMap.put("expire", String.valueOf(300));
		argMap.put("event_name", "pv");
		argMap.put("from_date", "2015-05-03");
		argMap.put("to_date", "2015-05-10");
		argMap.put("type", "all");
		argMap.put("on", "date");
		argMap.put("where", "city in ( '北京市','邢台市','其它' )");
		argMap.put("on", Base64.encodeBase64String(argMap.get("on").getBytes()));
		argMap.put("where",
				Base64.encodeBase64String(argMap.get("where").getBytes()));

		// 得到openapi
		OpenApi openapi = OpenApi.getInstance();

		// 构造请求
		String url = new StringBuilder(
				"https://dplus.cnzz.com/api/segmentation?")
				.append(openapi.generateUrl(argMap)).append("&sign=")
				.append(openapi.generateSignature(argMap, api_secret))
				.toString();

		String response = openapi.executeRequest(url);
		System.out.println(response);
	}
}
