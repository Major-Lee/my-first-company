package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.rpc.facade.UserDeviceFacadeService;
import com.bhu.vas.rpc.facade.UserUnitFacadeService;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 15/4/10.
 */
@Service("userDeviceRpcService")
public class UserDeviceRpcService implements IUserDeviceRpcService {

    private final Logger logger = LoggerFactory.getLogger(UserDeviceRpcService.class);

    @Resource
    private UserDeviceFacadeService userDeviceFacadeService;

    @Resource
    private DeviceFacadeService deviceFacadeService;

    @Resource
    private UserUnitFacadeService userUnitFacadeService;

    @Override
    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid) {
        logger.info(String.format("bindDevice with mac[%s] uid[%s]",mac, uid));
        if (userDeviceFacadeService.countBindDevices(uid) >= UserUnitFacadeService.WIFI_DEVICE_BIND_LIMIT_NUM) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_OWNER_REACHLIMIT);
        }
        int retStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        ResponseErrorCode responseErrorCode = null;
        if (retStatus < WIFI_DEVICE_STATUS_ONLINE) {
            if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_EXIST) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_EXIST;
            }else if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
                responseErrorCode = ResponseErrorCode.DEVICE_NOT_MATCHED;
            }else if (retStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
                responseErrorCode = ResponseErrorCode.DEVICE_DATA_NOT_ONLINE;
            } else if (retStatus == WIFI_DEVICE_STATUS_BINDED) {
                responseErrorCode = ResponseErrorCode.DEVICE_ALREADY_BEBINDED;
            }
            return RpcResponseDTOBuilder.builderErrorRpcResponse(responseErrorCode,new String[]{mac});
        } else if (retStatus == WIFI_DEVICE_STATUS_BINDED){
           return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED,new String[]{mac});
        }
        
        String deviceName = deviceFacadeService.getUrouterSSID(mac);

        RpcResponseDTO<UserDeviceDTO> result = userDeviceFacadeService.bindDevice(mac,uid,deviceName == null ? "":deviceName );
        return result;
    }

    @Override
    public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
        logger.info(String.format("unBindDevice with mac[%s] uid[%s]",mac, uid));
        int deviceStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        logger.debug("devicestatus==" + deviceStatus);
        if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_EXIST ) {
        	return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{mac});
        } else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
        	return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_MATCHED,new String[]{mac});
        }  else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_BINDED
                || deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
            return userDeviceFacadeService.unBindDevice(mac, uid);
        } /*else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_UNBINDED) {
            //TODO(bluesand):未绑定过装备的时候，取消绑定
        	return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        }*/
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }

    //@Override
    private int validateDeviceStatusIsOnlineAndBinded(String mac) {
        logger.info(String.format("validateDeviceStatusIsOnlineAndBinded with mac[%s]",mac));
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
        logger.info(String.format("fetchBindDevices with uid[%s]", uid));
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userUnitFacadeService.fetchBindDevices(uid));
    }

    @Override
    public TailPage<UserDeviceDTO> pageBindDevices(int uid, String dut, int pageNo, int pageSize) {
        logger.info(String.format("pageBindDevices with uid[%s] dut[%s] pageNo[%s] pageSize[%s]", uid, dut, pageNo, pageSize));

        return userUnitFacadeService.fetchBindDevicesFromIndex(uid, dut, pageNo, pageSize);
    }

/*@Override
    public RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevicesByAccOrUid(int countrycode,String acc,int uid) {
        logger.info(String.format("fetchBindDevicesByAccOrUid with uid[%s]", uid));
        return userUnitFacadeService.fetchBindDevicesByAccOrUid(countrycode,acc,uid);
    }*/
    
    @Override
    public RpcResponseDTO<Boolean>  unBindDevicesByAccOrUid(int countrycode,String acc,int uid){
    	logger.info(String.format("unBindDevicesByAccOrUid with uid[%s]", uid));
        return userUnitFacadeService.unBindDevicesByAccOrUid(countrycode,acc,uid);
    }
    @Override
    public RpcResponseDTO<UserDeviceStatusDTO> validateDeviceStatus(String mac) {
        logger.info(String.format("validateDeviceStatus with mac[%s]", mac));
        UserDeviceStatusDTO userDeviceStatusDTO = new UserDeviceStatusDTO();
        int deviceStatus = validateDeviceStatusIsOnlineAndBinded(mac);
        if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_EXIST) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_EXIST.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_EXIST.i18n(),new String[]{mac}));
        } else if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_DATA_NOT_ONLINE.i18n(),new String[]{mac}));
        } else if (deviceStatus == DeviceFacadeService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_NOT_MATCHED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_NOT_MATCHED.i18n(),new String[]{mac}));
        } else if (deviceStatus == WIFI_DEVICE_STATUS_BINDED) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_ALREADY_BEBINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_ALREADY_BEBINDED.i18n(),new String[]{mac}));
        } else if (deviceStatus == WIFI_DEVICE_STATUS_UNBINDED) {
            userDeviceStatusDTO.setStatus(ResponseErrorCode.DEVICE_NOT_BINDED.code());
            userDeviceStatusDTO.setMessage(LocalI18NMessageSource.getInstance().getMessage(
                    ResponseErrorCode.DEVICE_NOT_BINDED.i18n(),new String[]{mac}));
        }
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceStatusDTO);
    }

    @Override
    public RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac) {
        logger.info(String.format("fetchBindDeviceUser with mac[%s]", mac));
        return userDeviceFacadeService.fetchBindDeviceUser(mac);
    }

    @Override
    public boolean modifyDeviceName(String mac, int uid, String deviceName) {
        logger.info(String.format("modifyDeviceName with mac[%s] uid[%s] deviceName[%s]", mac, uid, deviceName));
        return userDeviceFacadeService.modifyUserDeviceName(mac, uid, deviceName);
    }

	@Override
	public RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate(int uid,String mac, String appver) {
		logger.info(String.format("checkDeviceUpdate with uid[%s] mac[%s] appver[%s]", uid,mac,  appver));
		return userDeviceFacadeService.checkDeviceUpdate(uid, mac, appver);
	}

	@Override
	public RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac) {
		logger.info(String.format("forceDeviceUpdate with uid[%s] mac[%s]", uid,mac));
		return userDeviceFacadeService.forceDeviceUpdate(uid, mac);
	}
}
