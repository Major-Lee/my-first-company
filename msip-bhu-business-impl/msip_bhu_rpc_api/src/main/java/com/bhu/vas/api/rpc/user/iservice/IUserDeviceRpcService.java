package com.bhu.vas.api.rpc.user.iservice;

import java.util.List;
import java.util.Locale;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCloudDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.vto.device.DeviceConfigDetailVTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.UserDeviceTCPageVTO;
import com.bhu.vas.api.vto.device.UserDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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

/*    @Deprecated
    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid);*/

    RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(Locale locale, int uid, String dut, int pageNo, int pageSize);
    RpcResponseDTO<TailPage<UserDeviceDTO>> fetchPageBindDevices(Locale locale, int uid, String dut, int pageNo, int pageSize);

    UserDeviceTCPageVTO pageBindDevicesCustom(Integer uid, Integer u_id,
			String d_online, String s_content, int pageNo, int pageSize);

    List<UserDeviceVTO> pageBindDevices(Integer uid, Integer u_id,
    String d_online, String s_content, int pageNo, int pageSize);


    //RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevicesByAccOrUid(int countrycode,String acc,int uid);
//    RpcResponseDTO<Boolean>  unBindDevicesByAccOrUid(int countrycode,String acc,int uid);
    //int validateDeviceStatusIsOnlineAndBinded(String mac);

    RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac);

    RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac);

    boolean modifyDeviceName(String mac, int uid, String deviceName);

    RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate( int uid,String mac,String appver);
    RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac);
    
	RpcResponseDTO<DeviceDetailVTO> deviceDetail(int uid,String mac);
	
	RpcResponseDTO<DeviceConfigDetailVTO> deviceConfigDetail(String mac);
//    RpcResponseDTO<List<DeviceDetailVTO>> userDetail(int uid,int countrycode,String acc,int tid);
    
    //RpcResponseDTO<DeviceProfileVTO> portalDeviceProfile(String mac);
    
    public RpcResponseDTO<TailPage<UserDeviceCloudDTO>> devicePagesByUid(Integer uid, 
    		String dut, int pageNo, int pageSize);
    
    
    
    public RpcResponseDTO<Boolean> updateDeviceLocation(int uid, String mac, String country, String province, String city, String district, String street, String faddress, String lon, String lat);

}
