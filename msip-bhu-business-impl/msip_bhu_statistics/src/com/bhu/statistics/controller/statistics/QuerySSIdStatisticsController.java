package com.bhu.statistics.controller.statistics;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.NotifyUtil;
import com.bhu.statistics.util.enums.ErrorCodeEnum;

@Controller
public class QuerySSIdStatisticsController extends BaseController{
	private static Logger log = Logger.getLogger(QuerySSIdStatisticsController.class);
	@ResponseBody
	@RequestMapping(value="/querySSIDStatist", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String querySSIDStatist(HttpServletRequest request,HttpServletResponse response,String data) throws IOException{
		String umRes=staticsService.queryStatisticsByUM(data);
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		}
		//时间类型
		String dateType = StringUtils.EMPTY;
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
 		} catch (Exception e) {
			log.info("JSON格式转换错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON格式转换错误", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		} 
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
			//按时间间隔查询SSID统计信息
			result = staticsService.querySSIDInfoByTime(data);
		}else{
			//按时间类型查询SSID统计信息
			result=staticsService.querySSIDInfoByType(data);
		}
		JSONObject umResJson=JSONObject.fromObject(umRes);
		JSONObject resJson=JSONObject.fromObject(result);
		@SuppressWarnings("unchecked")
		Map<String, Object> umResResult=(Map<String, Object>)umResJson.get("result");
		@SuppressWarnings("unchecked")
		Map<String, Object> resResult=(Map<String, Object>)resJson.get("result");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateUmList=(List<Map<String, Object>>) umResResult.get("dataList");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateSsidList=(List<Map<String, Object>>) resResult.get("ssidList");
		for(Map<String,Object> i:dateUmList){
			for(Map<String,Object> j:dateSsidList){
				if(j.get("currDate").equals(i.get("date"))){
					i.put("ssid", j);
					break;
				}
			}
		}
		Map<String,Object> totalMap=(Map<String, Object>) umResResult.get("total");
		totalMap.put("ssidTotal", resResult.get("totalSSID"));;
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("total", umResResult.get("total"));
		resMap.put("dateList", dateUmList);
		String lastResult = NotifyUtil.success(resMap);
		return lastResult;
	}
}
