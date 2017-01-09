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

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/ad")
public class ConsoleAdvertiseController extends BaseController{
	@Resource
	private IAdvertiseRpcService advertiseRpcService;
	
	
	@ResponseBody()
	@RequestMapping(value = "/verifyAdvertise", method = {RequestMethod.POST})
	public void updateAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String advertiseId,
			@RequestParam(required = false) String msg,
			@RequestParam(required = true) int state
			) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.verifyAdvertise
					(uid,advertiseId, msg,state);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value = "/queryAdvertiseList", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String province,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String district,
			@RequestParam(required = false) String publishStartTime,
			@RequestParam(required = false) String publishEndTime,
			@RequestParam(required = false) Integer type,
			@RequestParam(required = false) String  createStartTime,
			@RequestParam(required = false) String createEndTime,
			@RequestParam(required = false) String mobileNo,
			@RequestParam(required = false,defaultValue="-1") int state,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		try{
			RpcResponseDTO<AdvertiseListVTO> rpcResult = advertiseRpcService.queryAdvertiseList
					(null, province, city,district, publishStartTime, publishEndTime, type,createStartTime,createEndTime,mobileNo,state,pageNo,pageSize,true);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
	}
	@ResponseBody()
	@RequestMapping(value = "/queryAdvertiseInfo", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String advertiseId
			) {
		try{
			RpcResponseDTO<AdvertiseVTO> rpcResult = advertiseRpcService.queryAdvertise
					(null,advertiseId);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
		
	}
	
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_condition_message", method = {RequestMethod.POST})
    public void fetch_by_condition_message(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

        RpcResponseDTO<List<TailPage<AdvertiseVTO>>> rpcResult = advertiseRpcService.fetchBySearchConditionMessages(
        		pageNo, pageSize, message);
		if(!rpcResult.hasError()){
			//兼容老的界面和接口
			List<TailPage<AdvertiseVTO>> rpcResultPayload = rpcResult.getPayload();
			if(rpcResultPayload != null && !rpcResultPayload.isEmpty()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.
						builderSuccessRpcResponse(rpcResultPayload.get(0))));
			}else{
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
			}
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
    }
}
