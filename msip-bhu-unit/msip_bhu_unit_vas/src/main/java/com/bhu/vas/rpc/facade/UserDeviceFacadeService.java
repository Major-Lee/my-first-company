package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.jdo.ResponseErrorCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluesand on 15/4/10.
 */
@Service
public class UserDeviceFacadeService {

    @Resource
    private UserDeviceService userDeviceService;

    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {

        UserDevice userDevice = new UserDevice();
        userDevice.setId(new UserDevicePK(mac, uid));
        userDevice.setDevice_name(deviceName);
        userDeviceService.insert(userDevice);
        UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
        userDeviceDTO.setMac(mac);
        userDeviceDTO.setUid(uid);
        userDeviceDTO.setDevice_name(deviceName);

        return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceDTO);

    }

    public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
        UserDevice userDevice = new UserDevice();
        userDevice.setId(new UserDevicePK(mac, uid));
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
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("uid", uid);
        mc.setPageNumber(1);
        mc.setPageSize(3);
        List<UserDevice> bindDevices = userDeviceService.findModelByModelCriteria(mc);

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
}
