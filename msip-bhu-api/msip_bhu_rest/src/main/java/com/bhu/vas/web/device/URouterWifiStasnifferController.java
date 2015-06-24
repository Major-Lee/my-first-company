package com.bhu.vas.web.device;

import java.util.List;

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
import com.bhu.vas.api.vto.URouterWSRecentVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
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
		
		RpcResponseDTO<List<URouterWSRecentVTO>> rpcResponse = deviceURouterRestRpcService.urouterWSRecent(uid, 
				mac, start, size);
		if(rpcResponse.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
		}
	}
	
}
