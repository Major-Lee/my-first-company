package com.bhu.vas.rpc.service.device;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.rpc.facade.UserDeviceFacadeService;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bluesand on 15/4/10.
 */
@Service("userDeviceRpcService")
public class UserDeviceRpcService implements IUserDeviceRpcService {

    private final static int WIFI_DEVICE_STATUS_BINDED = 3;
    private final static int WIFI_DEVICE_STATUS_UNBINDED = 4;
    private final static int WIFI_DEVICE_BIND_LIMIT_NUM = 3;


    @Resource
    private UserDeviceFacadeService userDeviceFacadeService;

    @Resource
    private DeviceFacadeService deviceFacadeService;

    @Override
    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {
        int retStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        ResponseErrorCode responseErrorCode = null;
        if (retStatus < 4) {
            if (retStatus == 0) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_EXIST;
            } else if (retStatus == 1) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_ONLINE;
            } else if (retStatus == 3) {
                responseErrorCode = ResponseErrorCode.DEVICE_ALREADY_BEBINDED;
            }
            return RpcResponseDTOBuilder.builderErrorRpcResponse(responseErrorCode);
        } else {
            if (userDeviceFacadeService.countBindDevices(uid) >= WIFI_DEVICE_BIND_LIMIT_NUM) {
                return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_OWNER_REACHLIMIT);
            }
        }
        RpcResponseDTO<UserDeviceDTO> result = userDeviceFacadeService.bindDevice(mac,uid,deviceName);
        return result;
    }

    @Override
    public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
        return userDeviceFacadeService.unBindDevice(mac,uid);
    }

    @Override
    public int validateDeviceStatusIsOnlineAndBinded(String mac) {
        int retStatus = deviceFacadeService.getWifiDeviceOnlineStatus(mac);
        if (retStatus == 2) {
            if (userDeviceFacadeService.isBinded(mac)) {
                retStatus = WIFI_DEVICE_STATUS_BINDED;
            } else {
                retStatus = WIFI_DEVICE_STATUS_UNBINDED;
            }
        }
        return retStatus;
    }

    @Override
    public RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid) {
        return userDeviceFacadeService.fetchBindDevices(uid);
    }

    @Override
    public RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac) {
        UserDeviceStatusDTO userDeviceStatusDTO = new UserDeviceStatusDTO();
        int deviceStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        if (deviceStatus == 0) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_EXIST.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_EXIST.i18n()));
        } else if (deviceStatus == 1) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.i18n()));
        } else if (deviceStatus == 3) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_ALREADY_BEBINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_ALREADY_BEBINDED.i18n()));
        } else if (deviceStatus == 4) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_NOT_BINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_NOT_BINDED.i18n()));
        }
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceStatusDTO);
    }

}
