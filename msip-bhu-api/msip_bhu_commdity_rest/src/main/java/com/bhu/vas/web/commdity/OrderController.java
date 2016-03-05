package com.bhu.vas.web.commdity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController{
	@ResponseBody()
	@RequestMapping(value="/v1",method={RequestMethod.GET,RequestMethod.POST})
	public void bson(
			HttpServletRequest request,
			HttpServletResponse response/*,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String uids*/) {

		try{
			//String[] uidarray = StringHelper.split(uids, StringHelper.COMMA_STRING_GAP);
			//List<Object> ret = UserPlayDurationService.getInstance().hget_pipeline_playDurations(ArrayHelper.toList(uidarray));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed("ping ok.."));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
