package com.bhu.vas.web.console;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.unifyStatistics.iservice.IUnifyStatisticsRpcService;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.statistics.OnlineStatisticsVTO;
import com.bhu.vas.api.vto.statistics.StateStatisticsVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console")
public class ConsoleStatisticsController {
	@Resource
    private IUnifyStatisticsRpcService unifyStatisticsRpcService;
	
	@ResponseBody()
	@RequestMapping(value = "/statistics/fetch_online_data", method = {RequestMethod.POST})
	public void fetch_online_data(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            @RequestParam(required = true) String queryParam){
			OnlineStatisticsVTO vto = unifyStatisticsRpcService.onlineStatistics(queryParam);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
	
	@ResponseBody()
	@RequestMapping(value = "/statistics/fetch_statestat", method = {RequestMethod.POST})
	public void fetch_statestat(
	            HttpServletRequest request,
	            HttpServletResponse response){
		StateStatisticsVTO vto = unifyStatisticsRpcService.stateStat();
	    SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
}
