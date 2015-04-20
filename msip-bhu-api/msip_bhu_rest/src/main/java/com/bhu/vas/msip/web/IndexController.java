package com.bhu.vas.msip.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController extends BaseController{

	/**
	 * 解决这个URI找不到导致idea控制台一直刷log
	 * [org.springframework.web.servlet.PageNotFoun -
	 * No mapping found for HTTP request with URI [/] in DispatcherServlet with name 'springmvc']
	 * @param response
	 */
	@RequestMapping(value="/", method = RequestMethod.GET)
	public void index(HttpServletResponse response) {
		logger.info("RequestMapping Rest Request URL [/] ...");
	}

	@RequestMapping("/index.html")
	public ModelAndView login(ModelAndView mv){
		mv.addObject("test", "请登录");
		mv.setViewName("/index");
		this.prepareModelAndView(mv);
		return mv;
	}

	@Override
	protected void prepareModelAndView(ModelAndView mv) {
		super.prepareCtx4ModelAndView(mv);
	}
}
