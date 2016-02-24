package com.bhu.vas.api.rpc.user.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.UserDeviceTCPageVTO;
import com.bhu.vas.api.vto.device.UserDeviceVTO;

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

    RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid);

    RpcResponseDTO<Boolean> unBindDevice(String mac, int uid);

    @Deprecated
    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid);

    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid, String dut);

    UserDeviceTCPageVTO pageBindDevicesCustom(Integer uid, Integer u_id,
			String d_online, String s_content, int pageNo, int pageSize);

    List<UserDeviceVTO> pageBindDevices(Integer uid, Integer u_id,
    String d_online, String s_content, int pageNo, int pageSize);


    //RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevicesByAccOrUid(int countrycode,String acc,int uid);
    RpcResponseDTO<Boolean>  unBindDevicesByAccOrUid(int countrycode,String acc,int uid);
    //int validateDeviceStatusIsOnlineAndBinded(String mac);

    RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac);

    RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac);

    boolean modifyDeviceName(String mac, int uid, String deviceName);

    RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate( int uid,String mac,String appver);
    RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac);
    
	RpcResponseDTO<DeviceDetailVTO> deviceDetail(int uid,String mac);
	
    RpcResponseDTO<List<DeviceDetailVTO>> userDetail(int uid,int countrycode,String acc,int tid);
}
