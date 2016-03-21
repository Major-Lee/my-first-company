package com.bhu.vas.web.device;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceSharedNetworkRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device/sharednetwork")
public class DeviceSharedNetworkController extends BaseController{
	@Resource
	private IDeviceSharedNetworkRpcService deviceSharedNetworkRpcService;
	
	@ResponseBody()
	@RequestMapping(value="/supported",method={RequestMethod.POST})
	public void supported(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid) {
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
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type,
			@RequestParam(required = false) String extparams) {
		RpcResponseDTO<ParamSharedNetworkDTO> rpcResult = deviceSharedNetworkRpcService.applyNetworkConf(uid, sharenetwork_type, extparams);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 获取用户共享网络配置并应用接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch",method={RequestMethod.POST})
	public void fetch(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type) {
		RpcResponseDTO<ParamSharedNetworkDTO> rpcResult = deviceSharedNetworkRpcService.fetchNetworkConf(uid, sharenetwork_type);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
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
	@RequestMapping(value="/takeeffect",method={RequestMethod.POST})	
	public void takeeffect(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type,
			@RequestParam(required = true) String macs) {
		String[] mac_array = macs.toLowerCase().split(StringHelper.COMMA_STRING_GAP);
		
		RpcResponseDTO<Boolean> rpcResult = deviceSharedNetworkRpcService.takeEffectNetworkConf(uid, sharenetwork_type, Arrays.asList(mac_array));
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 获取用户所属指定共享网络配置的设备分页列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 */
	@ResponseBody()
	@RequestMapping(value="/device/pages",method={RequestMethod.POST})
	public void device_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharedNetwork_type,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> rpcResult = deviceSharedNetworkRpcService.pages(uid, 
				sharedNetwork_type, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
}