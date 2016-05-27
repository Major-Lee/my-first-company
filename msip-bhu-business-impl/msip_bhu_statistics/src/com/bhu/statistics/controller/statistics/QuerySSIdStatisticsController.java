package com.bhu.statistics.controller.statistics;


import java.io.IOException;
import java.util.ArrayList;
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
		//当前页数
		String pageIndex = StringUtils.EMPTY;
		//每页显示条数
		String pageSize = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
			pageIndex = object.getString("pn");
			pageSize = object.getString("ps");
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
		List<Map<String,Object>> pageMapList = new ArrayList<Map<String,Object>>();
		//总条数
		int totalCount = dateUmList.size();
		//总页数
		int pagecount=0; 
		//每页显示总数
		int ps = Integer.parseInt(pageSize);
		//当前页码
		int pn = Integer.parseInt(pageIndex);
		
		int m=totalCount%ps;
		if(m>0){
			pagecount=totalCount/ps+1; 
		}else{
			pagecount=totalCount/ps;  
		}
		//截取起始下标
		int fromIndex = (pn - 1) * ps;
		//截取截止下标
		int toIndex = pn * ps;
		if(totalCount <= ps){
			pageMapList = dateUmList.subList(0, totalCount);
		}else{
			pageMapList = dateUmList.subList(fromIndex, toIndex);
		}
		Map<String,Object> totalMap=(Map<String, Object>) umResResult.get("total");
		totalMap.put("ssid", resResult.get("totalSSID"));;
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("total", umResResult.get("total"));
		//resMap.put("dateList", dateUmList);
		resMap.put("dateList", pageMapList);
		String lastResult = NotifyUtil.success(resMap);
		return lastResult;
	}
}
