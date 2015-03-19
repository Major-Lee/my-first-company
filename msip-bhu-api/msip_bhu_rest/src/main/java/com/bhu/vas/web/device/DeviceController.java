package com.bhu.vas.web.device;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device")
public class DeviceController {
	
	@Resource
	private IDeviceRestRpcService deviceRestRpcService;
	
	/**
	 * 获取最繁忙的TOP5wifi设备
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_max_busy_devices",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_max_busy_devices(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {

		try{
			List<WifiDeviceMaxBusyVTO> vtos = deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
