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
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/urouter/device")
public class URouterDeviceController extends BaseController{
	
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
	
	/**
	 * 获取设备的终端列表 
	 * 默认按照终端的速率倒序排序 在线排序
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param status 1:在线 2:离线 0:所有
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value="/hd_list",method={RequestMethod.GET,RequestMethod.POST})
	public void hd_list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="1") int status,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
		List<URouterHdVTO> vtos = deviceURouterRestRpcService.urouterHdList(uid, mac, status, start, size);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
	}
	
	/**
	 * 设备的下行速率查询
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/device_rx_rate",method={RequestMethod.GET,RequestMethod.POST})
	public void device_rx_rate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		URouterRealtimeRateVTO vto = deviceURouterRestRpcService.urouterRealtimeRate(uid, mac);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
	
}
