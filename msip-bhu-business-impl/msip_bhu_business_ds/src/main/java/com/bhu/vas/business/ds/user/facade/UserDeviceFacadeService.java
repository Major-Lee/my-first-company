package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 15/4/10.
 */
@Service
public class UserDeviceFacadeService {
	public final static int WIFI_DEVICE_BIND_LIMIT_NUM = 10;
    //@Resource
   // private UserService userService;
    
    @Resource
    private UserDeviceService userDeviceService;
    
	@Resource
	private WifiDeviceService wifiDeviceService;

	//@Resource
	//private DeviceUpgradeFacadeService deviceUpgradeFacadeService;
    
/*	@Resource
	private DeliverMessageService deliverMessageService;*/
	
/*	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;*/
	
    //TODO：重复插入异常
    //1、首先得判定UserDevicePK(mac, uid) 是否存在
    //2、存在返回错误，不存在进行insert
    /*public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {
    	User user = userService.getById(uid);
    	if(user == null)
    		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
    	
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
            wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(mac, user, deviceName);
            
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
        	wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(mac, null, null);
        	deliverMessageService.sendUserDeviceDestoryActionMessage(uid, mac);
            return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(
                    ResponseErrorCode.RPC_MESSAGE_UNSUPPORT, Boolean.FALSE);
        }
    }*/

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
	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线
	 * @param uid
	 * @return
	 */
	public List<UserDeviceDTO>  fetchBindDevices(int uid) {
		List<UserDevice> userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		List<UserDeviceDTO> bindDevicesDTO = new ArrayList<UserDeviceDTO>();
		if(userDeviceList != null && !userDeviceList.isEmpty()){
			for (UserDevice userDevice : userDeviceList) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(userDevice.getMac());
				userDeviceDTO.setUid(userDevice.getUid());
				userDeviceDTO.setDevice_name(userDevice.getDevice_name());

				WifiDevice wifiDevice = wifiDeviceService.getById(userDevice.getMac());
				if (wifiDevice != null) {
					userDeviceDTO.setOnline(wifiDevice.isOnline());
					if (wifiDevice.isOnline()) { //防止有些设备已经离线了，没有更新到后台
						userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
								.presentOnlineSize(userDevice.getMac()));
					}
					userDeviceDTO.setVer(wifiDevice.getOrig_swver());
					userDeviceDTO.setWork_mode(wifiDevice.getWork_mode());
					userDeviceDTO.setOrig_model(wifiDevice.getOrig_model());
				}
				bindDevicesDTO.add(userDeviceDTO);
			}
		}
		return bindDevicesDTO;
	}
    /*public RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac) {
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
                return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,new String[]{mac});
            }
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,new String[]{mac});
        }
    }*/

    /*public boolean modifyUserDeviceName(String mac, int uid, String deviceName) {
        try {
            UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
            UserDevice userDevice = userDeviceService.getById(userDevicePK);
            if (userDevice == null) {
                return false;
            }
            userDevice.setId(new UserDevicePK(mac, uid));
            userDevice.setDevice_name(deviceName);
            userDeviceService.update(userDevice);
            
            wifiDeviceStatusIndexIncrementService.bindUserDNickUpdIncrement(mac, deviceName);
            return true;
        } catch (Exception e) {
            return false;
        }

    }*/

/*    public RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac){
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
	        		deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, new_taskid,OperationCMD.DeviceUpgrade.getNo(), cmdPayload);
	        	}
        	}
        	return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
        }   	
    }*/
    
    /*public RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate(int uid, String mac, String appver){
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
        	app检测设备是否需要升级的时候不进行定时升级指令的操作
        	if(upgrade != null && upgrade.isForceDeviceUpgrade()){
        		String cmdPayload = upgrade.buildUpgradeCMD(mac, 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
        		deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, new_taskid,OperationCMD.DeviceUpgrade.getNo(), cmdPayload);
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
    }*/
    
    
	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线
	 * @param uid
	 * @return
	 */
/*	public List<UserDeviceDTO>  fetchBindDevices(int uid) {
		List<UserDevice> userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		List<UserDeviceDTO> bindDevicesDTO = new ArrayList<UserDeviceDTO>();
		if(userDeviceList != null && !userDeviceList.isEmpty()){
			for (UserDevice userDevice : userDeviceList) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(userDevice.getMac());
				userDeviceDTO.setUid(userDevice.getUid());
				userDeviceDTO.setDevice_name(userDevice.getDevice_name());

				WifiDevice wifiDevice = wifiDeviceService.getById(userDevice.getMac());
				if (wifiDevice != null) {
					userDeviceDTO.setOnline(wifiDevice.isOnline());
					if (wifiDevice.isOnline()) { //防止有些设备已经离线了，没有更新到后台
						userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
								.presentOnlineSize(userDevice.getMac()));
					}
					userDeviceDTO.setVer(wifiDevice.getOrig_swver());
					userDeviceDTO.setWork_mode(wifiDevice.getWork_mode());
				}
				bindDevicesDTO.add(userDeviceDTO);
			}
		}
		return bindDevicesDTO;
	}*/


	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线
	 * @param uid
	 * @param dut
	 * @return
	 */
/*	public List<UserDeviceDTO>  fetchBindDevices(int uid, String dut) {

		List<UserDeviceDTO> dtos = new ArrayList<UserDeviceDTO>();
		List<WifiDeviceDocument> searchDocuments = wifiDeviceDataSearchService.searchListByUidAndDut(uid, dut);

		if (searchDocuments != null && !searchDocuments.isEmpty()) {
			for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(wifiDeviceDocument.getD_mac());
				userDeviceDTO.setUid(Integer.parseInt(wifiDeviceDocument.getU_id()));
				userDeviceDTO.setDevice_name(wifiDeviceDocument.getU_dnick());
				userDeviceDTO.setWork_mode(wifiDeviceDocument.getD_workmodel());

				if ("1".equals(wifiDeviceDocument.getD_online())) {
					userDeviceDTO.setOnline(true);
					userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
							.presentOnlineSize(wifiDeviceDocument.getD_mac()));
				}
				userDeviceDTO.setVer(wifiDeviceDocument.getD_origswver());
				dtos.add(userDeviceDTO);
			}
		}

		return dtos;
	}*/


	/**
	 * 通过搜索引擎获取用户绑定的设备
	 * @param uid
	 * @param pageNo
	 * @param pageSize
	 * @return
     */
/*	public UserDeviceTCPageVTO fetchBindDevicesFromIndex(Integer uid, Integer u_id, 
			String d_online, String s_content, int pageNo, int pageSize) {

		int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
		SearchConditionMessage sm = WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, d_online, s_content);
		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchByConditionMessage(sm,searchPageNo,pageSize);
		//System.out.println("fetchBindDevicesFromIndex === " +  search_result);
		
		List<UserDeviceVTO> vtos = null;
		int total = 0;
		if(search_result != null){
			total = (int)search_result.getTotalElements();//.getTotal();
			if(total == 0){
				vtos = Collections.emptyList();
			}else{
				List<WifiDeviceDocument> searchDocuments = search_result.getContent();//.getResult();
				if(searchDocuments.isEmpty()) {
					vtos = Collections.emptyList();
				}else{
					vtos = new ArrayList<UserDeviceVTO>();
					//WifiDeviceVTO1 vto = null;
					//int startIndex = PageHelper.getStartIndexOfPage(searchPageNo, pageSize);
					for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
						UserDeviceVTO userDeviceVTO = new UserDeviceVTO();
						userDeviceVTO.setD_mac(wifiDeviceDocument.getD_mac());
						userDeviceVTO.setD_online(wifiDeviceDocument.getD_online());
						userDeviceVTO.setD_origmodel(wifiDeviceDocument.getD_origmodel());
						userDeviceVTO.setD_origswver(wifiDeviceDocument.getD_origswver());
						userDeviceVTO.setD_wanip(wifiDeviceDocument.getD_wanip());
						userDeviceVTO.setU_dnick(wifiDeviceDocument.getU_dnick());
						userDeviceVTO.setU_id(wifiDeviceDocument.getU_id());
						userDeviceVTO.setD_workmodel(wifiDeviceDocument.getD_workmodel());
						userDeviceVTO.setD_dut(wifiDeviceDocument.getD_dut());
						userDeviceVTO.setD_type(wifiDeviceDocument.getD_type());
	
						vtos.add(userDeviceVTO);
					}
				}
			}
		}
		TailPage<UserDeviceVTO> pages = new CommonPage<UserDeviceVTO>(pageNo, pageSize, total, vtos);

		//获取3种在线状态的数量，在线，离线，全部
		UserDeviceStatisticsVTO statistics = new UserDeviceStatisticsVTO();
		OnlineEnum onlineEnum = WifiDeviceDocumentEnumType.OnlineEnum.getOnlineEnumFromType(d_online);
		//当前搜索条件为全部，则查询在线和离线数量
		long to = 0l;
		long on = 0l;
		long of = 0l;
		if(onlineEnum == null){
			to = total;
			on = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, OnlineEnum.Online.getType(), null));
			of = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, OnlineEnum.Offline.getType(), null));
		}
		//当前搜索条件为在线，则查询全部和离线数量
		else if(onlineEnum.getType().equals(OnlineEnum.Online.getType())){
			on = total;
			to = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, null, null));
			of = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, OnlineEnum.Offline.getType(), null));
		}
		//当前搜索条件为离线，则查询全部和在线数量
		else if(onlineEnum.getType().equals(OnlineEnum.Offline.getType())){
			of = total;
			to = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, null, null));
			on = wifiDeviceDataSearchService.searchCountByConditionMessage(WifiDeviceTCSearchMessageBuilder
					.builderSearchTCMessage(u_id, OnlineEnum.Online.getType(), null));
		}
		statistics.setTo(to);
		statistics.setOn(on);
		statistics.setOf(of);
		
		UserDeviceTCPageVTO vto = new UserDeviceTCPageVTO();
		vto.setPages(pages);
		vto.setStatistics(statistics);
		return vto;
	}*/
		

	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * @param countrycode
	 * @param acc
	 * @return
	 */
/*	public RpcResponseDTO<List<UserDeviceDTO>>  fetchBindDevicesByAccOrUid(int countrycode,String acc,int uid) {
		if(uid <=0 && StringUtils.isEmpty(acc))
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		if(uid <=0){
			Integer ret_uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(ret_uid == null || ret_uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			uid = ret_uid.intValue();
		}
		List<UserDeviceDTO> fetchBindDevices = fetchBindDevices(uid);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(fetchBindDevices);
	}*/
	
	
	
	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * @param countrycode
	 * @param acc
	 * @return
	 */
/*	public RpcResponseDTO<Boolean>  unBindDevicesByAccOrUid(int countrycode,String acc,int uid) {
		if(uid <=0 && StringUtils.isEmpty(acc))
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		if(uid <=0){
			Integer ret_uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(ret_uid == null || ret_uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			uid = ret_uid.intValue();
		}
		List<UserDevice> userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		for(UserDevice ud:userDeviceList){
			UserDevicePK userDevicePK = ud.getId();
	        if (userDeviceService.deleteById(userDevicePK) > 0)  {
	        	deliverMessageService.sendUserDeviceDestoryActionMessage(uid, userDevicePK.getMac());
	        }
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}*/
	
	public void validateUserDeviceBind(Integer uid, String mac){
		//验证用户是否管理设备
		UserDevice userdevice_entity = userDeviceService.getById(new UserDevicePK(mac, uid));
		if(userdevice_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_YOURBINDED,new String[]{mac});
		}
	}
}
