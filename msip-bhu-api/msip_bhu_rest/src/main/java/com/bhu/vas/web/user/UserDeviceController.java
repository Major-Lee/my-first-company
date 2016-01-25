package com.bhu.vas.web.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.vto.device.UserDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
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
                           @RequestParam(required = true, value = "uid") int uid) throws Exception{
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return;
        }

        RpcResponseDTO<UserDeviceDTO> userDeviceResult = userDeviceRpcService.bindDevice(mac, uid);
        if (!userDeviceResult.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(userDeviceResult));
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
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        /*int deviceStatus = userDeviceRpcService.validateDeviceStatusIsOnlineAndBinded(mac);
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
        }*/
        RpcResponseDTO<Boolean> rpcResult = userDeviceRpcService.unBindDevice(mac, uid);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
        }
    }

    @ResponseBody()
    @RequestMapping(value="/validate",method={RequestMethod.POST})
    public void validateDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "mac") String mac) {
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return ;
        }
        RpcResponseDTO<UserDeviceStatusDTO> rpcResult = userDeviceRpcService.validateDeviceStatus(mac);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
        }
        //SpringMVCHelper.renderJson(response,ResponseSuccess.embed(userDeviceRpcService.validateDeviceStatus(mac).getPayload()));
    }

    @ResponseBody()
    @RequestMapping(value="/fetchbinded",method={RequestMethod.POST})
    public void listBindDevice(HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid) {
        RpcResponseDTO<List<UserDeviceDTO>> rpcResult = userDeviceRpcService.fetchBindDevices(uid);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
        }
    }


    @ResponseBody()
    @RequestMapping(value="/pagebinded",method={RequestMethod.POST})
    public void pageBindedDevice(HttpServletResponse response,
                               @RequestParam(required = true) int uid,
                                 @RequestParam(required = true) String  dut,
                                 @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                                 @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize

                                 ) {
        TailPage<UserDeviceVTO> rpcResult = userDeviceRpcService.pageBindDevices(uid, dut, pageNo, pageSize);
        System.out.println("ret===" + rpcResult.isEmpty());
        if (!rpcResult.isEmpty()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }




    @ResponseBody()
    @RequestMapping(value="/modify/device_name",method={RequestMethod.POST})
    public void modifyDeviceName(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam(required = true) Integer uid,
                                 @RequestParam(required = true) String mac,
                                 @RequestParam(required = true, value = "device_name") String deviceName) throws Exception{
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return;
        }

        if (!validateDeviceName(deviceName)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL));
            return;
        }

        if (userDeviceRpcService.modifyDeviceName(mac, uid, deviceName)) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }

    
    @ResponseBody()
    @RequestMapping(value="/check_upgrade",method={RequestMethod.POST})
    public void check_upgrade(HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid,
                               @RequestParam(required = true) String mac,
                               @RequestParam(required = true) String appver
                               ) {
    	mac = mac.toLowerCase();
    	if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return;
        }
        RpcResponseDTO<UserDeviceCheckUpdateDTO> rpcResult = userDeviceRpcService.checkDeviceUpdate(uid, mac, appver);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    @ResponseBody()
    @RequestMapping(value="/force_upgrade",method={RequestMethod.POST})
    public void force_upgrade(HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid,
                               @RequestParam(required = true) String mac
                               ) {
    	mac = mac.toLowerCase();
    	if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
            return;
        }
        RpcResponseDTO<Boolean> resp = userDeviceRpcService.forceDeviceUpdate(uid, mac);
		if(!resp.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp));
    }
    
    private boolean validateDeviceName(String deviceName) throws  Exception {
        if (deviceName.getBytes("utf-8").length < 48) {
            return true;
        }
        return false;
    }

}
