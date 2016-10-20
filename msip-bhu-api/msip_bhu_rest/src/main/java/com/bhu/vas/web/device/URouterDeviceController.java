package com.bhu.vas.web.device;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.vto.URouterAdminPasswordVTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdDetailVTO;
import com.bhu.vas.api.vto.URouterHdHostNameVTO;
import com.bhu.vas.api.vto.URouterMainEnterVTO;
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakSectionsDTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.api.vto.URouterVapPasswordVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigMutilVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/urouter/device")
public class URouterDeviceController extends BaseController{
	
	@Resource
	private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	
	/**
	 * 获取urouter主入口界面数据
	 * 	hd_list接口需要终端的所有数据
		路由器上下行速率数据
		绑定的路由器在线状态、终端个数、路由器个数、路由器名称
		需要路由器版本号.
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/mainenter",method={RequestMethod.POST})
	public void mainenter(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterMainEnterVTO> rpcResult = deviceURouterRestRpcService.urouterMainEnter(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
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
		
		RpcResponseDTO<URouterEnterVTO> rpcResult = deviceURouterRestRpcService.urouterEnter(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
	 * @param filterWiredHandset true 结果集中过滤了有线终端  false不过滤
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
			@RequestParam(required = false, defaultValue="5", value = "ps") int size,
			@RequestParam(required = false, defaultValue="true", value = "fw") boolean filterWiredHandset
			) {
		
		RpcResponseDTO<Map<String,Object>> rpcResult = 
				deviceURouterRestRpcService.urouterHdList(uid, mac.toLowerCase(), status, start, size,filterWiredHandset?Boolean.TRUE:Boolean.FALSE);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}

	/**
	 * 获取设备的终端列表 (主网络访客网络统一)
	 * 默认按照终端的速率倒序排序 在线排序
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param start
	 * @param size
	 * @param filterWiredHandset true 结果集中过滤了有线终端  false不过滤
	 */
	@ResponseBody()
	@RequestMapping(value="/hd_unit_list",method={RequestMethod.POST})
	public void hd_list_all(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size,
			@RequestParam(required = false, defaultValue="true", value = "fw") boolean filterWiredHandset
			) {
		
		RpcResponseDTO<Map<String,Object>> rpcResult = 
				deviceURouterRestRpcService.urouterAllHdList(uid, mac.toLowerCase(), start, size,filterWiredHandset?Boolean.TRUE:Boolean.FALSE);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/count_hd",method={RequestMethod.POST})
	public void count_hd(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String macs,
			@RequestParam(required = true) Long timestamp
			) {
		
		RpcResponseDTO<Map<String, String>> rpcResult = 
				deviceURouterRestRpcService.countOnlineByTimestamp(uid, macs, timestamp);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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

		RpcResponseDTO<URouterHdDetailVTO> rpcResult = deviceURouterRestRpcService.urouterHdDetail(uid, mac.toLowerCase(), hd_mac);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}

	@ResponseBody()
	@RequestMapping(value="/hd_modify",method={RequestMethod.POST})
	public void hdModify(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String hd_mac,
			@RequestParam(required = true) String alias
			) {

		RpcResponseDTO<Long> rpcResult = deviceURouterRestRpcService.urouterHdModifyAlias(uid, mac.toLowerCase(), hd_mac.toLowerCase(), alias);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<URouterRealtimeRateVTO> rpcResult = deviceURouterRestRpcService.urouterRealtimeRate(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 设备的网速测试调用
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param type 1为只测试下行速率 2为只测试上行速率 3 都测
	 * @param period 测速数据上报间隔 秒
	 * @param duration 测速时长 秒
	 */
	@ResponseBody()
	@RequestMapping(value="/device_peak_section",method={RequestMethod.POST})
	public void device_peak_section(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="2", value = "pd") int period,
			@RequestParam(required = false, defaultValue="10", value = "dr") int duration,
			@RequestParam(required = false, defaultValue="1") int type) {
		
		RpcResponseDTO<String> rpcResult = deviceURouterRestRpcService.urouterPeakSection(uid, mac.toLowerCase(), type, period, duration);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<URouterPeakSectionsDTO> rpcResult = deviceURouterRestRpcService.urouterPeakSectionFetch(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<Map<String,Object>> rpcResult = deviceURouterRestRpcService.urouterBlockList(uid, mac.toLowerCase(), start, size);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<URouterSettingVTO> rpcResult = deviceURouterRestRpcService.urouterSetting(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<URouterModeVTO> rpcResult = deviceURouterRestRpcService.urouterLinkMode(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<Map<String,Object>> rpcResult = deviceURouterRestRpcService.urouterPlugins(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
	 * @param alias_on 昵称终端开关
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
			@RequestParam(required = false, defaultValue = "false") boolean alias_on,
			@RequestParam(required = false, defaultValue=UserTerminalOnlineSettingDTO.Default_Timeslot) String timeslot,
			@RequestParam(required = true) int timeslot_mode) {
		
		RpcResponseDTO<Boolean> rpcResult = deviceURouterRestRpcService.urouterUpdPluginTerminalOnline(
				uid, mac.toLowerCase(), on, stranger_on, alias_on, timeslot, timeslot_mode);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		RpcResponseDTO<Boolean> rpcResult = deviceURouterRestRpcService.urouterUpdPluginWifisniffer(uid, mac.toLowerCase(), on);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/upd_notify_reward",method={RequestMethod.POST})
	public void upd_notify_reward(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) boolean on) {
		
		RpcResponseDTO<Boolean> rpcResult = deviceURouterRestRpcService.urouterUpdNotifyReward(uid, on);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/device_query_used_status",method={RequestMethod.POST})
	public void device_query_used_status(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		RpcResponseDTO<DeviceUsedStatisticsDTO> rpcResult = deviceURouterRestRpcService.urouterDeviceUsedStatusQuery(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		
		RpcResponseDTO<URouterDeviceConfigVTO> rpcResult = deviceURouterRestRpcService.urouterConfigs(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 获取urouter设备的相关配置数据multi
	 * 支持双频
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/configs_multi",method={RequestMethod.POST})
	public void configs_multi(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
		RpcResponseDTO<URouterDeviceConfigMutilVTO> rpcResult = deviceURouterRestRpcService.urouterConfigsSupportMulti(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
			@RequestParam(required = true) String dmac,
			@RequestParam(required = true) String hmacs) {
		
		RpcResponseDTO<List<URouterHdHostNameVTO>> rpcResult = deviceURouterRestRpcService.terminalHostnames(uid,dmac.toLowerCase(), hmacs.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		RpcResponseDTO<URouterAdminPasswordVTO> rpcResult = deviceURouterRestRpcService.urouterAdminPassword(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		RpcResponseDTO<URouterVapPasswordVTO> rpcResult = deviceURouterRestRpcService.urouterVapPassword(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
    /**
     * 多条件组合搜索接口(APP专用的返回值)
     * @param uid
     * @param conditions
     * @param pageNo
     * @param pageSize
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_condition_message", method = {RequestMethod.POST})
    public void fetch_by_condition_message(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

        RpcResponseDTO<TailPage<UserDeviceDTO>> vtos = deviceURouterRestRpcService.urouterFetchBySearchConditionMessage(
        		uid, message, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
    }
    
}
