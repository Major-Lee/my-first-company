package com.bhu.statistics.controller.statistics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;
import com.bhu.statistics.util.DataUtils;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;
@Controller
@RequestMapping("/bhu_api/v1/msip_bhu_statistics")
public class QueryStatisticsFromUMController extends BaseController{
	@ResponseBody
	@RequestMapping(value="/index", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String queryIndexInfo(HttpServletRequest request,HttpServletResponse response,String data){
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		JSONObject jsonObject=JSONObject.fromObject(data);
		String event_name=jsonObject.getString("event_name");
		String type=jsonObject.getString("type");
		String from_date=StringUtils.EMPTY;
		String to_date=StringUtils.EMPTY;
		if("1".equals(type)){
			String currDay=DataUtils.currDay();
			from_date=currDay;
			to_date=currDay;
		}else if("2".equals(type)){
			String beforeDay=DataUtils.beforeDay();
			from_date=beforeDay;
			to_date=beforeDay;
		}else if("3".equals(type)){
			from_date=DataUtils.firstDay();
			to_date=DataUtils.currDay();
		}else{
			from_date=jsonObject.getString("from_date");
			to_date=jsonObject.getString("to_date");
		}
		String on_condition=jsonObject.getString("on_condition");
		String where_condition=jsonObject.getString("where_condition");
		String result=apiCnzzImpl.queryCnzzStatistic(event_name, from_date, to_date, on_condition, where_condition,1);
		return result;
	}
	@ResponseBody
	@RequestMapping(value="/queryEventNames", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String queryEventNames(HttpServletRequest request,HttpServletResponse response,String data){
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		String result=apiCnzzImpl.queryCnzzEvents();
		return result;
	}
	
}
