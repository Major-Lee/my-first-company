package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;

import java.util.List;

/**
 * Created by bluesand on 15/4/10.
 */
public interface IUserDeviceRpcService {

    /**
     * 100以上的状态为用户在线后可以处理的状态.
     */
    int WIFI_DEVICE_STATUS_NOT_EXIST = 0;
    int WIFI_DEVICE_STATUS_NOT_UROOTER = 98;
    int WIFI_DEVICE_STATUS_NOT_ONLINE = 99;
    int WIFI_DEVICE_STATUS_ONLINE = 100;
    int WIFI_DEVICE_STATUS_BINDED = 101;
    int WIFI_DEVICE_STATUS_UNBINDED = 102;

    RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName);

    RpcResponseDTO<Boolean> unBindDevice(String mac, int uid);

    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid);

    int validateDeviceStatusIsOnlineAndBinded(String mac);

    RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac);



}
