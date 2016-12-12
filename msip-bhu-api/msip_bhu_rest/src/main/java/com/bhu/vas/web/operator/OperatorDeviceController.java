package com.bhu.vas.web.operator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.OpsBatchImportVTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ops/device")
public class OperatorDeviceController extends BaseController{

	private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";

	
    @Resource
    private IChargingRpcService chargingRpcService;

	
	private ResponseError validateSecretKey(String secretKey){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID);
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
    public void bindDevice(HttpServletResponse response,
    					@RequestParam(required = true,value="sk") String secretKey,
    		            @RequestParam(required = false,value = "cc",defaultValue="86") int countrycode,
    		            @RequestParam(required = true,value = "mobileno") String mobileno,
                           @RequestParam(required = true, value = "macs") String macs,
                           @RequestParam(required = true, value = "uid") int uid) throws Exception{
		ResponseError validateError = validateSecretKey(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> ret = chargingRpcService.bindDevice(uid, macs, countrycode, mobileno);
        if (!ret.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(ret));
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
    public void unBindDevice(HttpServletResponse response,
    						@RequestParam(required = true,value="sk") String secretKey,
                             @RequestParam(required = true, value = "macs") String macs,
                             @RequestParam(required = true, value = "uid") int uid
    ) {
		ResponseError validateError = validateSecretKey(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> ret = chargingRpcService.unbindDevice(uid, macs);
        if (!ret.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(ret));
        }
    }
}
