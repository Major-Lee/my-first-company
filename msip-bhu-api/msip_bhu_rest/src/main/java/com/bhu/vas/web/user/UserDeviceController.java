package com.bhu.vas.web.user;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户和设备直接操作,绑定，解绑。
 */
@Controller
@RequestMapping("/user/device")
public class UserDeviceController extends BaseController {

    @Resource
    private IUserDeviceRpcService userDeviceRpcService;

    @ResponseBody()
    @RequestMapping(value="/bind",method={RequestMethod.GET,RequestMethod.POST})
    public void bindDevice(HttpServletResponse response,
                           @RequestParam(required = true, value = "mac") String mac,
                           @RequestParam(required = true, value = "uid") int uid,
                           @RequestParam(required = true, value = "device_name") String deviceName) {
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        RpcResponseDTO<UserDeviceDTO> userDeviceResult = userDeviceRpcService.bindDevice(mac, uid, deviceName);
        if (userDeviceResult.getErrorCode() != null) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(userDeviceResult.getErrorCode()));
            return;
        } else {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
        }




    }

    @ResponseBody()
    @RequestMapping(value="/unbind",method={RequestMethod.GET,RequestMethod.POST})
    public void unBindDevice(HttpServletResponse response,
                             @RequestParam(required = true, value = "mac") String mac,
                             @RequestParam(required = true, value = "uid") int uid
    ) {
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        int deviceStatus = userDeviceRpcService.validateDeviceStatusIsOnlineAndBinded(mac);
        ResponseErrorCode responseErrorCode = null;
        if (deviceStatus < 4) {
            if (deviceStatus == 0) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_EXIST;
            } else if (deviceStatus == 1) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_ONLINE;
            } else if (deviceStatus == 3) {
                responseErrorCode = ResponseErrorCode.DEVICE_ALREADY_BEBINDED;
            }
            SpringMVCHelper.renderJson(response, ResponseError.embed(responseErrorCode));
            return;
        } else {
            RpcResponseDTO<Boolean> userDeviceResult = userDeviceRpcService.unBindDevice(mac, uid);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
        }

    }

    @ResponseBody()
    @RequestMapping(value="/validate",method={RequestMethod.GET,RequestMethod.POST})
    public void validateDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "mac") String mac) {
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        int deviceStatus = userDeviceRpcService.validateDeviceStatusIsOnlineAndBinded(mac);
        ResponseErrorCode responseErrorCode = null;
        if (deviceStatus < 4) {
            if (deviceStatus == 0) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_EXIST;
            } else if (deviceStatus == 1) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_ONLINE;
            } else if (deviceStatus == 3) {
                responseErrorCode = ResponseErrorCode.DEVICE_ALREADY_BEBINDED;
            }
            SpringMVCHelper.renderJson(response, ResponseError.embed(responseErrorCode));
            return;
        } else {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
        }
    }

    @ResponseBody()
    @RequestMapping(value="/fetchbinded",method={RequestMethod.GET,RequestMethod.POST})
    public void listBindDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid) {
        RpcResponseDTO<List<UserDeviceDTO>> userDeviceResult = userDeviceRpcService.fetchBindDevices(uid);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
    }
}
