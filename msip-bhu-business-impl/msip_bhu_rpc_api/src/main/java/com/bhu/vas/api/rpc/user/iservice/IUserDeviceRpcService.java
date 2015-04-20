package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;

import java.util.List;

/**
 * Created by bluesand on 15/4/10.
 */
public interface IUserDeviceRpcService {

    RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName);

    RpcResponseDTO<Boolean> unBindDevice(String mac, int uid);

    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid);

    int validateDeviceStatusIsOnlineAndBinded(String mac);

    RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac);



}
