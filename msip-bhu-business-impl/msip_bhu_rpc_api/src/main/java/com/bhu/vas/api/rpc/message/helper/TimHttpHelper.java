package com.bhu.vas.api.rpc.message.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimHttpHelper {
	private static Logger log = LoggerFactory.getLogger(TimHttpHelper.class);
	public static final String DEFAULT_ENCODE = "utf-8";
	
	
	/**
	 * POST请求, 结果以字符串形式返回.
	 * 
	 * @param url 请求地址
	 * @param queryString queryString参数
	 * @param rawBody 请求体
	 * @return 内容字符串
	 */
	public static String postUrlAsString(String url, 
			Map<String, String> queryString, 
			Map<String, String> header,
			String rawBody)
			throws Exception {
		return postUrlAsString(url, queryString, header, rawBody,DEFAULT_ENCODE);
	}
	
	
	/**
	 * POST请求, 结果以字符串形式返回.
	 * 
	 * @param url 请求地址
	 * @param queryString queryString参数
	 * @param rawBody 请求体
	 * @return 内容字符串
	 */
	public static String postUrlAsString(String url, 
			Map<String, String> queryString, 
			String rawBody)
			throws Exception {
		return postUrlAsString(url, queryString, null, rawBody,DEFAULT_ENCODE);
	}
	public static String postUrlAsString(String url,
			Map<String, String> queryString, Map<String, String> reqHeader,
			String rawBody, String encode) throws Exception {
		// 开始时间
		long t1 = System.currentTimeMillis();
		// 获得HttpPost对象
		HttpPost httpPost = getHttpPost(url, queryString, rawBody, encode);
		// 发送请求
		String result = executeHttpRequest(httpPost, reqHeader);
		// 结束时间
		long t2 = System.currentTimeMillis();
		// 调试信息
		log.debug("url:" + url);
		log.debug("params:" + queryString.toString());
		log.debug("reqHeader:" + reqHeader);
		log.debug("encode:" + encode);
		log.debug("result:" + result);
		log.debug("consume time:" + ((t2 - t1)));
		// 返回结果
		return result;
	}
	
	
	/**
	 * 获得HttpPost对象
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param encode
	 *            编码方式
	 * @return HttpPost对象
	 */
	private static HttpPost getHttpPost(String url, Map<String, String> queryString,
			String rawBody, String encode) {
		StringBuffer buf = new StringBuffer(url);
		if (queryString != null) {
			// 地址增加?或者&
			String flag = (url.indexOf('?') == -1) ? "?" : "&";
			// 添加参数
			for (String name : queryString.keySet()) {
				buf.append(flag);
				buf.append(name);
				buf.append("=");
				try {
					String param = queryString.get(name);
					if (param == null) {
						param = "";
					}
					buf.append(URLEncoder.encode(param, encode));
				} catch (UnsupportedEncodingException e) {
					log.error("URLEncoder Error,encode=" + encode + ",param="
							+ queryString.get(name), e);
				}
				flag = "&";
			}
		}
		HttpPost httpPost = new HttpPost(buf.toString());
		try {
			httpPost.setEntity(new StringEntity(rawBody,encode));
		} catch (UnsupportedEncodingException e) {
			log.error("httpPost setEntity Error",e);
		}
		return httpPost;
	}
	
	
	/**
	 * 执行HTTP请求
	 * 
	 * @param request
	 *            请求对象
	 * @param reqHeader
	 *            请求头信息
	 * @return 内容字符串
	 */
	private static String executeHttpRequest(HttpUriRequest request,
			Map<String, String> reqHeader) throws Exception {
		HttpClient client = null;
		String result = null;
		try {
			// 创建HttpClient对象
			client = new DefaultHttpClient();
			// 设置连接超时时间
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
			// 设置Socket超时时间
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					5000);
			// 设置请求头信息
			if (reqHeader != null) {
				for (String name : reqHeader.keySet()) {
					request.addHeader(name, reqHeader.get(name));
					//System.out.println(name+":::"+reqHeader.get(name));
				}
			}
			// 获得返回结果
			HttpResponse response = client.execute(request);
			// 如果成功
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			}
			// 如果失败
			else {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("httpStatus:");
				errorMsg.append(response.getStatusLine().getStatusCode());
				errorMsg.append(response.getStatusLine().getReasonPhrase());
				errorMsg.append(", Header: ");
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					errorMsg.append(header.getName());
					errorMsg.append(":");
					errorMsg.append(header.getValue());
				}
				log.error("HttpResonse Error:" + errorMsg);
			}
		} catch (Exception e) {
			log.error("http连接异常", e);
			throw new Exception("http连接异常");
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				log.error("finally HttpClient shutdown error", e);
			}
		}
		return result;
	}

}
