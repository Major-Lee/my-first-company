package com.bhu.vas.web.device;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterWSCommunityVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * 周边探测rest
 * @author tangzichao
 *
 */
@Controller
@RequestMapping("/urouter/device/ws")
public class URouterWifiStasnifferController extends BaseController{
	
	@Resource
	private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	/**
	 * 周边探测的最近出现列表
	 * 最近12小时内出现的终端
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac 设备mac
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value="/recent",method={RequestMethod.POST})
	public void recent(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
/*		
 * 		RpcResponseDTO<Map<String, Object>> rpcResult = deviceURouterRestRpcService.urouterWSRecent(uid, 
				mac.toLowerCase(), start, size);
		
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
		*/
		Map<String, Object> payload = PageHelper.partialAllList(Collections.emptyList(), 0, start, size);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payload));
	}
	
	
	/**
	 * 隔壁老王列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value="/neighbour",method={RequestMethod.POST})
	public void neighbour(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
/*		RpcResponseDTO<Map<String, Object>> rpcResult = deviceURouterRestRpcService.urouterWSNeighbour(uid, mac.toLowerCase(), 
				start, size);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
		Map<String, Object> payload = PageHelper.partialAllList(Collections.emptyList(), 0, start, size);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payload));
	}
	
	/**
	 * 用户关注或者取消关注探测的终端
	 * @param request
	 * @param response
	 * @param uid
	 * @param hd_mac
	 */
	@ResponseBody()
	@RequestMapping(value="/focus",method={RequestMethod.POST})
	public void focus(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String hd_mac,
			@RequestParam(required = false, defaultValue="true") boolean focus) {
		
/*		RpcResponseDTO<Boolean> rpcResult = deviceURouterRestRpcService.urouterWSFocus(uid, hd_mac.toLowerCase(), focus);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(true));
	}
	
	/**
	 * 用户修改探测终端的昵称
	 * @param request
	 * @param response
	 * @param uid
	 * @param hd_mac
	 * @param nick
	 */
	@ResponseBody()
	@RequestMapping(value="/nick",method={RequestMethod.POST})
	public void nick(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String hd_mac,
			@RequestParam(required = false) String nick) {
		
/*		RpcResponseDTO<Boolean> rpcResult = deviceURouterRestRpcService.urouterWSNick(uid, hd_mac.toLowerCase(), nick);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(true));
	}
	
	/**
	 * 获取终端的探测细节流水
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 * @param hd_mac
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value="/details",method={RequestMethod.POST})
	public void details(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String hd_mac,
			@RequestParam(required = false, defaultValue="0", value = "st") int start,
			@RequestParam(required = false, defaultValue="5", value = "ps") int size) {
		
/*		RpcResponseDTO<Map<String,Object>> rpcResult = deviceURouterRestRpcService.urouterWSDetails(uid, mac.toLowerCase(), 
				hd_mac.toLowerCase(), start, size);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
		Map<String, Object> payload = PageHelper.partialAllList(Collections.emptyList(), 0, start, size);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payload));
	}
	
	/**
	 * 获取设备的周边社区数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/community",method={RequestMethod.POST})
	public void urouterWSCommunity(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		
/*		RpcResponseDTO<URouterWSCommunityVTO> rpcResult = deviceURouterRestRpcService.urouterWSCommunity(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
    	URouterWSCommunityVTO vto = new URouterWSCommunityVTO();
    	vto.setTotal(0);
    	vto.setCt("温拿社区");
    	vto.setHdts(null);
    	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
}
