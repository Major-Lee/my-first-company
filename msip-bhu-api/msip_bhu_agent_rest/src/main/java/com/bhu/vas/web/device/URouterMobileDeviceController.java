package com.bhu.vas.web.device;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/urouter/mobile/device")
public class URouterMobileDeviceController extends BaseController{
	
	@Resource
	private IDeviceURouterRestRpcService deviceURouterRestRpcService;
	
	/**
	 * 注册用户目前登录的设备及设备token
	 * @param response
	 * @param request
	 * @param uid
	 * @param dt
	 * @param dtk
	 */
	@ResponseBody()
	@RequestMapping(value="/register",method={RequestMethod.POST})
	public void register(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) Integer uid,
			//device type
			@RequestParam(required = false, value="d",defaultValue="R") String device,
			//device token 对于ios设备 用于iospush使用 由于客户端设备获取dt需要用户授权，可能获取不到
			@RequestParam(required = true) String dt,
			//device mac 地址 mac地址也不一定能获取到
			@RequestParam(required = false) String dm,
			//client 系统版本号
			@RequestParam(required = true) String cv,
			//client production 版本号
			@RequestParam(required = true) String pv,
			//设备型号unit type
			@RequestParam(required = true) String ut,
			//push type 默认为商店版
			@RequestParam(required = false, defaultValue="O") String pt
//			@RequestParam(required = false,defaultValue = "") String dn,
//			@RequestParam(required = false,defaultValue = "") String duuid
			){
		RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterUserMobileDeviceRegister(uid, device,
				dt, dm, cv, pv, ut, pt);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
	/**
	 * 注销用户目前登录的设备及设备token
	 * @param response
	 * @param request
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/destory",method={RequestMethod.POST})
	public void destory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) int uid,
			//device type
			@RequestParam(required = false, value="d",defaultValue="R") String device,
			@RequestParam(required = true) String dt){
		
		RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterUserMobileDeviceDestory(uid, 
				device, dt);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
}
