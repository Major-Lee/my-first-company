package com.bhu.statistics.controller.statistics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;
@Controller
public class QueryStatisticsFromUMController extends BaseController{
	@ResponseBody
	@RequestMapping(value="/index", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String queryPriceMgtInfo(HttpServletRequest request,HttpServletResponse response,String data){
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		//data="{\"event_name\":\"$Pv\",\"from_date\":\"2016-05-01\",\"to_date\":\"2016-05-20\",\"on_condition\":\"\",\"where_condition\":\"\"}";
		JSONObject jsonObject=JSONObject.fromObject(data);
		String event_name=jsonObject.getString("event_name");
		String from_date=jsonObject.getString("from_date");
		String to_date=jsonObject.getString("to_date");
		String on_condition=jsonObject.getString("on_condition");
		String where_condition=jsonObject.getString("where_condition");
		//String result=apiCnzzImpl.queryCnzzStatistic("$Pv", "2016-05-01", "2016-05-20", "", "");
		String result=apiCnzzImpl.queryCnzzStatistic(event_name, from_date, to_date, on_condition, where_condition);
		return result;
	}
}
