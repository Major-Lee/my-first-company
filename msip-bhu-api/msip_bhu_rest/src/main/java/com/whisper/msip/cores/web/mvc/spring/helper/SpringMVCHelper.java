package com.whisper.msip.cores.web.mvc.spring.helper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.XStreamHelper;
import com.whisper.msip.cores.web.mvc.WebHelper;

public class SpringMVCHelper {
	//-- header 常量定义 --//
	private static final String HEADER_ENCODING = "encoding";
	private static final String HEADER_NOCACHE = "no-cache";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final boolean DEFAULT_NOCACHE = true;

	//-- content-type 常量定义 --//
	private static final String TEXT_TYPE = "text/plain";
	private static final String JSON_TYPE = "application/json";
	private static final String XML_TYPE = "text/xml";
	private static final String HTML_TYPE = "text/html";
	private static final String JS_TYPE = "text/javascript";
	private static final String STREAM_TYPE = "application/octet-stream";
	
	private static final String FORM_TYPE = "application/x-www-form-urlencoded";

	public static boolean isJsonRequest(HttpServletRequest request){
		String contentType = request.getContentType();
		if(contentType == null) return true;
		System.out.println("---------:"+contentType);
		//if(JSON_TYPE.equalsIgnoreCase(contentType)) return true;
		//if(FORM_TYPE.equalsIgnoreCase(contentType)) return true;
		if(contentType.toLowerCase().contains(FORM_TYPE)) return true;
		if(contentType.toLowerCase().contains(JSON_TYPE)) return true;
		//if(FORM_TYPE.equalsIgnoreCase(contentType)) return true;
		return false;
	}
	//private static ObjectMapper mapper = new ObjectMapper();

	//-- 取得Request/Response/Session的简化函数 --//
	/**
	 * 取得HttpSession的简化函数.
	 */
	public static HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}

	/**
	 * 取得HttpRequest的简化函数.
	 */
	public static HttpServletRequest getRequest(HttpServletRequest request) {
		return request;
	}

	/**
	 * 取得HttpResponse的简化函数.
	 */
	public static HttpServletResponse getResponse(HttpServletResponse resonse) {
		return resonse;//return ServletActionContext.getResponse();
	}

	/**
	 * 取得Request Parameter的简化方法.
	 */
	public static String getParameter(HttpServletRequest request, String name) {
		return request.getParameter(name);
	}

	//-- 绕过jsp/freemaker直接输出文本的函数 --//
	/**
	 * 直接输出内容的简便函数.

	 * eg.
	 * render("text/plain", "hello", "encoding:GBK");
	 * render("text/plain", "hello", "no-cache:false");
	 * render("text/plain", "hello", "encoding:GBK", "no-cache:false");
	 * 
	 * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
	 */
	public static void render(HttpServletResponse response, final String contentType, final String content, final String... headers) {
		response = initResponse(response,contentType, headers);
		try {
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void renderStream(HttpServletResponse response, String contentType, InputStream is, final String... headers) {
		if(contentType == null) contentType = STREAM_TYPE;
		response = initResponse(response,contentType, headers);
		OutputStream bos = null;
		try {
			byte data[]=new byte[4096];
			bos=new BufferedOutputStream(response.getOutputStream());
	        int len;
	        while((len=is.read(data))!=-1){ // 循环读取
	        	bos.write(data,0,len); // 写入到输出流
	        }
	        //清空输出缓冲流
	        bos.flush();
	        /*is.close();
	        bos.close();*/
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally{
			try {
				if(bos != null){
					//bos.flush();
					bos.close();
					bos = null;
				}
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	

	/**
	 * 直接输出文本.
	 * @see #render(String, String, String...)
	 */
	public static void renderText(HttpServletResponse response,final String text, final String... headers) {
		render(response,TEXT_TYPE, text, headers);
	}

	/**
	 * 直接输出HTML.
	 * @see #render(String, String, String...)
	 */
	public static void renderHtml(HttpServletResponse response, final String html, final String... headers) {
		render(response, HTML_TYPE, html, headers);
	}
	
//	/**
//	 * 直接输出IMAGE.
//	 * @see #render(String, String, String...)
//	 */
//	public static void renderImage(HttpServletResponse response, final InputStream inputStream, final String... headers) {
//		renderStream(response, HTML_TYPE, inputStream, headers);
//	}

	/**
	 * 直接输出XML.
	 * @see #render(String, String, String...)
	 */
	public static void renderXml(HttpServletResponse response, final String xml, final String... headers) {
		render(response, XML_TYPE, xml, headers);
	}
	public static void renderXml(HttpServletResponse response, final Object object, final String... headers) {
		render(response, XML_TYPE, XStreamHelper.toXML(object), headers);
	}
	/**
	 * 直接输出JSON.
	 * 
	 * @param jsonString json字符串.
	 * @see #render(String, String, String...)
	 */
/*	public static void renderJson(HttpServletResponse response, final String jsonString, final String... headers) {
		render(response, JSON_TYPE, jsonString, headers);
	}*/

	/**
	 * 直接输出JSON,使用Jackson转换Java对象.
	 * 
	 * @param data 可以是List<POJO>, POJO[], POJO, 也可以Map名值对.
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(HttpServletResponse response, final Object object, final String... headers) {
		renderJson(response,object,false,headers);
	}
	public static void renderJson(HttpServletResponse response, final Object object, boolean plainContentType, final String... headers) {
		String result = JsonHelper.getJSONString(object,false);//getObjectData(true, data);
		if(plainContentType)
			render(response,TEXT_TYPE,result,headers);
		else
			render(response,JSON_TYPE,result,headers);
	}
	
	public static void renderJson(HttpServletResponse response, final String jsonStr, final String... headers) {
		renderJson(response,jsonStr,false,headers);
	}	
	public static void renderJson(HttpServletResponse response, final String jsonStr, boolean plainContentType, final String... headers) {
		if(plainContentType)
			render(response,TEXT_TYPE,jsonStr,headers);
		else
			render(response,JSON_TYPE,jsonStr,headers);
	}
	
	
	/**
	 * 直接输出支持跨域Mashup的JSONP.
	 * 
	 * @param callbackName callback函数名.
	 * @param object Java对象,可以是List<POJO>, POJO[], POJO ,也可以Map名值对, 将被转化为json字符串.
	 */
	public static void renderJsonp(HttpServletResponse response, final String callbackName, final Object object, final String... headers) {
/*		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}*/
		String jsonString = JsonHelper.getJSONString(object);
		renderJsonp(response,callbackName,jsonString,headers);
		//String result = new StringBuilder().append(callbackName).append("(").append(jsonString).append(");").toString();
		//渲染Content-Type为javascript的返回内容,输出结果为javascript语句, 如callback197("{html:'Hello World!!!'}");
		//render(response, JS_TYPE, result, headers);
	}
	public static void renderJsonp(HttpServletResponse response, final String callbackName, final String jsonStr, final String... headers) {
		String result = new StringBuilder().append(callbackName).append("(").append(jsonStr).append(");").toString();
		//渲染Content-Type为javascript的返回内容,输出结果为javascript语句, 如callback197("{html:'Hello World!!!'}");
		render(response, JS_TYPE, result, headers);
	}
	/**
	 * 分析并设置contentType与headers.
	 */
	private static HttpServletResponse initResponse(HttpServletResponse response,final String contentType, final String... headers) {
		//分析headers参数
		String encoding = DEFAULT_ENCODING;
		boolean noCache = DEFAULT_NOCACHE;
		for (String header : headers) {
			String headerName = StringUtils.substringBefore(header, ":");
			String headerValue = StringUtils.substringAfter(header, ":");

			if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
				encoding = headerValue;
			} else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
				noCache = Boolean.parseBoolean(headerValue);
			} else {
				throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
			}
		}

		//HttpServletResponse response = ServletActionContext.getResponse();

		//设置headers参数
		String fullContentType = contentType + ";charset=" + encoding;
		response.setContentType(fullContentType);
		if (noCache) {
			WebHelper.setNoCacheHeader(response);
		}

		return response;
	}
}
