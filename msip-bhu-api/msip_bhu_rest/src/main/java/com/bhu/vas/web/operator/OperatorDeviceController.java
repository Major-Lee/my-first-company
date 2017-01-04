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
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ops/device")
public class OperatorDeviceController extends BaseController{

	private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";

	
    @Resource
    private IChargingRpcService chargingRpcService;

	
	private ResponseError validateSecretKey(String secretKey, HttpServletRequest request){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID, BusinessWebHelper.getLocale(request));
		}
		return null;
	}

    /**
     * 用户绑定设备
     * @param response
     * @param mac
     * @param uid
     * @throws Exception
     */
    @ResponseBody()
    @RequestMapping(value="/bind",method={RequestMethod.POST})
    public void bindDevice(HttpServletRequest request, HttpServletResponse response,
    					@RequestParam(required = true,value="sk") String secretKey,
    		            @RequestParam(required = false,value = "cc",defaultValue="86") int countrycode,
    		            @RequestParam(required = true,value = "mobileno") String mobileno,
                           @RequestParam(required = true, value = "macs") String macs,
                           @RequestParam(required = true, value = "uid") int uid) throws Exception{
		ResponseError validateError = validateSecretKey(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> ret = chargingRpcService.bindDevice(uid, macs, countrycode, mobileno);
        if (!ret.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(ret, BusinessWebHelper.getLocale(request)));
        }
    }

    /**
     * 设备不在线可以解绑
     * @param response
     * @param mac
     * @param uid
     */
    @ResponseBody()
    @RequestMapping(value="/unbind",method={RequestMethod.POST})
    public void unBindDevice(HttpServletRequest request, HttpServletResponse response,
    						@RequestParam(required = true,value="sk") String secretKey,
                             @RequestParam(required = true, value = "macs") String macs,
                             @RequestParam(required = true, value = "uid") int uid
    ) {
		ResponseError validateError = validateSecretKey(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> ret = chargingRpcService.unbindDevice(uid, macs);
        if (!ret.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(ret, BusinessWebHelper.getLocale(request)));
        }
    }
}
