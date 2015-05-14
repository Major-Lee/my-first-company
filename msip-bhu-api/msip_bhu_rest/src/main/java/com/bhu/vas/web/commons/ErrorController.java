package com.bhu.vas.web.commons;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Controller
public class ErrorController extends BaseController{

	
	@ResponseBody()
	@RequestMapping(value = {"/error"},method={RequestMethod.POST})
	public void commons(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String url,
			@RequestParam(required = true) String message,
			@RequestParam(required = true) String legacy) {
		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_UNKNOW_ERROR));
	}
	
/*	 @RequestMapping(value = {"/commons/{commonCode}.html"})
	 public ModelAndView common(
			 ModelAndView mv, 
			 HttpServletRequest request,
			 @PathVariable String commonCode) {
		 //this.prepareModelAndView(mv);
		 //mv.setViewName("/commons/"+commonPage);
		 return mv;
	 }*/

}
