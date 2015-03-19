package com.bhu.vas.web.device;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device")
public class DeviceController {
	
	@Resource
	private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	/**
	 * 获取最繁忙的TOP5wifi设备
	 * TODO：目前直接从mongodb中获取 后续改成后台程序定时从mongodb获取并放入指定的redis中 这边直接从redis提取数据
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_max_busy_devices",method={RequestMethod.GET,RequestMethod.POST})
	public void bson(
			HttpServletRequest request,
			HttpServletResponse response) {

		try{
			wifiHandsetDeviceLoginCountMService.findLoginCountsSortByCountDesc(1, 5);
			WifiDeviceMaxBusyVTO vto = new WifiDeviceMaxBusyVTO();
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
