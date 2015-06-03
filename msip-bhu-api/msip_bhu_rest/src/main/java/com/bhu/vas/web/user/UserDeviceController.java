package com.bhu.vas.web.user;

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
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * 用户和设备直接操作,绑定，解绑。
 */
@Controller
@RequestMapping("/user/device")
public class UserDeviceController extends BaseController {

    @Resource
    private IUserDeviceRpcService userDeviceRpcService;

    @ResponseBody()
    @RequestMapping(value="/bind",method={RequestMethod.POST})
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


    /**
     * 设备不在线可以解绑
     * @param response
     * @param mac
     * @param uid
     */
    @ResponseBody()
    @RequestMapping(value="/unbind",method={RequestMethod.POST})
    public void unBindDevice(HttpServletResponse response,
                             @RequestParam(required = true, value = "mac") String mac,
                             @RequestParam(required = true, value = "uid") int uid
    ) {
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        int deviceStatus = userDeviceRpcService.validateDeviceStatusIsOnlineAndBinded(mac);
        logger.debug("devicestatus==" + deviceStatus);
        if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_EXIST ) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_DATA_NOT_EXIST));
        } else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_NOT_UROOTER));
        }  else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_BINDED
                || deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
            RpcResponseDTO<Boolean> userDeviceResult = userDeviceRpcService.unBindDevice(mac, uid);
            if (userDeviceResult.getPayload()) {
                SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
            } else {
                SpringMVCHelper.renderJson(response, ResponseError.embed(userDeviceResult.getErrorCode()));
            }

        } else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_UNBINDED) {
            //TODO(bluesand):未绑定过装备的时候，取消绑定
            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
        }

    }

    @ResponseBody()
    @RequestMapping(value="/validate",method={RequestMethod.POST})
    public void validateDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "mac") String mac) {
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        SpringMVCHelper.renderJson(response,ResponseSuccess.embed(userDeviceRpcService.validateDeviceStatus(mac).getPayload()));
    }

    @ResponseBody()
    @RequestMapping(value="/fetchbinded",method={RequestMethod.POST})
    public void listBindDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid) {
        RpcResponseDTO<List<UserDeviceDTO>> userDeviceResult = userDeviceRpcService.fetchBindDevices(uid);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
    }


    @ResponseBody()
    @RequestMapping(value="/modify/device_name",method={RequestMethod.POST})
    public void modifyDeviceName(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam(required = true) Integer uid,
                                 @RequestParam(required = true) String mac,
                                 @RequestParam(required = true, value = "device_name") String deviceName) {

        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }

        if (userDeviceRpcService.modifyDeviceName(mac, uid, deviceName)) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }





    }

}
