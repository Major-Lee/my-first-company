package com.bhu.statistics.controller.statistics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;
@Controller
@RequestMapping("/bhu_api/v1/msip_bhu_statistics/")
public class QueryStatisticsController extends BaseController{
	@ResponseBody
	@RequestMapping(value="statisticsUm",method = { RequestMethod.GET,
			RequestMethod.POST })
	public String queryStatistics(HttpServletRequest request,HttpServletResponse response,String data){
		
		staticsService.queryStatisticsByUM(data);
		return "";
	}
}
