package com.bhu.vas.web.device;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device/snk")
public class DeviceSharedealController extends BaseController{
	
    @Resource
    private IChargingRpcService chargingRpcService;
    
	/**
     * 
     * @param request
     * @param response
     * @param uid
     * @param message
     * @param canbeturnoff
     * @param enterpriselevel
     * @param customized false 使用默认值 true 使用定制的值
     * @param owner_percent
     * @param range_cash_mobile
     * @param range_cash_pc
     * @param access_internet_time
     */
    @ResponseBody()
    @RequestMapping(value = "/sharedeal/batch/modify", method = {RequestMethod.POST})
    public void deviceBatch_sharedeal_modify(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            /*@RequestParam(required = false,value = "percent") String owner_percent,
            @RequestParam(required = false,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = false,value = "percent_d") String distributor_percent,*/
            @RequestParam(required = true,value = "rcm") String range_cash_mobile,
            @RequestParam(required = true,value = "rcp") String range_cash_pc,
            @RequestParam(required = true,value = "ait") String access_internet_time,
            @RequestParam(required = false,value = "fait") String free_access_internet_time
            ) {
    	try{
    		ValidateService.validAmountRange(range_cash_mobile,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
    		ValidateService.validAmountRange(range_cash_pc,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
    		ValidateService.validAitRange(access_internet_time,NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
    		if(!StringUtils.isEmpty(free_access_internet_time))
    			ValidateService.validFreeAitRange(free_access_internet_time,NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
        	RpcResponseDTO<Boolean> rpcResult = chargingRpcService.doBatchSharedealModify(uid, message, 
        			null,null,
        			true,
        			null,null,null,
        			range_cash_mobile,range_cash_pc,access_internet_time, StringUtils.isEmpty(free_access_internet_time)?access_internet_time:free_access_internet_time, true);
    		if(!rpcResult.hasError()){
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		}else{
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    		}
    	}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }  
    
    @ResponseBody()
    @RequestMapping(value = "/sharedeal/detail", method = {RequestMethod.POST})
    public void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String mac
    		) {
    	try{
			RpcResponseDTO<DeviceSharedealVTO> rpcResult = chargingRpcService.sharedealDetail(uid, mac.toLowerCase());
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	    }catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
}