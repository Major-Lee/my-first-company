package com.bhu.vas.rpc.util.um;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.bhu.vas.rpc.util.OpenApi;


public class OpenApiCnzzImpl implements IopenApiCnzz {
	public String queryCnzzStatistic(String event_name,String from_date,String to_date,String on_condition,String where_condition,int type){
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		String rapi_key=StringUtils.EMPTY;
		String rapi_secret=StringUtils.EMPTY;
		if(type==1){
			rapi_key=api_key;
			rapi_secret=api_secret;
		}else{
			rapi_key=Mapi_key;
			rapi_secret=Mapi_secret;
		}
		argMap.put("api_key", rapi_key);
		argMap.put("systemtime",String.valueOf(System.currentTimeMillis() / 1000));
		argMap.put("expire", String.valueOf(300));
		argMap.put("event_name", event_name);
		argMap.put("from_date", from_date);
		argMap.put("to_date", to_date);
		argMap.put("type", "all");
		if(!on_condition.equals("")){
			argMap.put("on", on_condition);
			argMap.put("on", Base64.encodeBase64String(argMap.get("on").getBytes()));
		}
		if(!where_condition.equals("")){
			argMap.put("where", where_condition);
			argMap.put("where",Base64.encodeBase64String(argMap.get("where").getBytes()));
		}
		// 得到openapi
		OpenApi openapi = OpenApi.getInstance();
		// 构造请求
		String url = new StringBuilder("https://dplus.cnzz.com/api/segmentation?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, rapi_secret)).toString();
		System.out.println("---------------------------------"+url);
		//		argMap.put("type", "count");
//		argMap.put("on", "ip");
//		argMap.put("on", Base64.encodeBase64String(argMap.get("on").getBytes()));
//		String urlIP = new StringBuilder("https://dplus.cnzz.com/api/segmentation?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		String response = openapi.executeRequest(url);
		//String responseIp = openapi.executeRequest(urlIP);
//		JSONObject resJson=JSONObject.fromObject(response);
//		List<Double> resList=(List<Double>) resJson.get("values");
//		JSONObject resJsonIp=JSONObject.fromObject(responseIp);
//		String resIpList=resJsonIp.get("values").toString();
//		String[] ipSizeArray=resIpList.split(",");
//		Map<String,Object> resMap=new HashMap<String,Object>();
//		resMap.put("pv", Double.toString(resList.get(0)).replaceAll(".0", ""));
//		resMap.put("uv", Double.toString(resList.get(1)).replaceAll(".0", ""));
		//resMap.put("ip", ipSizeArray.length);
		//resMap.put("eventName", event_name);
//		JSONObject resultJson=JSONObject.fromObject(resMap);
//		return resultJson.toString();
		return response;
	}
	public String queryCnzzEvents(){
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		argMap.put("api_key", api_key);
		argMap.put("systemtime",String.valueOf(System.currentTimeMillis() / 1000));
		argMap.put("expire", String.valueOf(300));
		// 得到openapi
		OpenApi openapi = OpenApi.getInstance();
		// 构造请求
		String url = new StringBuilder("https://dplus.cnzz.com/api/events/names?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		String response = openapi.executeRequest(url);
		response=response.replaceAll("\\[([^\\]]*)\\]", "$1").replace("\"","");
		return response;
	}
}
