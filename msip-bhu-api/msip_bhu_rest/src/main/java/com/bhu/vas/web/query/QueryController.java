package com.bhu.vas.web.query;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/query")
public class QueryController extends BaseController {
	@ResponseBody()
    @RequestMapping(value="/fetch_device_hasbinded",method={RequestMethod.GET,RequestMethod.POST})
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
}
