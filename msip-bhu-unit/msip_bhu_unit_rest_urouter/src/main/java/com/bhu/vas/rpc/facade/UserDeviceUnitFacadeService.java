package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingInterfaceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGrayVersionPK;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCloudDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.api.vto.config.URouterDeviceConfigInterfaceVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigRadioVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVapVTO;
import com.bhu.vas.api.vto.device.DeviceBaseVTO;
import com.bhu.vas.api.vto.device.DeviceConfigDetailVTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.DeviceOperationVTO;
import com.bhu.vas.api.vto.device.DevicePresentVTO;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.api.vto.device.UserDeviceStatisticsVTO;
import com.bhu.vas.api.vto.device.UserDeviceTCPageVTO;
import com.bhu.vas.api.vto.device.UserDeviceVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetUnitPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceModeStatusService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceUpgradeFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.search.builder.WifiDeviceTCSearchMessageBuilder;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementProcesser;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.JNIRsaHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 15/4/10.
 */
@Service
public class UserDeviceUnitFacadeService {
	@Resource
	private UserService userService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	/*
	 * @Resource private UserDeviceService userDeviceService;
	 * 
	 * @Resource private UserDeviceFacadeService userDeviceFacadeService;
	 */

	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;

	@Resource
	private UserWifiDeviceService userWifiDeviceService;

	@Resource
	private DeviceFacadeService deviceFacadeService;

	@Resource
	private UserSettingStateService userSettingStateService;

	@Resource
	private DeviceUpgradeFacadeService deviceUpgradeFacadeService;

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	@Resource
	private WifiDeviceIndexIncrementProcesser wifiDeviceIndexIncrementProcesser;

	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;

	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;

	@Resource
	private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;

	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;

	@Resource
	private ChargingFacadeService chargingFacadeService;
	// @Resource
	// private TagDevicesService tagDevicesService;

	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;

	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;

	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

	// TODO：重复插入异常
	// 1、首先得判定UserDevicePK(mac, uid) 是否存在
	// 2、存在返回错误，不存在进行insert
	public RpcResponseDTO<UserDeviceDTO> bindDevice(String mac, int uid, String deviceName) {
		User user = userService.getById(uid);
		if (user == null)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);

		/*
		 * UserDevice userDevice; UserDevicePK userDevicePK = new
		 * UserDevicePK(mac, uid); userDevice =
		 * userDeviceService.getById(userDevicePK);
		 */
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice != null) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED);
		} else {
			/*
			 * userDevice = new UserDevice(); userDevice.setId(new
			 * UserDevicePK(mac, uid)); userDevice.setCreated_at(new Date());
			 * userDevice.setDevice_name(deviceName);
			 * userDeviceService.insert(userDevice);
			 */
			userWifiDeviceFacadeService.insertUserWifiDevice(mac, uid, deviceName);

//			deviceFacadeService.updateDeviceIndustry(mac, null);

			wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(mac, user, deviceName, null);

			deliverMessageService.sendUserDeviceRegisterActionMessage(uid, mac, true);
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
			userDeviceDTO.setMac(mac);
			userDeviceDTO.setUid(uid);
			userDeviceDTO.setDevice_name(deviceName);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDeviceDTO);
		}
	}

	public RpcResponseDTO<Boolean> unBindDevice(String mac, int uid) {
		// TODO(bluesand):有没有被其他用户绑定，现在一台设备只能被一个客户端绑定。
		/*
		 * List<UserDevice> bindDevices =
		 * userDeviceService.fetchBindDevicesUsers(mac); for (UserDevice
		 * bindDevice : bindDevices) { if (bindDevice.getUid() != uid) { return
		 * RpcResponseDTOBuilder.builderErrorRpcResponse(
		 * ResponseErrorCode.DEVICE_ALREADY_BEBINDED_OTHER,Boolean.FALSE); } }
		 */
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice != null && !userWifiDevice.getUid().equals(uid)) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED_OTHER,
					Boolean.FALSE);
		}

		/*
		 * UserDevicePK userDevicePK = new UserDevicePK(mac, uid); if
		 * (userDeviceService.deleteById(userDevicePK) > 0) {
		 */
		if (userWifiDeviceService.deleteById(mac) > 0) {

//			deviceFacadeService.updateDeviceIndustry(mac, null);
			wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(mac, null, null, null);
			deliverMessageService.sendUserDeviceDestoryActionMessage(uid, mac);
		} /*
			 * else { return RpcResponseDTOBuilder.builderErrorRpcResponse(
			 * ResponseErrorCode.RPC_MESSAGE_UNSUPPORT, Boolean.FALSE); }
			 */
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}

	/*
	 * public boolean isBinded(String mac) { ModelCriteria mc = new
	 * ModelCriteria(); mc.createCriteria().andColumnEqualTo("mac", mac); return
	 * userDeviceService.countByCommonCriteria(mc) > 0 ? true : false; }
	 */

	/*
	 * public int countBindDevices(int uid) { ModelCriteria mc = new
	 * ModelCriteria(); mc.createCriteria().andColumnEqualTo("uid", uid); return
	 * userDeviceService.countByModelCriteria(mc); }
	 */

	public RpcResponseDTO<UserDTO> fetchBindDeviceUser(String mac) {
		// UserDTO userDTO = new UserDTO();
		/*
		 * List<UserDevice> bindDevices =
		 * userDeviceService.fetchBindDevicesUsers(mac); if
		 * (!bindDevices.isEmpty()) { int uid = bindDevices.get(0).getUid();
		 * User user = userService.getById(uid);
		 */
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice != null) {
			int uid = userWifiDevice.getUid();
			User user = userService.getById(uid);
			// userDTO.setId(uid);
			if (user != null) {
				UserDTO userDTO = RpcResponseDTOBuilder.builderUserDTOFromUser(user, false);
				if (StringUtils.isNotEmpty(userDTO.getMobileno())) {
					userDTO.setMobileno(String.format("%s********",
							user.getMobileno().isEmpty() ? "***" : user.getMobileno().substring(0, 3)));
				}
				/*
				 * userDTO.setCountrycode(user.getCountrycode());
				 * userDTO.setMobileno(String.format("%s********",
				 * user.getMobileno().isEmpty() ? "***" :
				 * user.getMobileno().substring(0,3)));
				 */
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(userDTO);
			} else {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,
						new String[] { mac });
			}
		} else {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,
					new String[] { mac });
		}
	}

	public boolean modifyUserDeviceName(String mac, int uid, String deviceName) {
		try {
			/*
			 * UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
			 * UserDevice userDevice = userDeviceService.getById(userDevicePK);
			 * if (userDevice == null) { return false; }
			 */
			UserWifiDevice userWifiDevice = userWifiDeviceFacadeService.findUserWifiDeviceById(mac, uid);
			if (userWifiDevice == null) {
				return false;
			}

			/*
			 * userDevice.setId(new UserDevicePK(mac, uid));
			 * userDevice.setDevice_name(deviceName);
			 * userDevice.setDevice_name_modifyed(true);
			 * userDeviceService.update(userDevice);
			 */

			userWifiDevice.setDevice_name(deviceName);
			userWifiDevice.setDevice_name_modifyed(true);
			userWifiDeviceService.update(userWifiDevice);

			wifiDeviceStatusIndexIncrementService.bindUserDNickUpdIncrement(mac, deviceName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public RpcResponseDTO<Boolean> forceDeviceUpdate(int uid, String mac) {
		// UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
		// UserDevice userDevice = userDeviceService.getById(userDevicePK);
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice == null) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED,
					new String[] { mac });
		} else {
			WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			if (wifiDevice == null) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,
						new String[] { mac });
			}
			if (!wifiDevice.isOnline()) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,
						new String[] { mac });
			}
			// 发送异步Device升级指令，指定立刻升级
			{
				String cmdPayload = deviceUpgradeFacadeService.clientForceDeviceUpgrade(mac,
						wifiDevice.getOrig_swver());
				if (StringUtils.isNotEmpty(cmdPayload)) {
					deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, cmdPayload);
				}
				// boolean isFirstGray =
				// wifiDeviceGroupFacadeService.isDeviceInGrayGroup(mac);
				/*
				 * UpgradeDTO upgrade =
				 * deviceUpgradeFacadeService.checkDeviceUpgrade(mac,
				 * wifiDevice); //UpgradeDTO upgrade =
				 * deviceUpgradeFacadeService.checkDeviceUpgrade(mac,
				 * wifiDevice); if(upgrade != null &&
				 * upgrade.isForceDeviceUpgrade()){ //long new_taskid =
				 * CMDBuilder.auto_taskid_fragment.getNextSequence(); //String
				 * cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, new_taskid,
				 * StringHelper.EMPTY_STRING, StringHelper.EMPTY_STRING,
				 * upgrade.getUpgradeurl()); String cmdPayload =
				 * upgrade.buildUpgradeCMD(mac, 0,
				 * StringHelper.EMPTY_STRING_GAP,
				 * StringHelper.EMPTY_STRING_GAP);
				 * deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac,
				 * new_taskid,OperationCMD.DeviceUpgrade.getNo(), cmdPayload); }
				 */
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
	}

	/**
	 * 客户端接口响应 期望目标：只有固件固化版本才考虑给客户端提示升级，其他版本由于有灰度的存在，升级还是很及时的 规则
	 * 配置文件中定义固件固化版本定义在配置中并加载配置 如果发现设备版本不是固件固化版本则直接false
	 * 如果发现设备版本是固件固化版本则直接走检测升级流程
	 * 
	 * @param uid
	 * @param mac
	 * @param appver
	 * @return
	 */
	public RpcResponseDTO<UserDeviceCheckUpdateDTO> checkDeviceUpdate(int uid, String mac, String appver) {
		User user = userService.getById(uid);
		if (user == null) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		String handset_device = user.getLastlogindevice();
		// UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
		// UserDevice userDevice = userDeviceService.getById(userDevicePK);
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice == null) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_NOT_BINDED);
		} else {
			WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			if (wifiDevice == null) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,
						new String[] { mac });
			}
			if (!wifiDevice.isOnline()) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,
						new String[] { mac });
			}
			UserDeviceCheckUpdateDTO clientCheckDeviceUpgrade = deviceUpgradeFacadeService.clientCheckDeviceUpgrade(mac,
					wifiDevice, handset_device, appver);
			/*
			 * UpgradeDTO upgrade = null;
			 * if(!BusinessRuntimeConfiguration.isInitialDeviceFirmwareVersion(
			 * wifiDevice.getOrig_swver())){ //非固件固化定义的版本，直接返回不需要强制升级
			 * System.out.println(String.format(
			 * "not initial device firmware version:[%s] for[%s]",
			 * wifiDevice.getOrig_swver(),wifiDevice.getId())); }else{ upgrade =
			 * deviceUpgradeFacadeService.checkDeviceUpgradeWithClientVer(mac,
			 * wifiDevice,handset_device,appver); app检测设备是否需要升级的时候不进行定时升级指令的操作
			 * if(upgrade != null && upgrade.isForceDeviceUpgrade()){ String
			 * cmdPayload = upgrade.buildUpgradeCMD(mac, 0,
			 * WifiDeviceHelper.Upgrade_Default_BeginTime,
			 * WifiDeviceHelper.Upgrade_Default_EndTime);
			 * deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac,
			 * new_taskid,OperationCMD.DeviceUpgrade.getNo(), cmdPayload); } }
			 * UserDeviceCheckUpdateDTO retDTO = new UserDeviceCheckUpdateDTO();
			 * retDTO.setMac(mac); retDTO.setUid(uid);
			 * retDTO.setOnline(wifiDevice.isOnline());
			 * retDTO.setDut(upgrade!=null?upgrade.getDut():StringHelper.
			 * MINUS_STRING_GAP);
			 * retDTO.setGray(upgrade!=null?upgrade.getGl():0);
			 * retDTO.setForceDeviceUpdate(upgrade!=null?upgrade.
			 * isForceDeviceUpgrade():false);
			 * retDTO.setForceAppUpdate(upgrade!=null?upgrade.isForceAppUpgrade(
			 * ):false); retDTO.setCurrentDVB(wifiDevice.getOrig_swver());
			 * retDTO.setCurrentAVB(upgrade!=null?upgrade.getCurrentAVB():null);
			 */
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(clientCheckDeviceUpgrade);
		}
	}

	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线(兼容旧版app的接口)
	 * 
	 * @param uid
	 * @param dut
	 * @return
	 */
	public List<UserDeviceDTO> fetchBindDevices(int uid, String dut, int pageNo, int pageSize) {
		int searchPageNo = pageNo >= 1 ? (pageNo - 1) : pageNo;
		List<UserDeviceDTO> dtos = new ArrayList<UserDeviceDTO>();
		List<WifiDeviceDocument> searchDocuments = wifiDeviceDataSearchService.searchListByUidAndDut(uid, dut,
				searchPageNo, pageSize);

		if (searchDocuments != null && !searchDocuments.isEmpty()) {
			for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(wifiDeviceDocument.getD_mac());
				userDeviceDTO.setUid(Integer.parseInt(wifiDeviceDocument.getU_id()));
				userDeviceDTO.setDevice_name(wifiDeviceDocument.getU_dnick());
				userDeviceDTO.setWork_mode(wifiDeviceDocument.getD_workmodel());
				userDeviceDTO.setOrig_model(wifiDeviceDocument.getD_origmodel());
				userDeviceDTO.setAdd(wifiDeviceDocument.getD_address());
				userDeviceDTO.setIp(wifiDeviceDocument.getD_wanip());
				userDeviceDTO.setD_sn(wifiDeviceDocument.getD_sn());
				userDeviceDTO.setD_address(wifiDeviceDocument.getD_address());
				userDeviceDTO.setLastlogoutat(wifiDeviceDocument.getD_lastlogoutat());
				userDeviceDTO.setLastregedat(wifiDeviceDocument.getD_lastregedat());
				userDeviceDTO.setD_distributor_type(wifiDeviceDocument.getD_distributor_type());
				if (wifiDeviceDocument.getD_snk_allowturnoff() != null) {
					userDeviceDTO.setD_snk_allowturnoff(Integer.parseInt(wifiDeviceDocument.getD_snk_allowturnoff()));
				} else {
					userDeviceDTO.setD_snk_allowturnoff(1);
				}

				if ("1".equals(wifiDeviceDocument.getD_online())) {
					userDeviceDTO.setOnline(true);
					userDeviceDTO.setOhd_count(WifiDeviceHandsetUnitPresentSortedSetService.getInstance()
							.presentOnlineSize(wifiDeviceDocument.getD_mac()));
				}
				userDeviceDTO.setD_online(wifiDeviceDocument.getD_online());
				userDeviceDTO.setVer(wifiDeviceDocument.getD_origswver());
				dtos.add(userDeviceDTO);
			}
		}

		return dtos;
	}

	/**
	 * 获取用户绑定的设备，设备状态只有在线和不在线
	 * 
	 * @param uid
	 * @param dut
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserDeviceDTO>> fetchPageBindDevices(int uid, String dut, int pageNo, int pageSize) {
		try {
			int searchPageNo = pageNo >= 1 ? (pageNo - 1) : pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageByUidAndDut(uid, dut,
					searchPageNo, pageSize);

			List<UserDeviceDTO> vtos = null;
			List<String> macs = null;
			int total = 0;
			if (search_result != null) {
				total = (int) search_result.getTotalElements();// .getTotal();
				if (total == 0) {
					vtos = Collections.emptyList();
				} else {
					List<WifiDeviceDocument> searchDocuments = search_result.getContent();// .getResult();
					if (searchDocuments.isEmpty()) {
						vtos = Collections.emptyList();
					} else {
						vtos = new ArrayList<UserDeviceDTO>();
						macs = new ArrayList<String>();
						for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
							UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
							userDeviceDTO.setMac(wifiDeviceDocument.getD_mac());
							userDeviceDTO.setUid(Integer.parseInt(wifiDeviceDocument.getU_id()));
							userDeviceDTO.setDevice_name(wifiDeviceDocument.getU_dnick());
							userDeviceDTO.setWork_mode(wifiDeviceDocument.getD_workmodel());
							userDeviceDTO.setOrig_model(wifiDeviceDocument.getD_origmodel());
							userDeviceDTO.setAdd(wifiDeviceDocument.getD_address());
							userDeviceDTO.setIp(wifiDeviceDocument.getD_wanip());
							userDeviceDTO.setD_sn(wifiDeviceDocument.getD_sn());
							userDeviceDTO.setD_address(wifiDeviceDocument.getD_address());
							userDeviceDTO.setD_distributor_type(wifiDeviceDocument.getD_distributor_type());
							if(wifiDeviceDocument.getD_geopoint() != null && wifiDeviceDocument.getD_geopoint().length == 2){
								userDeviceDTO.setLon(String.valueOf(wifiDeviceDocument.getD_geopoint()[0]));;
								userDeviceDTO.setLat(String.valueOf(wifiDeviceDocument.getD_geopoint()[1]));;
							}
							userDeviceDTO.setO_scalelevel(wifiDeviceDocument.getO_scalelevel());
							if (wifiDeviceDocument.getD_snk_allowturnoff() != null) {
								userDeviceDTO.setD_snk_allowturnoff(
										Integer.parseInt(wifiDeviceDocument.getD_snk_allowturnoff()));
							} else {
								userDeviceDTO.setD_snk_allowturnoff(1);
							}
							if ("1".equals(wifiDeviceDocument.getD_online())) {
								userDeviceDTO.setOnline(true);
								userDeviceDTO.setOhd_count(WifiDeviceHandsetUnitPresentSortedSetService.getInstance()
										.presentOnlineSize(wifiDeviceDocument.getD_mac()));
							}
							userDeviceDTO.setD_online(wifiDeviceDocument.getD_online());
							userDeviceDTO.setVer(wifiDeviceDocument.getD_origswver());
							userDeviceDTO.setProvince(wifiDeviceDocument.getD_province());
							userDeviceDTO.setCity(wifiDeviceDocument.getD_city());
							userDeviceDTO.setDistrict(wifiDeviceDocument.getD_district());
							userDeviceDTO.setLastlogoutat(wifiDeviceDocument.getD_lastlogoutat());
							userDeviceDTO.setLastregedat(wifiDeviceDocument.getD_lastregedat());
							macs.add(wifiDeviceDocument.getD_mac());
							vtos.add(userDeviceDTO);
						}
					}
				}
			} else {
				vtos = Collections.emptyList();
			}
			TailPage<UserDeviceDTO> returnRet = new CommonPage<UserDeviceDTO>(pageNo, pageSize, total, vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		} catch (BusinessI18nCodeException i18nex) {
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	private List<UserDeviceVTO> builderUserDeviceVTOs(Page<WifiDeviceDocument> search_result) {
		List<UserDeviceVTO> vtos = null;
		List<WifiDeviceDocument> searchDocuments = search_result.getContent();// .getResult();
		if (searchDocuments.isEmpty()) {
			vtos = Collections.emptyList();
		} else {
			vtos = new ArrayList<UserDeviceVTO>();
			// WifiDeviceVTO1 vto = null;
			// int startIndex = PageHelper.getStartIndexOfPage(searchPageNo,
			// pageSize);

			List<String> macs = new ArrayList<String>();

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
				userDeviceVTO.setD_address(wifiDeviceDocument.getD_address());
				vtos.add(userDeviceVTO);

				macs.add(wifiDeviceDocument.getD_mac());
			}

			List<WifiDeviceSetting> wifiDeviceSettings = wifiDeviceSettingService.findByIds(macs, true, true);

			int index = 0;
			if (wifiDeviceSettings != null) {
				for (int i = 0; i < macs.size(); i++) {
					WifiDeviceSetting wifiDeviceSetting = wifiDeviceSettings.get(i);
					if (wifiDeviceSetting != null) {
						WifiDeviceSettingDTO setting_dto = wifiDeviceSetting.getInnerModel();
						// 信号强度和当前信道
						String[] powerAndRealChannel = DeviceHelper.getURouterDevicePowerAndRealChannel(setting_dto);
						UserDeviceVTO userDeviceVTO = vtos.get(index);
						userDeviceVTO.setD_power(powerAndRealChannel[0]);
						userDeviceVTO.setD_channel(powerAndRealChannel[1]);
					}
					index++;
				}
			}
		}
		return vtos;
	}

	public List<UserDeviceVTO> fetchBindDevicesFromIndex(Integer uid, Integer u_id, String d_online, String s_content,
			int pageNo, int pageSize) {

		int searchPageNo = pageNo >= 1 ? (pageNo - 1) : pageNo;
		SearchConditionMessage sm = WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, d_online, s_content);
		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchByConditionMessage(sm, searchPageNo,
				pageSize);
		// System.out.println("fetchBindDevicesFromIndex === " + search_result);

		List<UserDeviceVTO> vtos = null;
		int total = 0;
		if (search_result != null) {
			total = (int) search_result.getTotalElements();// .getTotal();
			if (total == 0) {
				vtos = Collections.emptyList();
			} else {
				// List<WifiDeviceDocument> searchDocuments =
				// search_result.getContent();//.getResult();
				vtos = builderUserDeviceVTOs(search_result);
			}
		}

		return vtos;

	}

	/**
	 * 通过搜索引擎获取用户绑定的设备
	 * 
	 * @param uid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public UserDeviceTCPageVTO fetchBindDevicesFromIndexCustom(Integer uid, Integer u_id, String d_online,
			String s_content, int pageNo, int pageSize) {

		int searchPageNo = pageNo >= 1 ? (pageNo - 1) : pageNo;
		SearchConditionMessage sm = WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, d_online, s_content);
		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchByConditionMessage(sm, searchPageNo,
				pageSize);
		// System.out.println("fetchBindDevicesFromIndex === " + search_result);

		List<UserDeviceVTO> vtos = null;
		int total = 0;
		if (search_result != null) {
			total = (int) search_result.getTotalElements();// .getTotal();
			if (total == 0) {
				vtos = Collections.emptyList();
			} else {
				// List<WifiDeviceDocument> searchDocuments =
				// search_result.getContent();//.getResult();
				vtos = builderUserDeviceVTOs(search_result);
			}
		}
		TailPage<UserDeviceVTO> pages = new CommonPage<UserDeviceVTO>(pageNo, pageSize, total, vtos);

		// 获取3种在线状态的数量，在线，离线，全部
		UserDeviceStatisticsVTO statistics = new UserDeviceStatisticsVTO();
		OnlineEnum onlineEnum = WifiDeviceDocumentEnumType.OnlineEnum.getOnlineEnumFromType(d_online);
		// 当前搜索条件为全部，则查询在线和离线数量
		long to = 0l;
		long on = 0l;
		long of = 0l;
		if (onlineEnum == null) {
			to = total;
			on = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, OnlineEnum.Online.getType(), null));
			of = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, OnlineEnum.Offline.getType(), null));
		}
		// 当前搜索条件为在线，则查询全部和离线数量
		else if (onlineEnum.getType().equals(OnlineEnum.Online.getType())) {
			on = total;
			to = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, null, null));
			of = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, OnlineEnum.Offline.getType(), null));
		}
		// 当前搜索条件为离线，则查询全部和在线数量
		else if (onlineEnum.getType().equals(OnlineEnum.Offline.getType())) {
			of = total;
			to = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, null, null));
			on = wifiDeviceDataSearchService.searchCountByConditionMessage(
					WifiDeviceTCSearchMessageBuilder.builderSearchTCMessage(u_id, OnlineEnum.Online.getType(), null));
		}
		statistics.setTo(to);
		statistics.setOn(on);
		statistics.setOf(of);

		UserDeviceTCPageVTO vto = new UserDeviceTCPageVTO();
		vto.setPages(pages);
		vto.setStatistics(statistics);
		return vto;
	}

	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * 
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	/*
	 * public RpcResponseDTO<List<UserDeviceDTO>> fetchBindDevicesByAccOrUid(int
	 * countrycode,String acc,int uid) { if(uid <=0 && StringUtils.isEmpty(acc))
	 * return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * COMMON_DATA_PARAM_ERROR); if(uid <=0){ Integer ret_uid =
	 * UniqueFacadeService.fetchUidByMobileno(countrycode,acc); if(ret_uid ==
	 * null || ret_uid.intValue() == 0){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * LOGIN_USER_DATA_NOTEXIST); } uid = ret_uid.intValue(); }
	 * List<UserDeviceDTO> fetchBindDevices =
	 * userDeviceFacadeService.fetchBindDevices(uid); return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(fetchBindDevices); }
	 */

	/**
	 * 通过用户手机号或者指定用户的uid得到其绑定的设备
	 * 
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	/*
	 * public RpcResponseDTO<Boolean> unBindDevicesByAccOrUid(int
	 * countrycode,String acc,int uid) { if(uid <=0 && StringUtils.isEmpty(acc))
	 * return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * COMMON_DATA_PARAM_ERROR); if(uid <=0){ Integer ret_uid =
	 * UniqueFacadeService.fetchUidByMobileno(countrycode,acc); if(ret_uid ==
	 * null || ret_uid.intValue() == 0){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * LOGIN_USER_DATA_NOTEXIST); } uid = ret_uid.intValue(); } List<UserDevice>
	 * userDeviceList = userDeviceService.fetchBindDevicesWithLimit(uid,
	 * WIFI_DEVICE_BIND_LIMIT_NUM); for(UserDevice ud:userDeviceList){
	 * UserDevicePK userDevicePK = ud.getId(); if
	 * (userDeviceService.deleteById(userDevicePK) > 0) {
	 * deliverMessageService.sendUserDeviceDestoryActionMessage(uid,
	 * userDevicePK.getMac()); } } return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE); }
	 */

	// public final static int WIFI_DEVICE_BIND_LIMIT_NUM = 10;

	/*
	 * public RpcResponseDTO<List<DeviceDetailVTO>> userDetail(int
	 * operationUid,int countrycode,String acc,int tid){ if(tid <=0 &&
	 * StringUtils.isEmpty(acc)) return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * COMMON_DATA_PARAM_ERROR); if(tid <=0){ Integer ret_uid =
	 * UniqueFacadeService.fetchUidByMobileno(countrycode,acc); if(ret_uid ==
	 * null || ret_uid.intValue() == 0){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * LOGIN_USER_DATA_NOTEXIST); } tid = ret_uid.intValue(); } List<UserDevice>
	 * userDevices = userDeviceService.fetchBindDevicesWithLimit(tid,
	 * UserDeviceFacadeService.WIFI_DEVICE_BIND_LIMIT_NUM);
	 * 
	 * List<DeviceDetailVTO> resultVTOs = new ArrayList<>(); for(UserDevice
	 * udevice:userDevices){ RpcResponseDTO<DeviceDetailVTO> vto =
	 * this.deviceDetail(operationUid, udevice.getMac()); if(!vto.hasError())
	 * resultVTOs.add(vto.getPayload()); } return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(resultVTOs); }
	 */

	public RpcResponseDTO<DeviceDetailVTO> deviceDetail(int operationUid, String mac) {
		try {
			WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			if (wifiDevice == null) {
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST, new String[] { "mac" });
			}
			WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(mac);
			User user = null;
			// Integer bindUid = userDeviceService.fetchBindUid(mac);
			Integer bindUid = userWifiDeviceFacadeService.findUidById(mac);
			if (bindUid != null) {
				user = userService.getById(bindUid);
			}

			WifiDeviceSharedNetwork wifiDeviceSharedNetwork = sharedNetworksFacadeService.fetchDeviceSharedNetwork(mac);
			// 基础信息
			DeviceBaseVTO dbv = new DeviceBaseVTO();
			dbv.setMac(wifiDevice.getId());
			dbv.setSn(wifiDevice.getSn());
			dbv.setOrig_swver(wifiDevice.getOrig_swver());
			dbv.setVap_module(wifiDeviceModule != null ? wifiDeviceModule.getOrig_vap_module() : StringUtils.EMPTY);
			dbv.setOrig_model(wifiDevice.getOrig_model());
			dbv.setOrig_hdver(wifiDevice.getOrig_hdver());
			dbv.setWork_mode(wifiDevice.getWork_mode());
			dbv.setHdtype(wifiDevice.getHdtype());
			dbv.setChannel_lv1(wifiDevice.getChannel_lv1());
			dbv.setChannel_lv2(wifiDevice.getChannel_lv2());
			DeviceVersion parser = DeviceVersion.parser(wifiDevice.getOrig_swver());
			// String dut = parser.toDeviceUnitTypeIndex();
			dbv.setDut(parser.getSt());
			DeviceUnitType unitType = DeviceUnitType.fromIndex(parser.getSt());
			dbv.setDutn(unitType != null ? unitType.getName() : StringHelper.MINUS_STRING_GAP);
			if (wifiDeviceSharedNetwork != null) {
				dbv.setD_snk_type(wifiDeviceSharedNetwork.getSharednetwork_type());
			}
			// 状态信息
			DevicePresentVTO dpv = new DevicePresentVTO();
			dpv.setMac(wifiDevice.getId());
			dpv.setUid(user != null ? user.getId() : 0);
			dpv.setMobileno(user != null ? user.getMobileno() : StringUtils.EMPTY);
			if (user != null) {
				dpv.setHandsettype(user.getLastlogindevice());
				dpv.setHandsetn(DeviceEnum.getBySName(user.getLastlogindevice()).getName());
			} else {
				dpv.setHandsettype(StringHelper.MINUS_STRING_GAP);
				dpv.setHandsetn(StringHelper.MINUS_STRING_GAP);
			}

			dpv.setAddress(wifiDevice.getFormatted_address());
			dpv.setOnline(wifiDevice.isOnline());
			dpv.setMonline(wifiDeviceModule != null ? wifiDeviceModule.isModule_online() : false);
			if (wifiDevice.getFirst_reged_at() == null) {
				dpv.setFirst_reg_at(StringHelper.MINUS_STRING_GAP);
			} else {
				dpv.setFirst_reg_at(
						DateTimeHelper.formatDate(wifiDevice.getFirst_reged_at(), DateTimeHelper.FormatPattern0));
			}

			if (wifiDevice.getLast_reged_at() != null)
				dpv.setLast_reg_at(
						DateTimeHelper.formatDate(wifiDevice.getLast_reged_at(), DateTimeHelper.FormatPattern0));
			if (wifiDevice.getLast_logout_at() != null)
				dpv.setLast_logout_at(
						DateTimeHelper.formatDate(wifiDevice.getLast_logout_at(), DateTimeHelper.FormatPattern0));
			dpv.setDod(wifiDevice.getUptime());
			
			WifiDeviceSharedealConfigs wifiDeviceShareConfig = wifiDeviceSharedealConfigsService.getById(mac);

            if(wifiDeviceShareConfig != null){
                if (wifiDeviceShareConfig.getDistributor() > 0) {
                	dpv.setA_id((userService.getById(wifiDeviceShareConfig.getDistributor()).getMobileno()));
                }
            }

			// 运营状态信息 灰度、模板
			DeviceOperationVTO dov = new DeviceOperationVTO();
			WifiDeviceGrayVersionPK deviceGray = wifiDeviceGrayFacadeService.determineDeviceGray(wifiDevice.getId(),
					wifiDevice.getOrig_swver());
			dov.setDut(deviceGray.getDuts());
			dov.setGl(deviceGray.getGl());
			dov.setGln(VapEnumType.GrayLevel.fromIndex(deviceGray.getGl()).getName());
			String mstyle = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(wifiDevice.getId());
			if (StringUtils.isNotEmpty(mstyle))
				dov.setMstyle(mstyle);
			else
				dov.setMstyle(StringHelper.MINUS_STRING_GAP);

			// 分成详情
			WifiDeviceSharedealConfigs configs = chargingFacadeService.userfulWifiDeviceSharedealConfigsJust4View(mac);
			DeviceSharedealVTO dsv = new DeviceSharedealVTO();
			dsv.setOwner(configs.getOwner());
			dsv.setDistributor(configs.getDistributor());
			dsv.setDistributor_l2(configs.getDistributor_l2());
			dsv.setMac(configs.getId());
			dsv.setBatchno(configs.getBatchno());
			dsv.setOwner_percent(configs.getOwner_percent());
			dsv.setManufacturer_percent(configs.getManufacturer_percent());
			dsv.setDistributor_percent(configs.getDistributor_percent());
			dsv.setDistributor_l2_percent(configs.getDistributor_l2_percent());
			if(wifiDeviceSharedNetwork != null){
				ParamSharedNetworkDTO pdto = wifiDeviceSharedNetwork.getInnerModel().getPsn();
				if(pdto != null){
					dsv.setRcm(pdto.getRange_cash_mobile());
					dsv.setRcp(pdto.getRange_cash_pc());
					dsv.setAitm(pdto.getAit_mobile());
					dsv.setAitp(pdto.getAit_pc());
					dsv.setFaitm(pdto.getFree_ait_mobile());
					dsv.setFaitp(pdto.getFree_ait_pc());
				}
			}
			dsv.setCanbeturnoff(configs.isCanbe_turnoff());
			dsv.setRuntime_applydefault(configs.isRuntime_applydefault());
			dsv.setCustomized(configs.isCustomized());
			DeviceDetailVTO dvto = new DeviceDetailVTO();
			dvto.setDbv(dbv);
			dvto.setDpv(dpv);
			dvto.setDov(dov);
			dvto.setDsv(dsv);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dvto);
		} catch (BusinessI18nCodeException i18nex) {
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}


	public RpcResponseDTO<DeviceConfigDetailVTO> deviceConfigDetail(String mac) {
		try {
			DeviceConfigDetailVTO ret = new DeviceConfigDetailVTO();
			
			WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			WifiDeviceSetting wifiDeviceSetting = wifiDeviceSettingService.getById(mac);
			if (wifiDevice == null) {
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST, new String[] { "mac" });
			}
			User user = null;
			// Integer bindUid = userDeviceService.fetchBindUid(mac);
			Integer bindUid = userWifiDeviceFacadeService.findUidById(mac);
			if (bindUid != null) {
				user = userService.getById(bindUid);
			}

			WifiDeviceSharedNetwork wifiDeviceSharedNetwork = sharedNetworksFacadeService.fetchDeviceSharedNetwork(mac);
			
			ret.setMac(mac);
			ret.setSn(wifiDevice.getSn());
			ret.setOrig_hdver(wifiDevice.getOrig_hdver());
			ret.setOrig_swver(wifiDevice.getOrig_swver());
			ret.setWork_mode(wifiDevice.getWork_mode());
			ret.setUid((user == null)?0:bindUid);
			ret.setMobileno((user == null)?StringUtils.EMPTY:user.getMobileno());
			ret.setNick((user == null)?StringUtils.EMPTY:user.getNick());
			ret.setOnline(wifiDevice.isOnline());
			ret.setAddress(wifiDevice.getFormatted_address());
			ret.setFirst_reg_at((wifiDevice.getFirst_reged_at()  == null)?StringHelper.MINUS_STRING_GAP:DateTimeHelper.formatDate(wifiDevice.getFirst_reged_at(), DateTimeHelper.FormatPattern0));
			ret.setLast_logout_at((wifiDevice.getLast_logout_at()  == null)?StringHelper.MINUS_STRING_GAP:DateTimeHelper.formatDate(wifiDevice.getLast_logout_at(), DateTimeHelper.FormatPattern0));
			ret.setLast_reg_at((wifiDevice.getLast_reged_at()  == null)?StringHelper.MINUS_STRING_GAP:DateTimeHelper.formatDate(wifiDevice.getLast_reged_at(), DateTimeHelper.FormatPattern0));
			ret.setDod(wifiDevice.getUptime());	
			ret.setIndustry(wifiDevice.getIndustry());
			ret.setMerchant_name(wifiDevice.getMerchant_name());
			
			if(wifiDeviceSetting != null){
				WifiDeviceSettingDTO psn = wifiDeviceSetting.getInnerModel();
				ret.setRf_2in1(psn.getRf_2in1());
				if(psn.getUsers() != null){
					for(WifiDeviceSettingUserDTO duser:psn.getUsers()){
						if(WifiDeviceSettingUserDTO.Admin_Name.equals(duser.getName())){
							if(StringUtils.isEmpty(duser.getPassword()) && StringUtils.isNotEmpty(duser.getPassword_rsa())){
								ret.setAdmin_pwd(JNIRsaHelper.jniRsaDecryptHexStr(duser.getPassword_rsa()));
							} else {
								ret.setAdmin_pwd(duser.getPassword());
							}
							break;
						}
					}
				}
				
				if(psn.getRadios() != null && !psn.getRadios().isEmpty()){
					List<URouterDeviceConfigRadioVTO> radios_vto = new ArrayList<URouterDeviceConfigRadioVTO>();
					URouterDeviceConfigRadioVTO radio_vto = null;
					for (WifiDeviceSettingRadioDTO ra : psn.getRadios()) {
						radio_vto = new URouterDeviceConfigRadioVTO();
						radio_vto.setName(ra.getName());
						radio_vto.setChannel_bandwidth(ra.getChannel_bandwidth());
						radio_vto.setCountry(ra.getCountry());
						radio_vto.setPower(Integer.valueOf(ra.getPower()));
						radio_vto.setReal_channel(Integer.valueOf(ra.getReal_channel()));
						radio_vto.setRf(ra.getRf());
						radios_vto.add(radio_vto);
					}
					ret.setRadios(radios_vto);
				}
				
				if(psn.getVaps() != null && !psn.getVaps().isEmpty()){
					List<URouterDeviceConfigVapVTO> vaps_vto = new ArrayList<URouterDeviceConfigVapVTO>();
					URouterDeviceConfigVapVTO vap_vto = null;
					for (WifiDeviceSettingVapDTO vap : psn.getVaps()) {
						if(vap.getName() == null ||
								(!vap.getName().equals(WifiDeviceSetting.VAPNAME_WLAN0) && !vap.getName().equals(WifiDeviceSetting.VAPNAME_WLAN10)))
							continue;
						vap_vto = new URouterDeviceConfigVapVTO();
						vap_vto.setVap_auth(vap.getAuth());
						vap_vto.setVap_name(vap.getName());
						vap_vto.setVap_ssid(vap.getSsid());
						vap_vto.setVap_pwd(JNIRsaHelper.jniRsaDecryptHexStr(vap.getAuth_key_rsa()));
						vap_vto.setVap_hide_ssid(vap.getHide_ssid());
						vap_vto.setEnable(vap.getEnable());
						vaps_vto.add(vap_vto);
					}
					ret.setVaps(vaps_vto);
				}
				
				if(psn.getInterfaces() != null && !psn.getInterfaces().isEmpty()){
					List<URouterDeviceConfigInterfaceVTO> interface_vtos = new ArrayList<URouterDeviceConfigInterfaceVTO>();
					for (WifiDeviceSettingInterfaceDTO interface_dto : psn.getInterfaces()) {
						if(interface_dto.getName() == null || 
								(!interface_dto.getName().equals(WifiDeviceSetting.VAPNAME_WLAN0) && !interface_dto.getName().equals(WifiDeviceSetting.VAPNAME_WLAN10)))
							continue;
						URouterDeviceConfigInterfaceVTO interface_vto = new URouterDeviceConfigInterfaceVTO();
						interface_vto.setName(interface_dto.getName());
						interface_vto.setEnable(interface_dto.getEnable());
						if (interface_dto.getUsers_tx_rate() != null) {
							interface_vto.setUsers_tx_rate(interface_dto.getUsers_tx_rate().intValue() / 8);// Kbps转KBps
						}
						if (interface_dto.getUsers_rx_rate() != null) {
							interface_vto.setUsers_rx_rate(interface_dto.getUsers_rx_rate().intValue() / 8);// Kbps转KBps
						}
						interface_vtos.add(interface_vto);
					}
					ret.setIfs(interface_vtos);
				}
			}
			
			if(wifiDeviceSharedNetwork != null && wifiDeviceSharedNetwork.getInnerModel() != null){
				SharedNetworkSettingDTO snkdto = wifiDeviceSharedNetwork.getInnerModel();
				ParamSharedNetworkDTO snkpsn = snkdto.getPsn();
				ret.setSnk_on(snkdto.isOn());
				if(snkpsn != null){
					ret.setSnk_type(snkpsn.getNtype());
					ret.setSnk_ssid(snkpsn.getSsid());
					ret.setRemote_auth_url(snkpsn.getRemote_auth_url());
					ret.setUsers_rx_rate(snkpsn.getUsers_rx_rate());
					ret.setUsers_tx_rate(snkpsn.getUsers_tx_rate());
				}
			}

			// 分成详情
			WifiDeviceSharedealConfigs configs = chargingFacadeService.userfulWifiDeviceSharedealConfigsJust4View(mac);
			DeviceSharedealVTO dsv = new DeviceSharedealVTO();
			dsv.setOwner(configs.getOwner());
			dsv.setDistributor(configs.getDistributor());
			dsv.setDistributor_l2(configs.getDistributor_l2());
			dsv.setMac(configs.getId());
			dsv.setBatchno(configs.getBatchno());
			dsv.setOwner_percent(configs.getOwner_percent());
			dsv.setManufacturer_percent(configs.getManufacturer_percent());
			dsv.setDistributor_percent(configs.getDistributor_percent());
			dsv.setDistributor_l2_percent(configs.getDistributor_l2_percent());

			if(wifiDeviceSharedNetwork != null && wifiDeviceSharedNetwork.getInnerModel() != null){
				ParamSharedNetworkDTO pdto = wifiDeviceSharedNetwork.getInnerModel().getPsn();
				if(pdto != null){
					dsv.setRcm(pdto.getRange_cash_mobile());
					dsv.setRcp(pdto.getRange_cash_pc());
					dsv.setAitm(pdto.getAit_mobile());
					dsv.setAitp(pdto.getAit_pc());
					dsv.setFaitm(pdto.getFree_ait_mobile());
					dsv.setFaitp(pdto.getFree_ait_pc());
				}
			}
			dsv.setCanbeturnoff(configs.isCanbe_turnoff());
			dsv.setRuntime_applydefault(configs.isRuntime_applydefault());
			dsv.setCustomized(configs.isCustomized());
			
			ret.setDsv(dsv);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		} catch (BusinessI18nCodeException i18nex) {
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	/**
	 * 根据用户uid获取绑定的设备分页数据
	 * 
	 * @param uid
	 *            用户uid
	 * @param dut
	 *            设备业务线
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserDeviceCloudDTO>> devicePagesByUid(Integer uid, String dut, int pageNo,
			int pageSize) {
		try {
			int searchPageNo = pageNo >= 1 ? (pageNo - 1) : pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageByUidAndDut(uid, dut,
					searchPageNo, pageSize);

			List<UserDeviceCloudDTO> vtos = null;
			int total = 0;
			if (search_result != null) {
				total = (int) search_result.getTotalElements();// .getTotal();
				if (total == 0) {
					vtos = Collections.emptyList();
				} else {
					List<WifiDeviceDocument> searchDocuments = search_result.getContent();// .getResult();
					if (searchDocuments.isEmpty()) {
						vtos = Collections.emptyList();
					} else {
						List<String> macs = WifiDeviceDocumentHelper.generateDocumentIds(searchDocuments);
						List<Object> ohd_counts = WifiDeviceHandsetUnitPresentSortedSetService.getInstance()
								.presentOnlineSizes(macs);
						List<String> d_linkmodes = WifiDeviceModeStatusService.getInstance().getPresents(macs);
						UserDeviceCloudDTO vto = null;
						vtos = new ArrayList<UserDeviceCloudDTO>();
						int cursor = 0;
						for (WifiDeviceDocument wifiDeviceDocument : searchDocuments) {
							vto = new UserDeviceCloudDTO();
							vto.setD_mac(wifiDeviceDocument.getId());
							vto.setD_name(wifiDeviceDocument.getU_dnick());
							vto.setD_type(wifiDeviceDocument.getD_type());
							vto.setD_online(wifiDeviceDocument.getD_online());
							vto.setD_origmodel(wifiDeviceDocument.getD_origmodel());
							vto.setD_workmode(wifiDeviceDocument.getD_workmodel());
							vto.setD_origswver(wifiDeviceDocument.getD_origswver());
							vto.setD_dut(wifiDeviceDocument.getD_dut());
							vto.setD_snk_type(wifiDeviceDocument.getD_snk_type());
							vto.setD_sn(wifiDeviceDocument.getD_sn());
							vto.setD_address(wifiDeviceDocument.getD_address());
							if (wifiDeviceDocument.getD_snk_allowturnoff() != null) {
								vto.setD_snk_allowturnoff(Integer.parseInt(wifiDeviceDocument.getD_snk_allowturnoff()));
							} else {
								vto.setD_snk_allowturnoff(1);
							}
							String u_id = wifiDeviceDocument.getU_id();
							if (StringUtils.isNotEmpty(u_id)) {
								vto.setUid(Integer.parseInt(u_id));
							}
							// long ohd_count =
							// WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiDeviceDocument.getId());
							if (ohd_counts != null) {
								Object ohd_count_obj = ohd_counts.get(cursor);
								if (ohd_count_obj != null) {
									vto.setOhd_count((Long) ohd_count_obj);
								}
							}
							if (d_linkmodes != null) {
								String d_linkmode = d_linkmodes.get(cursor);
								if (StringUtils.isNotEmpty(d_linkmode)) {
									WifiDeviceSettingLinkModeDTO mode_dto = JsonHelper.getDTO(d_linkmode,
											WifiDeviceSettingLinkModeDTO.class);
									if (mode_dto != null) {
										vto.setLink_mode_type(DeviceHelper.getDeviceMode(mode_dto.getModel()));
									}
								}
							}
							vtos.add(vto);
							cursor++;
						}
					}
				}
			} else {
				vtos = Collections.emptyList();
			}
			TailPage<UserDeviceCloudDTO> returnRet = new CommonPage<UserDeviceCloudDTO>(pageNo, pageSize, total, vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		} catch (BusinessI18nCodeException i18nex) {
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

    public RpcResponseDTO<Boolean> updateDeviceLocation(int uid, String mac, String country, String province, String city, 
    		String district, String street, String faddress, String lon, String lat){
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if (userWifiDevice != null && !userWifiDevice.getUid().equals(uid)) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_ALREADY_BEBINDED_OTHER,
					Boolean.FALSE);
		}
		
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
		if(wifiDevice == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,
					Boolean.FALSE);
		}

		wifiDevice.setCountry(country);
		wifiDevice.setProvince(province);
		wifiDevice.setCity(city);
		wifiDevice.setDistrict(district);
		wifiDevice.setStreet(street);
		wifiDevice.setFormatted_address(faddress);
		wifiDevice.setLon(lon);
		wifiDevice.setLat(lat);
		wifiDevice.setLoc_method(WifiDeviceHelper.Device_Location_By_APP);
		
		wifiDeviceService.update(wifiDevice);
		wifiDeviceIndexIncrementProcesser.locaitionUpdIncrement(mac, Double.parseDouble(lat),
				Double.parseDouble(lon), faddress ,province ,city ,district);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}

	
}
