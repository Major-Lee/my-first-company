package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.rpc.facade.UserDeviceFacadeService;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 15/4/10.
 */
@Service("userDeviceRpcService")
public class UserDeviceRpcService implements IUserDeviceRpcService {

    private final static int WIFI_DEVICE_BIND_LIMIT_NUM = 3;

    @Resource
    private UserDeviceFacadeService userDeviceFacadeService;

    @Resource
    private DeviceFacadeService deviceFacadeService;

    @Override
    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {
        if (userDeviceFacadeService.countBindDevices(uid) >= WIFI_DEVICE_BIND_LIMIT_NUM) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_OWNER_REACHLIMIT);
        }
        int retStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        ResponseErrorCode responseErrorCode = null;
        if (retStatus < WIFI_DEVICE_STATUS_ONLINE) {
            if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_EXIST) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_EXIST;
            }else if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
                responseErrorCode = ResponseErrorCode.DEVICE_NOT_UROOTER;
            }else if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_ONLINE;
            } else if (retStatus == WIFI_DEVICE_STATUS_BINDED) {
                responseErrorCode = ResponseErrorCode.DEVICE_ALREADY_BEBINDED;
            }
            return RpcResponseDTOBuilder.builderErrorRpcResponse(responseErrorCode);
        } else if (retStatus == WIFI_DEVICE_STATUS_BINDED){
           return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED);
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
        if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_ONLINE) {
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
        if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_EXIST) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_EXIST.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_EXIST.i18n()));
        } else if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.i18n()));
        } else if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_NOT_UROOTER.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_NOT_UROOTER.i18n()));
        } else if (deviceStatus == WIFI_DEVICE_STATUS_BINDED) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_ALREADY_BEBINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_ALREADY_BEBINDED.i18n()));
        } else if (deviceStatus == WIFI_DEVICE_STATUS_UNBINDED) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_NOT_BINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_NOT_BINDED.i18n()));
        }
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceStatusDTO);
    }

    @Override
    public RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac) {
        return userDeviceFacadeService.fetchBindDeviceUser(mac);
    }

    @Override
    public boolean modifyDeviceName(String mac, int uid, String deviceName) {
        return userDeviceFacadeService.modifyUserDeviceName(mac, uid, deviceName);
    }
}
