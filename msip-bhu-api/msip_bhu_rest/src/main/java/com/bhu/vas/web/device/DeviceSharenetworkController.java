package com.bhu.vas.web.device;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device/sharenetwork")
public class DeviceSharenetworkController extends BaseController{
	//@Resource
	//private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	@ResponseBody()
	@RequestMapping(value="/supported",method={RequestMethod.POST})
	public void supported(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(SharedNetworkType.getSharedNetworkVtos()));
	}
	
	/**
	 * 修改用户共享网络配置并应用接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/apply",method={RequestMethod.POST})
	public void apply(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snt") String sharenetwork_type,
			@RequestParam(required = false) String extparams) {
		/*RpcResponseDTO<URouterMainEnterVTO> rpcResult = deviceURouterRestRpcService.urouterMainEnter(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
	}
	
	/**
	 * 获取用户所属指定共享网络配置的设备分页列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 */
	@ResponseBody()
	@RequestMapping(value="/pagedevices",method={RequestMethod.POST})
	public void pagedevices(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type) {
		
		/*RpcResponseDTO<URouterEnterVTO> rpcResult = deviceURouterRestRpcService.urouterEnter(uid, mac.toLowerCase());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}*/
	}
	
}