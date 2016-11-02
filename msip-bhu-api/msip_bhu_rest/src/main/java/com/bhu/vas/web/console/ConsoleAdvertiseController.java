package com.bhu.vas.web.console;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console")
public class ConsoleAdvertiseController extends BaseController{
	@Resource
	private IAdvertiseRpcService advertiseRpcService;
	
	
	@ResponseBody()
	@RequestMapping(value = "/ad/verifyAdvertise", method = {RequestMethod.POST})
	public void updateAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) int advertiseId,
			@RequestParam(required = true) String msg,
			@RequestParam(required = true) int state
			) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.verifyAdvertise
					(uid,advertiseId, msg,state);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value = "/ad/queryAdvertiseList", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String province,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String district,
			@RequestParam(required = false) String publishStartTime,
			@RequestParam(required = false) String publishEndTime,
			@RequestParam(required = false,defaultValue="0") int type,
			@RequestParam(required = false) String  createStartTime,
			@RequestParam(required = false) String createEndTime,
			@RequestParam(required = false) String userName,
			@RequestParam(required = false,defaultValue="-1") int state,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		try{
			RpcResponseDTO<AdvertiseListVTO> rpcResult = advertiseRpcService.queryAdvertiseList
					(null, province, city,district, publishStartTime, publishEndTime, type,createStartTime,createEndTime,userName,state,pageNo,pageSize);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
