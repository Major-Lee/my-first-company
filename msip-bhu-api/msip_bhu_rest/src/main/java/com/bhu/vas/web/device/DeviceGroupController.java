package com.bhu.vas.web.device;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;

@Controller
@RequestMapping("/group")
public class DeviceGroupController extends BaseController{
	
	@Resource
	private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	/**
	 * 增加及修改群组
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/save",method={RequestMethod.POST})
	public void save(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = true) String name) {
	}
	
	/**
	 * 群组详细信息
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/detail",method={RequestMethod.POST})
	public void detail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer id) {
	}
	
	/**
	 * 删除群组
	 * @param request
	 * @param response
	 * @param ids 逗号分割
	 */
	@ResponseBody()
	@RequestMapping(value="/remove",method={RequestMethod.POST})
	public void remove(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) String ids) {
	}
	
	/**
	 * 给指定的群组分配wifi设备
	 * @param request
	 * @param response
	 * @param id
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/grant",method={RequestMethod.POST})
	public void grant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String wifi_ids) {
	}
	
	/**
	 * 从指定的群组删除wifi设备
	 * @param request
	 * @param response
	 * @param id
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/ungrant",method={RequestMethod.POST})
	public void ungrant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String wifi_ids) {
	}
	
}
