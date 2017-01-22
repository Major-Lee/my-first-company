package com.bhu.vas.web.gome;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeConfigDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceDTO;
import com.bhu.vas.api.rpc.thirdparty.iservice.IThirdPartyRpcService;
import com.bhu.vas.thirdparty.response.GomeResponse;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
@Controller
@RequestMapping("/")
public class GomeController extends BaseController{
	@Resource
	private IThirdPartyRpcService thirdPartyRpcService;


	/**
	 * 绑定设备
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/device/bind",method={RequestMethod.POST})
	public void bindDevice(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String requestBody) {
		try{
			ValidateService.ValidateGomeRequest(request, requestBody, response);
			Map<String, Object> params = JsonHelper.getMapFromJson(requestBody);
			String deviceId = (String)params.get("deviceId");
			String mac = CryptoHelper.aesDecryptFromHex(deviceId, BusinessRuntimeConfiguration.GomeToBhuDataKey);
			RpcResponseDTO<Boolean> rpcResult = thirdPartyRpcService.gomeBindDevice(mac);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, GomeResponse.fromSuccessRpcResponse(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, GomeResponse.fromFailRpcResponse(rpcResult));
			}
		}catch(BusinessI18nCodeException be){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(be.getErrorCode()));
		}catch(Exception e){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_500_ERROR));
		}
	}

	/**
	 * 绑定设备
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/device/unbind",method={RequestMethod.POST})
	public void unbindDevice(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String requestBody) {
		try{
			ValidateService.ValidateGomeRequest(request, requestBody, response);
			Map<String, Object> params = JsonHelper.getMapFromJson(requestBody);
			String deviceId = (String)params.get("deviceId");
			String mac = CryptoHelper.aesDecryptFromHex(deviceId, BusinessRuntimeConfiguration.GomeToBhuDataKey);
			RpcResponseDTO<Boolean> rpcResult = thirdPartyRpcService.gomeUnbindDevice(mac);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, GomeResponse.fromSuccessRpcResponse(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, GomeResponse.fromFailRpcResponse(rpcResult));
			}
		}catch(BusinessI18nCodeException e){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(e.getErrorCode()));
		}catch(Exception e){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_500_ERROR));
		}
	}


	/**
	 * 下发配置
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/device/status/control",method={RequestMethod.POST})
	public void deviceControl(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String requestBody) {
		try{
			ValidateService.ValidateGomeRequest(request, requestBody, response);
			Map<String, Object> params = JsonHelper.getMapFromJson(requestBody);
			String deviceId = (String)params.get("deviceId");
			String mac = CryptoHelper.aesDecryptFromHex(deviceId, BusinessRuntimeConfiguration.GomeToBhuDataKey);
			GomeConfigDTO dto = JsonHelper.jsonToObject(requestBody, "command", GomeConfigDTO.class);
			RpcResponseDTO<Boolean> rpcResult = thirdPartyRpcService.gomeDeviceControl(mac, dto);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, GomeResponse.fromSuccessRpcResponse(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, GomeResponse.fromFailRpcResponse(rpcResult));
			}
		}catch(BusinessI18nCodeException e){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(e.getErrorCode()));
		}catch(Exception e){
			e.printStackTrace();
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_500_ERROR));
		}
	}

	/**
	 * 设备在线状态查询接口
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/device/online/get",method={RequestMethod.POST})
	public void deviceOnlineGet(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String requestBody) {
		try{
			ValidateService.ValidateGomeRequest(request, requestBody, response);
			Map<String, Object> params = JsonHelper.getMapFromJson(requestBody);
			String deviceId = (String)params.get("deviceId");
			String mac = CryptoHelper.aesDecryptFromHex(deviceId, BusinessRuntimeConfiguration.GomeToBhuDataKey);
			RpcResponseDTO<GomeDeviceDTO> rpcResult = thirdPartyRpcService.gomeDeviceOnlineGet(mac);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, GomeResponse.fromSuccessRpcResponse(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, GomeResponse.fromFailRpcResponse(rpcResult));
			}
		}catch(BusinessI18nCodeException e){
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(e.getErrorCode()));
		}catch(Exception e){
			e.printStackTrace();
			SpringMVCHelper.renderJson(response, GomeResponse.fromFailErrorCode(ResponseErrorCode.REQUEST_500_ERROR));
		}
	}
}
