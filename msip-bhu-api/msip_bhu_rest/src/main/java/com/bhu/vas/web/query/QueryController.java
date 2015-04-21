package com.bhu.vas.web.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.StaticResultController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/query")
public class QueryController extends BaseController {
	@ResponseBody()
    @RequestMapping(value="/fetch_device_hasbinded",method={RequestMethod.GET})
    public void fetch_devicebinded(HttpServletResponse response,
    		@RequestParam(required = false) String jsonpcallback,
            @RequestParam(required = true) String mac) {
		
		if(StringUtils.isEmpty(jsonpcallback))
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
	    else
	       	SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseSuccess.embed(null));
        /*RpcResponseDTO<List<UserDeviceDTO>> userDeviceResult = userDeviceRpcService.fetchBindDevices(uid);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));*/
    }
	
	/**
	 * 只能站内forward
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody()
	@RequestMapping(value="/f404",method={RequestMethod.GET,RequestMethod.POST})
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
	@RequestMapping(value="/r404",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView r404(
			HttpServletRequest request,
			HttpServletResponse response) {
		//return "gogogog";
		ModelAndView mv = new ModelAndView();
        StaticResultController.redirectURL(mv, "http://baidu.com", 100, "redirect");//.redirectError(mv, servletContext.getContextPath()+"/index.html", ex.getMessage());
        return mv;
	}
}
