package com.bhu.statistics.util.um;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.OpenApi;

public class OpenApiCnzzImpl implements IopenApiCnzz {
	public String queryCnzzStatistic(String event_name,String from_date,String to_date,String on_condition,String where_condition){
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		argMap.put("api_key", api_key);
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
		String url = new StringBuilder("https://dplus.cnzz.com/api/segmentation?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		argMap.put("type", "count");
		argMap.put("metric", "ip");
		String urlIP = new StringBuilder("https://dplus.cnzz.com/api/segmentation?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		//String url = new StringBuilder("https://dplus.cnzz.com/api/events/names?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		//String url = new StringBuilder("https://dplus.cnzz.com/api/events/properties/names?").append(openapi.generateUrl(argMap)).append("&sign=").append(openapi.generateSignature(argMap, api_secret)).toString();
		String response = openapi.executeRequest(url);
		String responseIp = openapi.executeRequest(urlIP);
		JSONObject resJson=JSONObject.fromObject(response);
		List<Double> resList=(List<Double>) resJson.get("values");
		JSONObject resJsonIp=JSONObject.fromObject(responseIp);
		List<Double> resIpList=(List<Double>) resJsonIp.get("values");
		
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("pv", Double.toString(resList.get(0)));
		resMap.put("uv", Double.toString(resList.get(1)));
		resMap.put("ip", Double.toString(resIpList.get(0)));
		JSONObject resultJson=JSONObject.fromObject(resMap);
		return resultJson.toString();
	}
}
