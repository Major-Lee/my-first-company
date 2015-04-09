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

import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/urouter/device")
public class URouterDeviceController {
	
	@Resource
	private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	/**
	 * 获取urouter入口界面数据
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/enter",method={RequestMethod.GET,RequestMethod.POST})
	public void enter(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		URouterEnterVTO vto = deviceURouterRestRpcService.urouterEnter(uid, mac);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
	
	@ResponseBody()
	@RequestMapping(value="/hd_online_list",method={RequestMethod.GET,RequestMethod.POST})
	public void hd_online_list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
		List<URouterHdVTO> vtos = deviceURouterRestRpcService.urouterHdOnlineList(uid, mac, start, size);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
	}
	
}
