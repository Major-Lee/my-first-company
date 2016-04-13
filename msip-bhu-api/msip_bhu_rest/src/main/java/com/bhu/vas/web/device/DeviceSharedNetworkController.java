package com.bhu.vas.web.device;

import java.util.Arrays;
import java.util.List;

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
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceSharedNetworkRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
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
	 * @param template 如果为"0000"或者不存在的template，0000也是不存在的编号 则代表新建 参数为空则采用0001的缺省值
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/apply",method={RequestMethod.POST})
	public void apply(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type,
			@RequestParam(required = false,defaultValue= "0001",value="tpl") String template,
			@RequestParam(required = false) String extparams) {
		RpcResponseDTO<ParamSharedNetworkDTO> rpcResult = deviceSharedNetworkRpcService.applyNetworkConf(uid, sharenetwork_type,template, extparams);
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
	@RequestMapping(value="/fetch_user",method={RequestMethod.POST})
	public void fetch_user(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type,
			@RequestParam(required = false,defaultValue= "0001",value="tpl") String template) {
		RpcResponseDTO<ParamSharedNetworkDTO> rpcResult = deviceSharedNetworkRpcService.fetchUserNetworkConf(uid, sharenetwork_type,template);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/fetch_users",method={RequestMethod.POST})
	public void fetch_users(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type) {
		RpcResponseDTO<List<ParamSharedNetworkDTO>> rpcResult = deviceSharedNetworkRpcService.fetchUserNetworksConf(uid, sharenetwork_type);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 获取指定设备的共享网络配置并应用接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_device",method={RequestMethod.POST})
	public void fetch_device(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac) {
		RpcResponseDTO<SharedNetworkSettingDTO> rpcResult = deviceSharedNetworkRpcService.fetchDeviceNetworkConf(uid, mac);
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
			@RequestParam(required = false,defaultValue= "0001",value="tpl") String template,
			@RequestParam(required = false,defaultValue = "true") boolean on,
			@RequestParam(required = true) String macs) {
		String[] mac_array = macs.toLowerCase().split(StringHelper.COMMA_STRING_GAP);
		
		RpcResponseDTO<Boolean> rpcResult = deviceSharedNetworkRpcService.takeEffectNetworkConf(uid,on, sharenetwork_type,template, Arrays.asList(mac_array));
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
	 * @param template null 没有此条件 "" 搜不存在的 其他则是搜索指定匹配数据
	 * @param uid
	 * @param sharenetwork_type
	 */
	@ResponseBody()
	@RequestMapping(value="/pages",method={RequestMethod.POST})
	public void device_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, defaultValue= "SafeSecure",value="snk_type") String sharedNetwork_type,
			@RequestParam(required = false, value="tpl") String template,
			@RequestParam(required = false) String dut,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> rpcResult = deviceSharedNetworkRpcService.pages(uid, 
				sharedNetwork_type, template, dut, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}