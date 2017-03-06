package com.bhu.vas.web.ssid;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.thirdparty.iservice.ISsidRpcService;
import com.bhu.vas.api.vto.SsidInfoVTO;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
@Controller
@RequestMapping("/ssid")
public class SsidController extends BaseController{
	@Resource
	private ISsidRpcService ssidRpcService;

	/**
	 * 上报信息
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/report",method={RequestMethod.POST})
	public void reportSsidInfo(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String bssid,
			@RequestParam(required = true) String ssid,
			@RequestParam(required = true) String capabilities,
			@RequestParam(required = true) String passwd,
			@RequestParam(required = false) Double latiude,
			@RequestParam(required = false) Double longitude) {
		RpcResponseDTO<Boolean> rpcResult = ssidRpcService.reportSsidInfo(bssid, ssid, capabilities, passwd, latiude, longitude);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	/**
	 * 查询信息
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/query",method={RequestMethod.POST})
	public void reportSsidInfo(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String bssid,
			@RequestParam(required = true) String ssid,
			@RequestParam(required = true) String capabilities){
		RpcResponseDTO<SsidInfoVTO> rpcResult = ssidRpcService.querySsidInfo(bssid, ssid, capabilities);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}

}
