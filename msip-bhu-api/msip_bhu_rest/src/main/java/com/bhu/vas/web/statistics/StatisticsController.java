package com.bhu.vas.web.statistics;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.statistics.iservice.IStatisticsRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.msip.exception.BusinessException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseStatus;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/statistics")
public class StatisticsController extends BaseController{
	@Resource
	private IStatisticsRpcService statisticsRpcService;
	/**
	 * 获取最繁忙的TOP5wifi设备
	 * @param request
	 * @param response
	 * @param t 年-0 季度-1 月-2 周-3 日-4
	 * @param ml 代表显示几条曲线的数据 1-本日（周、月、季度、年） 2-本日、前一天（周、月、季度、年） 3-本日、前一天、前两天（周、月、季度、年）,依次类推
	 * 	public static final int YEAR = 0;
		public static final int YEAR_QUARTER = 1;
		public static final int YEAR_MONTH = 2;
		public static final int YEAR_WHICH_WEEK = 3;
		public static final int YEAR_MONTH_DD = 4;
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_online_handset",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_online_handset(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true,defaultValue="4",value = "t") int type,
			@RequestParam(required = false,defaultValue="1",value = "ml") int ml
			//@RequestParam(required = false) String fragment
			) {
		if(type<0 || type>4) {
			throw new BusinessException(ResponseStatus.Forbidden,ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		if(ml<1 ) ml = 1;
		if(ml>5 ) ml = 5;
		
		RpcResponseDTO<Map<String, Object>> rpcResult = statisticsRpcService.buildHandsetOnline4Chart(type, ml);
		if(rpcResult.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}
		//Map<String,List<String>> result = build4Chart(type,ml);
		//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(build4Chart(type,ml)));
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/fetch_online_device",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_online_device(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true,defaultValue="4",value = "t") int type,
			@RequestParam(required = false,defaultValue="1",value = "ml") int ml
			//@RequestParam(required = false) String fragment
			) {
		if(type<0 || type>4) {
			throw new BusinessException(ResponseStatus.Forbidden,ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		if(ml<1 ) ml = 1;
		if(ml>5 ) ml = 5;
		
		RpcResponseDTO<Map<String, Object>> rpcResult = statisticsRpcService.buildDeviceOnline4Chart(type, ml);
		if(rpcResult.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}
		//Map<String,List<String>> result = build4Chart(type,ml);
		//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(build4Chart(type,ml)));
	}
}
