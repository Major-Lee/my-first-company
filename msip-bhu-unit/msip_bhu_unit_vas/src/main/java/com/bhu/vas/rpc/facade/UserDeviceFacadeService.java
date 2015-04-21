package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.MobileCaptchaCodeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 15/4/10.
 */
@Service
public class UserDeviceFacadeService {

    @Resource
    private UserDeviceService userDeviceService;
    
	@Resource
	private DeliverMessageService deliverMessageService;

    @Resource
    private UserService userService;
	

    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {

        UserDevice userDevice = new UserDevice();
        userDevice.setId(new UserDevicePK(mac, uid));
        userDevice.setCreated_at(new Date());
        userDevice.setDevice_name(deviceName);
        userDeviceService.insert(userDevice);
        
        deliverMessageService.sendUserDeviceRegisterActionMessage(uid, mac);
        
        UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
        userDeviceDTO.setMac(mac);
        userDeviceDTO.setUid(uid);
        userDeviceDTO.setDevice_name(deviceName);

        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceDTO);
    }

    public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
        UserDevice userDevice = new UserDevice();
        userDevice.setId(new UserDevicePK(mac, uid));

        //TODO(bluesand):现在一台设备只能被一个客户端绑定。
        List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
        for (UserDevice bindDevice : bindDevices) {
            if (bindDevice.getUid() != uid) {
                return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED_OTHER,Boolean.FALSE);
            }
        }

        if (userDeviceService.delete(userDevice) > 0)  {
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_MESSAGE_UNSUPPORT,Boolean.FALSE);
        }

    }

    public boolean isBinded(String mac) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("mac", mac);
        return  userDeviceService.countByCommonCriteria(mc) > 0 ? true : false;
    }

    public RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevices(int uid) {
        List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesWithLimit(uid, 3);
        List<UserDeviceDTO> bindDevicesDTO = new ArrayList<UserDeviceDTO>();
        for (UserDevice userDevice : bindDevices) {
            UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
            userDeviceDTO.setMac(userDevice.getMac());
            userDeviceDTO.setUid(userDevice.getUid());
            userDeviceDTO.setDevice_name(userDevice.getDevice_name());
            bindDevicesDTO.add(userDeviceDTO);
        }
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(bindDevicesDTO);

    }

    public int countBindDevices(int uid) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("uid", uid);
        return userDeviceService.countByModelCriteria(mc);
    }

    public RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac) {
        UserDTO userDTO = new UserDTO();
        List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
        if (!bindDevices.isEmpty()) {
            int uid = bindDevices.get(0).getUid();
            User user = userService.getById(uid);
            userDTO.setId(uid);
            if (user != null) {
                userDTO.setCountrycode(user.getCountrycode());
                userDTO.setMobileno(String.format("%s********",
                        user.getMobileno().isEmpty() ? "***" : user.getMobileno().substring(3)));
            }
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDTO);
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(null);
        }


    }

}
