package com.bhu.vas.web.test;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ping")
public class PingController {
    @Resource
    private IDeviceRestRpcService deviceRestRpcService;
    
	@ResponseBody()
	@RequestMapping(value="/v1",method={RequestMethod.GET,RequestMethod.POST})
	public void v1(
			HttpServletRequest request,
			HttpServletResponse response/*,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String uids*/) {

		try{
			//String[] uidarray = StringHelper.split(uids, StringHelper.COMMA_STRING_GAP);
			//List<Object> ret = UserPlayDurationService.getInstance().hget_pipeline_playDurations(ArrayHelper.toList(uidarray));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed("ping ok.."));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 获取设备的状态信息
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/dp",method={RequestMethod.GET,RequestMethod.POST})
	public void dp(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String mac) {
		try{
			RpcResponseDTO<String> rpcResponse = deviceRestRpcService.fetchDevicePresent(mac.toLowerCase());
			if(rpcResponse.getErrorCode() == null){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
