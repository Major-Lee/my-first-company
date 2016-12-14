package com.bhu.vas.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * open api的单例封装
 * 
 * @author xuqian
 *
 */
public class OpenApi {
	private static final Logger logger = LoggerFactory.getLogger(OpenApi.class);

	// 最大重试次数, 默认为5次.
	private volatile int maxRetryTimes = 5;

	private static OpenApi instance;

	private final HttpClient httpclient;

	public static OpenApi getInstance() {
		if (instance == null) {
			synchronized (OpenApi.class) {
				if (instance == null) {
					instance = new OpenApi();
				}
			}
		}
		return instance;
	}

	public OpenApi() {
		httpclient = new DefaultHttpClient();
	}

	/**
	 * 设置最大重试次数，默认为5次.
	 * 
	 * @param maxRetryTimes
	 *            新的最大重试次数.
	 */
	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	/**
	 * 执行请求，遇到异常时进行重试，最多重试MAX_RETRY_TIME次.
	 * 
	 * @param url
	 *            请求的url
	 * @return 返回的结果
	 */
	public String executeRequest(String url) {
		String responseBody = null;
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		int currentRetryTime = 1;
		while (currentRetryTime <= maxRetryTimes) {
			try {
				responseBody = httpclient.execute(httpGet, responseHandler);
				break;
			} catch (ClientProtocolException e) {
				logger.warn("请求url异常, {}秒后重试", currentRetryTime, e);
				currentRetryTime++;
				try {
					Thread.sleep(currentRetryTime * 1000);
				} catch (InterruptedException e1) {
					logger.error("中断异常", e1);
				}

			} catch (IOException e) {
				logger.warn("请求url异常, {}秒后重试", currentRetryTime, e);
				currentRetryTime++;
				try {
					Thread.sleep(currentRetryTime * 1000);
				} catch (InterruptedException e1) {
					logger.error("中断异常", e1);
				}
			}
		}
		if (currentRetryTime > maxRetryTimes) {
			logger.error("请求url异常, 重试{}次后仍然失败, 放弃重试. ", maxRetryTimes);
		}
		return responseBody;
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
	public String generateSignature(Map<String, String> argMap, String secret) {
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
	public String generateUrl(Map<String, String> argMap) {
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
	private String hmac_sha1(String text, String secret) {
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
	private String bytes2HexString(byte[] bytes) {
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

	/**
	 * 在程序的最后，来关闭open api连接池.
	 */
	public void shutdown() {
		httpclient.getConnectionManager().shutdown();
	}
}
