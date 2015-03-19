package com.bhu.vas.web.device;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device/status")
public class DeviceStatusController {
	
	@ResponseBody()
	@RequestMapping(value="/general",method={RequestMethod.GET,RequestMethod.POST})
	public void bson(
			HttpServletRequest request,
			HttpServletResponse response) {

		try{
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed("ping ok.."));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
