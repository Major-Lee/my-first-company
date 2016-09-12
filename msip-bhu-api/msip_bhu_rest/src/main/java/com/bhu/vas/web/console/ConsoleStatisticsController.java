package com.bhu.vas.web.console;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.unifyStatistics.iservice.IUnifyStatisticsRpcService;
import com.bhu.vas.api.rpc.unifyStatistics.vto.OnlineStatisticsVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.SsidStatisticsOutLineVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.StateStatisticsVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/statistics")
public class ConsoleStatisticsController extends BaseController{
	@Resource
    private IUnifyStatisticsRpcService unifyStatisticsRpcService;
	
	@ResponseBody()
	@RequestMapping(value = "/fetch_online_data", method = {RequestMethod.POST})
	public void fetch_online_data(
            HttpServletRequest request,
            HttpServletResponse response,
	            @RequestParam(required = true) String uid,
	            @RequestParam(required = true) String category,
	            @RequestParam(required = true) String queryParam){
		RpcResponseDTO<OnlineStatisticsVTO> rpcResult = unifyStatisticsRpcService.onlineStatistics(category,queryParam);
			
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value = "/fetch_statestat", method = {RequestMethod.POST})
	public void fetch_statestat(
            HttpServletRequest request,
            HttpServletResponse response,
	            @RequestParam(required = true) String uid){
		
		RpcResponseDTO<StateStatisticsVTO>  rpcResult = unifyStatisticsRpcService.stateStat();
		
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value = "/querySSIDStatisticInfo", method = {RequestMethod.POST})
	public void querySSIDStatisticInfo(
			HttpServletRequest request,
            HttpServletResponse response,
			@RequestParam(required = false,defaultValue = "7",value = "type") String type,
            @RequestParam(required = false) String deliveryChannel,
            @RequestParam(required = false) String deviceLabel,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String mac,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
			){
		//返回结果
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("type", type);
		map.put("deliveryChannel", deliveryChannel);
		map.put("deviceLabel", deviceLabel);
		map.put("mobileNo", mobileNo);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("mac", mac);
		map.put("pn", pageNo);
		map.put("ps", pageSize);
		result = unifyStatisticsRpcService.querySSIDStatisticsInfo(map);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
	}
	
	@ResponseBody()
	@RequestMapping(value = "/querySSIDStatisticOutLine", method = {RequestMethod.POST})
	public void querySSIDStatisticOutLine(
			HttpServletRequest request,
			HttpServletResponse response
			){
		//返回结果
		RpcResponseDTO<SsidStatisticsOutLineVTO> rpcResult = unifyStatisticsRpcService.sSIDStatisticsOutLineInfo();
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
