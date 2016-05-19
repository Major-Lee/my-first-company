package com.bhu.statistics.util.um;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

/**
 * 简单的open api示例
 * 
 * @author xuqian
 *
 */
public class OpenApiSample {

	public static void main(String[] args) throws ClientProtocolException,
			IOException, InterruptedException {
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		argMap.put("api_key", "");
		argMap.put("systemtime",
				String.valueOf(System.currentTimeMillis() / 1000));
		argMap.put("expire", String.valueOf(300));
		argMap.put("event_name", "");
		argMap.put("from_date", "");
		argMap.put("to_date", "");
		argMap.put("type", "all");
		if(!"".equals("")){
			argMap.put("on", "");
			argMap.put("on", Base64.encodeBase64String(argMap.get("on").getBytes()));
		}
		if(!"".equals("")){
			argMap.put("where", "");
			argMap.put("where",Base64.encodeBase64String(argMap.get("where").getBytes()));
		}
		String url = new StringBuilder(
				"https://dplus.cnzz.com/api/segmentation?")
				.append(generateUrl(argMap)).append("&sign=")
				.append(generateSignature(argMap, "")).toString();

		String response = Request.Get(url).execute().returnContent().asString();
		System.out.println(response);
	}

	/**
	 * 根据参数计算签名sign
	 * 
	 * @param argMap
	 *            参数
	 * @param secret
	 *            api secret
	 * @return 签名sign
	 */
	public static String generateSignature(Map<String, String> argMap,
			String secret) {
		try {
			argMap.remove("sign");
			List<String> keyList = new ArrayList<String>(argMap.keySet());
			Collections.sort(keyList);

			StringBuilder textBuilder = new StringBuilder();
			for (String key : keyList) {
				if (textBuilder.length() != 0) {
					textBuilder.append("&");
				}
				textBuilder.append(URLEncoder.encode(key, "utf-8")).append("=")
						.append(URLEncoder.encode(argMap.get(key), "utf-8"));
			}
			return hmac_sha1(textBuilder.toString(), secret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成请求的url
	 * 
	 * @param argMap
	 *            参数
	 * @return 请求的url
	 */
	public static String generateUrl(Map<String, String> argMap) {
		try {
			argMap.remove("sign");
			List<String> keyList = new ArrayList<String>(argMap.keySet());
			Collections.sort(keyList);

			StringBuilder textBuilder = new StringBuilder();
			for (String key : keyList) {
				if (textBuilder.length() != 0) {
					textBuilder.append("&");
				}
				textBuilder.append(URLEncoder.encode(key, "utf-8")).append("=")
						.append(URLEncoder.encode(argMap.get(key), "utf-8"));
			}
			return textBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据text和secret计算出签名
	 * 
	 * @param text
	 *            文本
	 * @param secret
	 *            api secret
	 * @return 签名
	 */
	private static String hmac_sha1(String text, String secret) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(),
					"HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(text.getBytes());
			String hexBytes = bytes2HexString(rawHmac);
			return hexBytes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将byte数组转换为16进制的string
	 * 
	 * @param bytes
	 *            byte数组
	 * @return 转换为的16进制的string
	 */
	private static String bytes2HexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp;
		for (int n = 0; n < bytes.length; n++) {
			tmp = Integer.toHexString(bytes[n] & 0xFF);
			if (tmp.length() == 1)
				sb.append("0").append(tmp);
			else
				sb.append(tmp);
		}
		return sb.toString();
	}
}
