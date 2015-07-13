package com.bhu.vas.web.device;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.vto.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
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
	@RequestMapping(value="/enter",method={RequestMethod.POST})
	public void enter(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {

		RpcResponseDTO<URouterEnterVTO> rpcResponse = deviceURouterRestRpcService.urouterEnter(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
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
	@RequestMapping(value="/hd_list",method={RequestMethod.POST})
	public void hd_list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="1") int status,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
		RpcResponseDTO<Map<String,Object>> rpcResponse = deviceURouterRestRpcService.urouterHdList(uid, mac, status, start, size);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}


	@ResponseBody()
	@RequestMapping(value="/hd_detail",method={RequestMethod.POST})
	public void hd_detail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String hd_mac) {

		RpcResponseDTO<URouterHdDetailVTO> rpcResponse = deviceURouterRestRpcService.urouterHdDetail(uid, mac, hd_mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}

	}



	
	/**
	 * 设备的实时速率查询
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/device_rx_rate",method={RequestMethod.POST})
	public void device_rx_rate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterRealtimeRateVTO> rpcResponse = deviceURouterRestRpcService.urouterRealtimeRate(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 设备的网速测试调用
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param type 1为只测试下行速率 2为只测试上行速率 3 都测
	 */
	@ResponseBody()
	@RequestMapping(value="/device_peak_section",method={RequestMethod.POST})
	public void device_peak_section(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="1") int type) {
		
		RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterPeakSection(uid, mac, type);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 获取设备测速的分段数据列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/device_peak_section_fetch",method={RequestMethod.POST})
	public void device_peak_section_fetch(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterPeakSectionsVTO> rpcResponse = deviceURouterRestRpcService.urouterPeakSectionFetch(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	/**
	 * 获取设备黑名单列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/block_list",method={RequestMethod.POST})
	public void block_list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
		RpcResponseDTO<Map<String,Object>> rpcResponse = deviceURouterRestRpcService.urouterBlockList(uid, mac, start, size);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 设备的设置信息接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/setting",method={RequestMethod.POST})
	public void setting(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterSettingVTO> rpcResponse = deviceURouterRestRpcService.urouterSetting(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 设备上网方式设置显示接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/mode",method={RequestMethod.POST})
	public void mode(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterModeVTO> rpcResponse = deviceURouterRestRpcService.urouterLinkMode(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}

	/**
	 * 获取设备的智能插件数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/plugins",method={RequestMethod.POST})
	public void plugins(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<Map<String,Object>> rpcResponse = deviceURouterRestRpcService.urouterPlugins(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 插件设置 终端上线通知数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param on 终端上线通知开关
	 * @param stranger_on 陌生终端通知开关
	 * @param timeslot 时间段
	 * @param timeslot_mode 时间段通知模式
	 */
	@ResponseBody()
	@RequestMapping(value="/upd_plugin_terminal_online",method={RequestMethod.POST})
	public void upd_plugin_terminal_online(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) boolean on,
			@RequestParam(required = true) boolean stranger_on,
			@RequestParam(required = false, defaultValue=UserTerminalOnlineSettingDTO.Default_Timeslot) String timeslot,
			@RequestParam(required = true) int timeslot_mode) {
		
		RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterUpdPluginTerminalOnline(
				uid, mac, on, stranger_on, timeslot, timeslot_mode);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}

	@ResponseBody()
	@RequestMapping(value="/upd_plugin_wifisniffer",method={RequestMethod.POST})
	public void upd_plugin_wifisniffer(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) boolean on) {
		RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterUpdPluginWifisniffer(uid, mac, on);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/device_query_used_status",method={RequestMethod.POST})
	public void device_query_used_status(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		RpcResponseDTO<DeviceUsedStatisticsDTO> rpcResponse = deviceURouterRestRpcService.urouterDeviceUsedStatusQuery(uid, mac);
		if(!rpcResponse.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	
	/**
	 * 获取urouter设备的相关配置数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/configs",method={RequestMethod.POST})
	public void configs(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterDeviceConfigVTO> rpcResponse = deviceURouterRestRpcService.urouterConfigs(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 获取多个终端的hostname
	 * @param request
	 * @param response
	 * @param uid
	 * @param macs
	 */
	@ResponseBody()
	@RequestMapping(value="/terminal_hostnames",method={RequestMethod.POST})
	public void terminal_hostnames(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String macs) {
		
		RpcResponseDTO<List<URouterHdHostNameVTO>> rpcResponse = deviceURouterRestRpcService.terminalHostnames(uid, macs);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 获取路由器管理密码
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch/admin_password",method={RequestMethod.POST})
	public void fetchAdminPassword(HttpServletRequest request,
								   HttpServletResponse response,
								   @RequestParam(required = true) Integer uid,
								   @RequestParam(required = true) String mac) {
		RpcResponseDTO<URouterAdminPasswordVTO> rpcResponse = deviceURouterRestRpcService.urouterAdminPassword(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}

	/**
	 * 获取路由器vap密码
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch/vap_password",method={RequestMethod.POST})
	public void fetchVapPassword(HttpServletRequest request,
								   HttpServletResponse response,
								   @RequestParam(required = true) Integer uid,
								   @RequestParam(required = true) String mac) {
		RpcResponseDTO<URouterVapPasswordVTO> rpcResponse = deviceURouterRestRpcService.urouterVapPassword(uid, mac);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
}
