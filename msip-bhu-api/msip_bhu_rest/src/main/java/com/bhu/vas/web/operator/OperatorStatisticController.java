package com.bhu.vas.web.operator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.UcloudMacStatisticsVTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.api.vto.statistics.OpertorUserIncomeVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
@Controller
@RequestMapping("/ops/statistic")
public class OperatorStatisticController extends BaseController{
private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";
	

	@Resource
	private IUserWalletRpcService userWalletRpcService;
	
	private ResponseError validate(String secretKey){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		return null;
	}
	
	@ResponseBody()
    @RequestMapping(value = "/income", method = {RequestMethod.POST})
    public void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true,value="sk") String secretKey,
            @RequestParam(required = true) Integer uid
    		) {
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
    	try{
			RpcResponseDTO<OpertorUserIncomeVTO> rpcResult = userWalletRpcService.operatorMonIncome(uid);
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
	
	 	@ResponseBody()
	    @RequestMapping(value="/richStatistics", method={RequestMethod.GET,RequestMethod.POST})
	    public void richStatistics( HttpServletResponse response, 
	    		@RequestParam(required = true,value="sk") String secretKey,
	    		@RequestParam(required = true) Integer uid){
	 		ResponseError validateError = validate(secretKey);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
	    	try{
	    		RpcResponseDTO<UcloudMacStatisticsVTO> rpcResult = userWalletRpcService.richStatistics(uid);
	    		if(!rpcResult.hasError()){
	    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
	    		}else{
	    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	    		}
	    	}catch(Exception ex){
	    		SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
	    	}
	    }
}
