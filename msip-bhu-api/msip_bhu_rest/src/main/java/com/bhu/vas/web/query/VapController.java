package com.bhu.vas.web.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.StaticResultController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/vap")
public class VapController extends BaseController {

	@ResponseBody()
	@RequestMapping(value="/url404",method={RequestMethod.POST})
	public void url404(
			HttpServletRequest request,
			HttpServletResponse response) {
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed("http://192.168.66.7:8080/"));
	}
	
	/**
	 * 只能站内forward
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody()
	@RequestMapping(value="/f404",method={RequestMethod.POST})
	public ModelAndView f404(
			HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView("forward:/11",null);
		//ModelAndView mv = new ModelAndView();
		//mv.setView(new );
		//return "forward:http://baidu.com";
	}
	
	/**
	 * 允许redirect到站外url
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody()
	@RequestMapping(value="/r404",method={RequestMethod.POST})
	public ModelAndView r404(
			HttpServletRequest request,
			HttpServletResponse response) {
		//return "gogogog";
		ModelAndView mv = new ModelAndView();
        StaticResultController.redirectURL(mv, "http://baidu.com", 100, "redirect");//.redirectError(mv, servletContext.getContextPath()+"/index.html", ex.getMessage());
        return mv;
	}
}
