package com.bhu.statistics.controller.statistics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;

@Controller
public class QuerySSIdStatisticsController extends BaseController{
	@ResponseBody
	@RequestMapping(value="/querySSIDStatist", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String querySSIDStatist(HttpServletRequest request,HttpServletResponse response,String data){
		String result=staticsService.querySSIDInfoByType(data);
		return result;
	}
}
