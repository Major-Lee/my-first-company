package com.whisper.msip.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.whisper.msip.cores.web.mvc.spring.BaseController;

@Controller
public class IndexController extends BaseController{
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
