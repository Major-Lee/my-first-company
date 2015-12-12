package com.bhu.vas.rpc.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.device.facade.DeviceUpgradeFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.jdo.ResponseErrorCode;

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

	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private DeviceUpgradeFacadeService deviceUpgradeFacadeService;
    //TODO：重复插入异常
    //1、首先得判定UserDevicePK(mac, uid) 是否存在
    //2、存在返回错误，不存在进行insert
    public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {

        UserDevice userDevice;
        UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
        userDevice = userDeviceService.getById(userDevicePK);
        if (userDevice != null) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED);
        } else {
            userDevice = new UserDevice();
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
    }

    public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
        //TODO(bluesand):有没有被其他用户绑定，现在一台设备只能被一个客户端绑定。
        List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
        for (UserDevice bindDevice : bindDevices) {
            if (bindDevice.getUid() != uid) {
                return RpcResponseDTOBuilder.builderErrorRpcResponse(
                        ResponseErrorCode.DEVICE_ALREADY_BEBINDED_OTHER,Boolean.FALSE);
            }
        }

        UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
        if (userDeviceService.deleteById(userDevicePK) > 0)  {
        	deliverMessageService.sendUserDeviceDestoryActionMessage(uid, mac);
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(
                    ResponseErrorCode.RPC_MESSAGE_UNSUPPORT, Boolean.FALSE);
        }

    }

    public boolean isBinded(String mac) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("mac", mac);
        return  userDeviceService.countByCommonCriteria(mc) > 0 ? true : false;
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
                        user.getMobileno().isEmpty() ? "***" : user.getMobileno().substring(0,3)));
                return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDTO);
            } else {
                return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED);
            }
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED);
        }
    }

    public boolean modifyUserDeviceName(String mac, int uid, String deviceName) {
        try {
            UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
            UserDevice userDevice = userDeviceService.getById(userDevicePK);
            if (userDevice == null) {
                return false;
            }
            userDevice.setId(new UserDevicePK(mac, uid));
            userDevice.setDevice_name(deviceName);
            userDeviceService.update(userDevice);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac){
    	UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
    	UserDevice userDevice = userDeviceService.getById(userDevicePK);
        if (userDevice == null) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,new String[]{mac});
        } else {
        	WifiDevice wifiDevice = wifiDeviceService.getById(mac);
        	if(wifiDevice == null){
        		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{mac});
        	}
        	if(!wifiDevice.isOnline()){
        		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,new String[]{mac});
        	}
        	//发送异步Device升级指令，指定立刻升级
        	{
        		//boolean isFirstGray = wifiDeviceGroupFacadeService.isDeviceInGrayGroup(mac);
        		UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac, wifiDevice);
        		//UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac, wifiDevice);
	        	if(upgrade != null && upgrade.isForceDeviceUpgrade()){
	        		//long new_taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
	        		//String cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, new_taskid, StringHelper.EMPTY_STRING, StringHelper.EMPTY_STRING, upgrade.getUpgradeurl());
	        		String cmdPayload = upgrade.buildUpgradeCMD(mac, 0, StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP);
	        		deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, /*new_taskid,OperationCMD.DeviceUpgrade.getNo(),*/ cmdPayload);
	        	}
        	}
        	return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        }   	
    }
    
    public RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate(int uid, String mac, String appver){
    	User user = userService.getById(uid);
    	if(user == null){
    		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
    	}
    	String handset_device = user.getLastlogindevice();
    	UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
    	UserDevice userDevice = userDeviceService.getById(userDevicePK);
        if (userDevice == null) {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED);
        } else {
        	WifiDevice wifiDevice = wifiDeviceService.getById(mac);
        	if(wifiDevice == null){
        		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{mac});
        	}
        	if(!wifiDevice.isOnline()){
        		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,new String[]{mac});
        	}
        	
        	UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceUpgradeWithClientVer(mac, wifiDevice,handset_device,appver);
        	if(upgrade != null && upgrade.isForceDeviceUpgrade()){
        		/*long new_taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
        		String cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, new_taskid,
        				WifiDeviceHelper.Upgrade_Default_BeginTime, 
        				WifiDeviceHelper.Upgrade_Default_EndTime, 
        				upgrade.getUpgradeurl());*/
        		String cmdPayload = upgrade.buildUpgradeCMD(mac, 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
        		deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, /*new_taskid,OperationCMD.DeviceUpgrade.getNo(),*/ cmdPayload);
        	}
        	UserDeviceCheckUpdateDTO retDTO = new UserDeviceCheckUpdateDTO();
        	retDTO.setMac(mac);
        	retDTO.setUid(uid);
        	retDTO.setOnline(wifiDevice.isOnline());
        	retDTO.setDut(upgrade!=null?upgrade.getDut():StringHelper.MINUS_STRING_GAP);
        	retDTO.setGray(upgrade!=null?upgrade.getGl():0);
        	retDTO.setForceDeviceUpdate(upgrade!=null?upgrade.isForceDeviceUpgrade():false);
        	retDTO.setForceAppUpdate(upgrade!=null?upgrade.isForceAppUpgrade():false);
        	retDTO.setCurrentDVB(wifiDevice.getOrig_swver());
        	retDTO.setCurrentAVB(upgrade!=null?upgrade.getCurrentAVB():null);
        	return RpcResponseDTOBuilder.builderSuccessRpcResponse(retDTO);
        }
    }
}
