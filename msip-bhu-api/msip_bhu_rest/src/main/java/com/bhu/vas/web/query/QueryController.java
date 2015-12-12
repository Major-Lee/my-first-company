package com.bhu.vas.web.query;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/query")
public class QueryController extends BaseController {

    @Resource
    private IUserDeviceRpcService userDeviceRpcService;

	@ResponseBody()
    @RequestMapping(value="/fetch_device_bind_user",method={RequestMethod.GET,RequestMethod.POST})
    public void fetch_device_bind_user(HttpServletResponse response,
    		@RequestParam(required = false) String jsonpcallback,
            @RequestParam(required = true) String mac) {

		mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        RpcResponseDTO<UserDTO> rpcResult = userDeviceRpcService.fetchBindDeviceUser(mac);
        if(!rpcResult.hasError()) {
            if(StringUtils.isEmpty(jsonpcallback))
                SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
            else
                SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
        	if(StringUtils.isEmpty(jsonpcallback))
                SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
            else
                SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(rpcResult));
            /*if(StringUtils.isEmpty(jsonpcallback))
                SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_NOT_BINDED));
            else
                SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(ResponseErrorCode.DEVICE_NOT_BINDED));*/
        }
    }
}
