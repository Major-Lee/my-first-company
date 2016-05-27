package com.bhu.statistics.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bhu.statistics.util.cache.BhuCache;
import com.bhu.statistics.util.file.FileHandling;
import com.bhu.statistics.util.http.RequestPostUtils;

/**
 * 设备数据统计
 * @author MyPC
 *
 */
public class EquipmentStatics {
	private static final String REQUEST_URL = "http://192.168.66.7/bhu_api/v1/dashboard/device/statistics";
	
	public static void main(String[] args) {
		//共享网络开启状态  1 为开启 0为关闭
		String params = "d_snk_turnstate=1&d_snk_type=SafeSecure&sk=PzdfTFJSUEBHG0dcWFcLew==";
		String result = StringUtils.EMPTY;
		//请求接口获取设备总数以及设备总数
		result = RequestPostUtils.sendPost(REQUEST_URL, params);
		 //设备数量
		int dc = 0;
		//在线设备数量
		int doc = 0;
		//解析参数
		JSONObject object = JSONObject.fromObject(result);
		if(object.getBoolean("success") == true){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) object.get("result");
			dc = (Integer) map.get("dc");
			doc = (Integer) map.get("doc");
			//存储当前日期的设备数以及在线设备数至redis
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("dc", dc);
			resultMap.put("doc", doc);
			BhuCache.getInstance().setEquipment(FileHandling.getNextDay(), "equipment", JSONObject.toJsonString(resultMap));
			String str = BhuCache.getInstance().getEquipment(FileHandling.getNextDay(), "equipment");
			System.out.println(str);
		}
	}
}
