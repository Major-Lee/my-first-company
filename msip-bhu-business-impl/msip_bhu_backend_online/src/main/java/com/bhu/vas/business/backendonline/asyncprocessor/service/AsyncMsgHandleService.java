package com.bhu.vas.business.backendonline.asyncprocessor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.baidumap.GeoPoiExtensionDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceVisitorAuthorizeOnlinePushDTO;
import com.bhu.vas.api.dto.push.UserBBSsignedonPushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceRebootPushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceSettingChangedPushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceWorkModeChangedDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingMMDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingSyskeyDTO;
import com.bhu.vas.api.dto.statistics.DeviceStatistics;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.ExchangeBBSHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.asyn.spring.model.CMUPWithWifiDeviceOnlinesDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingAclMacsDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingAliasDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingVapDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceVisitorAuthorizeOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.UserBBSsignedonDTO;
import com.bhu.vas.business.asyn.spring.model.UserCaptchaCodeFetchDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceDestoryDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceForceBindDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceRegisterDTO;
import com.bhu.vas.business.asyn.spring.model.UserRegisteredDTO;
import com.bhu.vas.business.asyn.spring.model.UserSignedonDTO;
import com.bhu.vas.business.asyn.spring.model.WifiCmdsNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceLocationDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceModuleOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingQueryDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDevicesGrayChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDevicesModuleStyleChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiMultiCmdsNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiRealtimeRateFetchDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessCacheService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceUpgradeFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementProcesser;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.bhu.vas.push.business.PushService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingPoiRespDTO;
import com.smartwork.msip.cores.helper.ip.IpLookup;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
public class AsyncMsgHandleService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	@Resource
	private DeviceUpgradeFacadeService deviceUpgradeFacadeService;

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private WifiDeviceIndexIncrementProcesser wifiDeviceIndexIncrementProcesser;
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	//@Resource
	//private UserSettingStateService userSettingStateService;
	
	@Resource
	private PushService pushService;
	
	@Resource
	private BusinessCacheService businessCacheService;

	@Resource
	private AgentDeviceClaimService agentDeviceClaimService;

	@Resource
	private UserDeviceService userDeviceService;

	@Resource
	private UserService userService;

	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	/**
	 * wifi设备上线
	 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
	 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 5:统计增量 wifi设备的daily启动次数增量 (backend)
	 * 6:wifi设备增量索引
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceOnlineHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOnlineHandle message[%s]", message));
		WifiDeviceOnlineDTO dto = JsonHelper.getDTO(message, WifiDeviceOnlineDTO.class);
		WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
		if(wifiDevice != null){
			boolean isURouter = WifiDeviceHelper.isURouterDevice(wifiDevice.getOrig_swver());//wifiDevice.getOrig_model());
			//boolean needWiffsniffer = false;
			List<String> payloads = new ArrayList<String>();
			//boolean forceFirmwareUpdate = false;
			if(isURouter){
				//对uRouter设备才下发管理参数触发设备自动上报用户通知并同步终端，其他设备不下发此指令（其他设备通过下发查询在线终端列表指令获取数据）
				payloads.add(CMDBuilder.builderDeviceOnlineTeminalQuery(dto.getMac()));
				//获取设备系统信息
				payloads.add(CMDBuilder.builderSysinfoQuery(dto.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
				//用户登录后 给其绑定的设备mac地址发送设备使用情况
				boolean needDeviceUsedQuery = BusinessMarkerService.getInstance().needNewRequestAndMarker(dto.getMac(),false);
				if(needDeviceUsedQuery)
					payloads.add(CMDBuilder.builderDeviceUsedStatusQuery(dto.getMac()));
				//设备上线push
				if(WifiDeviceDTO.UserCmdRebootReason.equals(dto.getJoin_reason())){
					pushService.push(new WifiDeviceRebootPushDTO(dto.getMac(), dto.getJoin_reason()));
				}
				UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(dto.getMac(), wifiDevice);
				if(upgrade != null && upgrade.isForceDeviceUpgrade()){
					//如果是指定的版本出厂版本 并且 第一次注册创建时间超过指定定义的天数,立刻升级
					if(BusinessRuntimeConfiguration.isInitialDeviceFirmwareVersion(upgrade.getCurrentDVB()) 
							&& !DateTimeHelper.isTimeDaysRecent(wifiDevice.getCreated_at().getTime(), BusinessRuntimeConfiguration.Device_Firmware_ForceUpdateImmediately_AfterDays)){
						payloads.add(upgrade.buildUpgradeCMD(dto.getMac(), 0, StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP));
					}else{
						payloads.add(upgrade.buildUpgradeCMD(dto.getMac(), 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime));
					}
				}
				//added by Edmond Lee @20160106 for mark workmode changed of device
				if(!dto.isNewWifi()){
					//workmode切换只支持uRouter，并且只能在router-ap和bridge-ap之间互相切换
					//判定workmode是否变更
					if(StringUtils.isNotEmpty(dto.getO_wmode()) && StringUtils.isNotEmpty(dto.getN_wmode())){
						if(!dto.getO_wmode().equals(dto.getN_wmode())){
							pushService.push(new WifiDeviceWorkModeChangedDTO(dto.getMac(), wifiDevice.getWork_mode()));
						}
					}
				}
			}
			
			{//开启共享网络判定，并更新索引
				if(BusinessRuntimeConfiguration.Device_SharedNetwork_Default_Start){
					logger.info(String.format("Device SharedNetwork Option[%s]", BusinessRuntimeConfiguration.Device_SharedNetwork_Default_Start));
					if(WifiDeviceHelper.autoDeviceSecureSharedNetworkStrategy(wifiDevice.getOrig_swver())){
						SharedNetworkSettingDTO sharedNetwork = sharedNetworksFacadeService.fetchDeviceSharedNetworkConfWhenEmptyThenCreate(dto.getMac());
						ParamSharedNetworkDTO psn = sharedNetwork.getPsn();
						if(sharedNetwork != null && sharedNetwork.isOn() && psn != null){
							logger.info(String.format("Device SharedNetwork Model[%s]", JsonHelper.getJSONString(psn)));
							//更新索引，下发指令
							wifiDeviceIndexIncrementService.sharedNetworkUpdIncrement(dto.getMac(), psn.getNtype());
							psn.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
							//生成下发指令
							String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, 
									dto.getMac(), -1,JsonHelper.getJSONString(psn),deviceCMDGenFacadeService);
							payloads.add(sharedNetworkCMD);
						}
					}
				}
			}
			
			afterDeviceOnlineThenCmdDown(dto.getMac(),dto.isNeedLocationQuery(),payloads);
			
			boolean needUpdate = false;
			boolean needClaim = wifiDevice.needClaim();
			try{
				//设备上线后认领
				if(needClaim){
					DeviceVersion parser = DeviceVersion.parser(wifiDevice.getOrig_swver());
					AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
					int claim_ret = agentDeviceClaimService.claimAgentDevice(agentDeviceClaim, wifiDevice.getId(), parser.toDeviceUnitTypeIndex());
					if(claim_ret != 0){//-1 or >0
						wifiDevice.setAgentuser(claim_ret);
						needUpdate = true;
						//如果认领存在代理商信息 增量设备代理商信息索引
						if(wifiDevice.getAgentuser() > 0){
							User agentUser = userService.getById(wifiDevice.getAgentuser());
							wifiDeviceIndexIncrementProcesser.agentUpdIncrement(wifiDevice.getId(),
									agentDeviceClaim.getImport_id(), agentUser);
						}
					}
				}
				//根据wan_ip获取设备的网络运营商信息
				if(dto.isWanIpChanged() && StringUtils.isNotEmpty(wifiDevice.getWan_ip())){
					String carrier = IpLookup.lookup_carrier(wifiDevice.getWan_ip());
					if(!StringUtils.isEmpty(carrier) && !carrier.equals(wifiDevice.getCarrier())){
						//可能会引起设备在线状态数据不正常 不过考虑到只有网络运营商发生变化才会更新 可以忽略不计
						wifiDevice.setCarrier(carrier);
						needUpdate = true;
						//wifiDeviceService.update(wifiDevice);
					}
				}
				if(needUpdate)
					wifiDeviceService.update(wifiDevice);
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
			//设备统计
			deviceFacadeService.deviceStatisticsOnline(new DeviceStatistics(dto.getMac(), dto.isNewWifi(), 
					new Date(dto.getLast_login_at())), DeviceStatistics.Statis_Device_Type);
		}

		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOnlineHandle message[%s] successful", message));
	}
	
	public void wifiDeviceModuleOnlineHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceModuleOnlineHandle message[%s]", message));
		WifiDeviceModuleOnlineDTO dto = JsonHelper.getDTO(message, WifiDeviceModuleOnlineDTO.class);
		WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
		if(wifiDevice != null){
			{//组件模块升级指令
				UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceOMUpgrade(dto.getMac(),wifiDevice.getOrig_swver(), dto.getOrig_vap_module());
				if(upgrade != null && upgrade.isForceDeviceUpgrade() && WifiDeviceHelper.WIFI_DEVICE_UPGRADE_OM == upgrade.isFw()){
					String cmd = upgrade.buildUpgradeCMD(dto.getMac(), 0l, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
					afterDeviceModuleOnlineThenCmdDown(dto.getMac(),cmd);
				}
			}
			//wifiDeviceIndexIncrementService.wifiDeviceIndexIncrement(wifiDevice);
			//wifiDeviceIndexIncrementProcesser.moduleOnlineUpdIncrement(wifiDevice.getId(), dto.getOrig_vap_module());
		}
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceModuleOnlineHandle message[%s] successful", message));
	}
	
	//下发获取配置，获取设备测速，地理位置
	public void afterDeviceOnlineThenCmdDown(String mac,boolean needLocationQuery,List<String> payloads){
		logger.info(String.format("wifiDeviceOnlineHandle afterDeviceOnlineThenCmdDown[%s]", mac));
		DaemonHelper.afterDeviceOnline(mac, needLocationQuery,payloads, daemonRpcService);
		/*//设备持久指令分发
		try{
			List<String> persistencePayloads = deviceFacadeService.fetchWifiDevicePersistenceCMD(mac);
			if(persistencePayloads != null && !persistencePayloads.isEmpty()){
				DaemonHelper.daemonCmdsDown(mac,persistencePayloads,daemonRpcService);
				System.out.println("~~~~~~~~~~~~~~~:persistencePayloads "+persistencePayloads.size());
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}*/
		
		logger.info(String.format("wifiDeviceOnlineHandle afterDeviceOnlineThenCmdDown message[%s] successful", mac));
	}
	
	public void afterDeviceModuleOnlineThenCmdDown(String mac,String cmd){
		logger.info(String.format("afterDeviceModuleOnlineThenCmdDown [%s]", mac));
		DaemonHelper.daemonCmdDown(mac, cmd, daemonRpcService);
		/*//设备持久指令分发
		try{
			List<String> persistencePayloads = deviceFacadeService.fetchWifiDevicePersistenceCMD(mac);
			if(persistencePayloads != null && !persistencePayloads.isEmpty()){
				DaemonHelper.daemonCmdsDown(mac,persistencePayloads,daemonRpcService);
				System.out.println("~~~~~~~~~~~~~~~:persistencePayloads "+persistencePayloads.size());
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}*/
		
		logger.info(String.format("wifiDeviceOnlineHandle afterDeviceOnlineThenCmdDown message[%s] successful", mac));
	}
	
	/**
	 *  a:如果设备是新上线的
	 * 	1：wifi设备基础信息更新(backend)
	 * 	2：wifi设备在线状态Redis更新(backend)
	 * 	3:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 	4:统计增量 wifi设备的daily启动次数增量(backend)
	 * b:如果设备本身是在线的
	 * 	do nothing
	 *  5:增量索引
	 * @throws Exception 
	 */
	public void cmupWithWifiDeviceOnlinesHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s]", message));
		
		CMUPWithWifiDeviceOnlinesDTO cmupWithOnlinesDto = JsonHelper.getDTO(message, CMUPWithWifiDeviceOnlinesDTO.class);
		List<WifiDeviceDTO> dtos = cmupWithOnlinesDto.getDevices();
		if(dtos != null && !dtos.isEmpty()){
			List<List<WifiDeviceDTO>> split_dtos = ArrayHelper.splitList(dtos);
			for(List<WifiDeviceDTO> split_item_dtos : split_dtos){
				cmupWithWifiDeviceOnlinesSplitAction(cmupWithOnlinesDto.getCtx(), split_item_dtos);
			}
		}
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s] successful", message));
	}
	
	/**
	 * 设备同步的split action
	 * @param ctx
	 * @param split_item_dtos
	 * @throws Exception
	 */
	public void cmupWithWifiDeviceOnlinesSplitAction(String ctx, List<WifiDeviceDTO> split_item_dtos) throws Exception{
		List<String> ids = new ArrayList<String>();
		for(WifiDeviceDTO dto : split_item_dtos){
			ids.add(dto.getMac().toLowerCase());
		}
		List<WifiDevice> entitys = wifiDeviceService.findByIds(ids, true, true);
		//新上线的设备列表(非新注册)
		List<WifiDevice> entityNewOnlines = new ArrayList<WifiDevice>();
		//新上线的并且是新注册的设备列表
		List<WifiDevice> entityNewRegisters = new ArrayList<WifiDevice>();
		//本次sync中本身未在线的设备
		List<String> idsSyncRegeds = new ArrayList<String>();
		//设备统计模型
		List<DeviceStatistics> ds = new ArrayList<DeviceStatistics>();
		
		int cursor = 0;
		for(WifiDevice entity : entitys){
			if(entity != null && entity.isOnline()){
				continue;
			}
			WifiDeviceDTO dto = split_item_dtos.get(cursor);
			if(entity == null){
				ds.add(new DeviceStatistics(dto.getMac(), true));
				entityNewRegisters.add(BusinessModelBuilder.wifiDeviceDtoToEntity(dto));
			}else{
				ds.add(new DeviceStatistics(dto.getMac(), entity.getLast_reged_at()));
				
				BeanUtils.copyProperties(dto, entity);
				entity.setLast_reged_at(new Date());
				entity.setOnline(true);
				entityNewOnlines.add(entity);
			}
			idsSyncRegeds.add(dto.getMac().toLowerCase());
			cursor++;
		}
		
		//有新上线的设备(非新注册)
		if(!entityNewOnlines.isEmpty()){
			//1：wifi设备基础信息更新
			wifiDeviceService.updateAll(entityNewOnlines);
		}
		//新上线的并且是新注册的设备列表
		if(!entityNewRegisters.isEmpty()){
			//1：wifi设备基础信息更新
			wifiDeviceService.insertAll(entityNewRegisters);
		}
		
		//2:wifi设备在线状态Redis更新
		if(!idsSyncRegeds.isEmpty()){
			WifiDevicePresentCtxService.getInstance().addPresents(idsSyncRegeds, ctx);
		}
		//5:增量索引
		//wifiDeviceIndexIncrementService.cmupWithWifiDeviceOnlinesIndexIncrement(entitys);
		wifiDeviceIndexIncrementProcesser.onlineMultiUpsertIncrement(entitys);
		//设备统计
		deviceFacadeService.deviceStatisticsOnlines(ds, DeviceStatistics.Statis_Device_Type);
	}
	
	
	/**
	 *  a:如果设备是新上线的
	 * 	1：wifi设备基础信息更新(backend)
	 * 	2：wifi设备在线状态Redis更新(backend)
	 * 	3:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 	4:统计增量 wifi设备的daily启动次数增量(backend)
	 * b:如果设备本身是在线的
	 * 	do nothing
	 *  5:增量索引
	 * @throws Exception 
	 */
	/*
	public void cmupWithWifiDeviceOnlinesHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s]", message));
		
		CMUPWithWifiDeviceOnlinesDTO cmupWithOnlinesDto = JsonHelper.getDTO(message, CMUPWithWifiDeviceOnlinesDTO.class);
		List<WifiDeviceDTO> dtos = cmupWithOnlinesDto.getDevices();
		if(dtos != null && !dtos.isEmpty()){
			List<String> ids = new ArrayList<String>();
			for(WifiDeviceDTO dto : dtos){
				ids.add(dto.getMac().toLowerCase());
			}
			//List<WifiDevice> entitys = wifiDeviceService.findByIdsAndOnline(ids, false);
			List<WifiDevice> entitys = wifiDeviceService.findByIds(ids, true, true);
			//新上线的设备列表(非新注册)
			List<WifiDevice> entityNewOnlines = new ArrayList<WifiDevice>();
			//新上线的并且是新注册的设备列表
			List<WifiDevice> entityNewRegisters = new ArrayList<WifiDevice>();
			//不在线的设备的ids
			List<String> offline_ids = new ArrayList<String>();
			int cursor = 0;
			for(WifiDevice entity : entitys){
				if(entity != null && entity.isOnline()){
					continue;
				}
				WifiDeviceDTO dto = dtos.get(cursor);
				if(entity == null){
					entityNewRegisters.add(BusinessModelBuilder.wifiDeviceDtoToEntity(dto));
				}else{
					BeanUtils.copyProperties(dto, entity);
					entityNewOnlines.add(entity);
				}
				offline_ids.add(dto.getMac().toLowerCase());
				cursor++;
			}
			//3:统计增量 wifi设备的daily新增设备增量
			int incr_statistics_news = 0;
			//3:统计增量 wifi设备的daily活跃设备增量
			int incr_statistics_active = 0;
			//4:统计增量 wifi设备的daily启动次数增量
			int incr_statistics_accesscount = 0;
			//有新上线的设备(非新注册)
			int newOnline_length = entityNewOnlines.size();
			if(newOnline_length > 0){
				incr_statistics_accesscount += newOnline_length;
				//今天的时间
				Date today = new Date();
				for(WifiDevice entity: entityNewOnlines){
					//如果最后的登录时间和今天不一样，说明今天是第一次登录
					if(!DateTimeHelper.isSameDay(entity.getLast_reged_at(), today)){
						incr_statistics_active++;
					}
					entity.setLast_reged_at(new Date());
					entity.setOnline(true);
				}
				//1：wifi设备基础信息更新
				wifiDeviceService.updateAll(entityNewOnlines);
			}
			//新上线的并且是新注册的设备列表
			int newRegister_length = entityNewRegisters.size();
			if(newRegister_length > 0){
				incr_statistics_accesscount += newRegister_length;
				incr_statistics_news += newRegister_length;
				//1：wifi设备基础信息更新
				wifiDeviceService.insertAll(entityNewRegisters);
			}
			
			//2:wifi设备在线状态Redis更新
			if(!offline_ids.isEmpty()){
				WifiDevicePresentService.getInstance().addPresents(offline_ids, cmupWithOnlinesDto.getCtx());
			}
			
			//3:统计增量 wifi设备的daily新增设备增量
			if(incr_statistics_news > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_News, incr_statistics_news);
			}
			//3:统计增量 wifi设备的daily活跃设备增量
			if(incr_statistics_active > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Actives, incr_statistics_active);
			}
			//4:统计增量 wifi设备的daily启动次数增量
			if(incr_statistics_accesscount > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, incr_statistics_accesscount);
			}
			//5:增量索引
			wifiDeviceIndexIncrementService.cmupWithWifiDeviceOnlinesIndexIncrement(entitys);
		}
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s] successful", message));
	}
	*/
	
	/**
	 * wifi设备下线
	 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
	 * 4:wifi设备对应handset在线列表redis清除 (backend)
	 * 5:统计增量 wifi设备的daily访问时长增量 (backend)
	 * 6:增量索引
	 * 7:清除已经下发给设备的未完成的任务状态
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceOfflineHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOfflineHandle message[%s]", message));
		
		WifiDeviceOfflineDTO dto = JsonHelper.getDTO(message, WifiDeviceOfflineDTO.class);
		//清除设备查询的实时速率的mark waiting标记 以便可以再次下发查询速率指令
		WifiDeviceRealtimeRateStatisticsStringService.getInstance().clearWaiting(dto.getMac());
		
		WifiDevice entity = wifiDeviceService.getById(dto.getMac());
		if(entity != null){
			//3:wifi上的移动设备基础信息表的在线状态更新
			//deviceFacadeService.allHandsetDoOfflines(dto.getMac());
			//3:wifi上的移动设备基础信息表的在线状态更新,返回在线设备记录，继续更新终端离线状态
			String wifiId = dto.getMac();
			{//修改为redis实现终端上下线日志 2015-12-11
				//wifiHandsetDeviceRelationMService.wifiDeviceIllegalOfflineAdapter(wifiId,deviceFacadeService.allHandsetDoOfflines(wifiId));
				HandsetStorageFacadeService.wifiDeviceIllegalOffline(wifiId, deviceFacadeService.allHandsetDoOfflines(wifiId));
			}
			//5:统计增量 wifi设备的daily访问时长增量
/*			if(dto.getLast_login_at() > 0){
				long uptime = dto.getTs() - dto.getLast_login_at();
				if(uptime > 0){
					deviceFacadeService.deviceStatisticsOffline(uptime, DeviceStatistics.Statis_Device_Type);
					
					String entity_uptime = entity.getUptime();
					BigInteger bi = new BigInteger(String.valueOf(uptime));
					BigInteger entity_bi = null;
					if(!StringUtils.isEmpty(entity_uptime)){
						entity_bi = new BigInteger(entity_uptime);
						bi = bi.add(entity_bi);
					}
					entity.setUptime(bi.toString());
					wifiDeviceService.update(entity);
				}
			}*/


			//7:清除已经下发给设备的未完成的任务状态
			taskFacadeService.taskStateFailByDevice(dto.getMac());
			
			//6:增量索引
			//wifiDeviceIndexIncrementService.wifiDeviceIndexIncrement(entity);
			//wifiDeviceIndexIncrementProcesser.offlineUpdIncrement(wifiId, entity.getUptime(), entity.getLast_logout_at().getTime());

		}
		
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOfflineHandle message[%s] successful", message));
	}
	
	/**
	 * 移动设备上线
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend) removed
	 * 5:wifi设备接入移动设备的接入数量增量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * @param message
	 */
	public void handsetDeviceOnlineHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle message[%s]", message));
		HandsetDeviceOnlineDTO dto = JsonHelper.getDTO(message, HandsetDeviceOnlineDTO.class);
		/*//3:移动设备连接wifi设备的接入记录(非流水)
		int result_status = wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(),new Date(dto.getLogin_ts()));
		//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
		if(result_status == WifiHandsetDeviceRelationMService.AddRelation_Insert){
			//5:wifi设备接入移动设备的接入数量增量
			wifiHandsetDeviceLoginCountMService.incrCount(dto.getWifiId());
		}*/
		//3:移动设备连接wifi设备的接入记录(非流水)
		//修改为redis实现终端上下线日志 2015-12-11
		//移植到device组件中实现 edmond 20160121
		//int result_status = HandsetStorageFacadeService.wifiDeviceHandsetOnline(dto.getWifiId(), dto.getMac(), dto.getLogin_ts());//wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(),new Date(dto.getLogin_ts()));
		/*//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
		if(result_status == HandsetLogDTO.Element_NewHandset){
			//5:wifi设备接入移动设备的接入数量增量
			wifiHandsetDeviceLoginCountMService.incrCount(dto.getWifiId());
		}*/
		//4:移动设备连接wifi设备的流水log	增值平台首页面 此部分数据统计暂时移除 edmond 20160121
		//BusinessWifiHandsetRelationFlowLogger.doFlowMessageLog(dto.getWifiId(), dto.getMac(), dto.getLogin_ts());
		//终端统计 由于去除了增值平台首页面 此部分数据统计暂时移除 edmond 20160121
		//deviceFacadeService.deviceStatisticsOnline(new DeviceStatistics(dto.getMac(), dto.isNewHandset(), new Date(dto.getLast_login_at())),
		//		DeviceStatistics.Statis_HandsetDevice_Type);
		
		//如果是urouter设备 才会发push
		if(deviceFacadeService.isURouterDevice(dto.getWifiId())){
			boolean terminal_notify_push_mark = businessCacheService.getQTerminalPushNotifyCacheByQ(dto.getWifiId(), dto.getMac());
			if(!terminal_notify_push_mark){
				logger.info("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle do Push");
				businessCacheService.storeQTerminalPushNotifyCacheResult(dto.getWifiId(), dto.getMac());
				
				HandsetDeviceOnlinePushDTO pushDto = new HandsetDeviceOnlinePushDTO();
				pushDto.setMac(dto.getWifiId());
				pushDto.setHd_mac(dto.getMac());
				pushDto.setTs(System.currentTimeMillis());
				//if(result_status == HandsetLogDTO.Element_NewHandset)
				if(dto.isNh4t())
					pushDto.setNewed(true);
				boolean push_successed = pushService.push(pushDto);
				if(push_successed){
					logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle push mac[%s] hd_mac[%s] result[%s] ", 
							dto.getWifiId(), dto.getMac(), push_successed));
					//businessCacheService.storeQTerminalPushNotifyCacheResult(dto.getWifiId(), dto.getMac());
				}
			}else{
				logger.info("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle push has mark");
			}

		}
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle message[%s] successful", message));
	}
	
	/**
	 * 移动设备下线
	 * 3:统计增量 移动设备的daily访问时长增量 (backend) removed
	 * @param message
	 */
	public void handsetDeviceOfflineHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOfflineHandle message[%s]", message));
		//HandsetDeviceOfflineDTO dto = JsonHelper.getDTO(message, HandsetDeviceOfflineDTO.class);
		//修改为redis实现终端上下线日志 2015-12-11
		//wifiHandsetDeviceRelationMService.offlineWifiHandsetDeviceItems(dto.getWifiId(), dto.getMac(), dto.getTx_bytes(), dto.getTs());
		//HandsetStorageFacadeService.wifiDeviceHandsetOffline(dto.getWifiId(), dto.getMac(), dto.getTx_bytes(), dto.getTs());
		
		//3:统计增量 移动设备的daily访问时长增量 增值平台首页面 此部分数据统计暂时移除 edmond 20160121
		/*if(!StringUtils.isEmpty(dto.getUptime())){
			deviceFacadeService.deviceStatisticsOffline(Long.parseLong(dto.getUptime()), DeviceStatistics.Statis_HandsetDevice_Type);
		}*/
		
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOfflineHandle message[%s] successful", message));
	}


	/**
	 * 设备上线后，sync会同步在线的终端，会通知终端上线。
	 *
	 * 设备下线的时候，会通知终端下线；
	 *
	 * @param message
	 */
	public void handsetDeviceSyncHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s]", message));
		/*HandsetDeviceSyncDTO syncDto = JsonHelper.getDTO(message, HandsetDeviceSyncDTO.class);

		for (HandsetDeviceDTO dto : syncDto.getDtos()) {
			//修改为redis实现终端上下线日志 2015-12-11
			//wifiHandsetDeviceRelationMService.addRelation(syncDto.getMac(), dto.getMac(),new Date(dto.getTs()));
			HandsetStorageFacadeService.wifiDeviceHandsetOnline(syncDto.getMac(), dto.getMac(), dto.getTs());//wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(),new Date(dto.getLogin_ts()));
		}*/
	}


	/**
	 * 访客终端认证通过
	 * @param message
	 */
	public void handsetDeviceVisitorAuthorizeOnline(String message) {

		HandsetDeviceVisitorAuthorizeOnlineDTO dto = JsonHelper.getDTO(message, HandsetDeviceVisitorAuthorizeOnlineDTO.class);
		if(deviceFacadeService.isURouterDevice(dto.getWifiId())){

			logger.info("AnsyncMsgBackendProcessor handsetDeviceVisitorAuthorizeOnline do Push");

			HandsetDeviceVisitorAuthorizeOnlinePushDTO pushDto = new HandsetDeviceVisitorAuthorizeOnlinePushDTO();
			pushDto.setMac(dto.getWifiId());
			pushDto.setHd_mac(dto.getMac());
			pushDto.setTs(System.currentTimeMillis());
			boolean push_successed = pushService.push(pushDto);
			if(push_successed){
				logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceVisitorAuthorizeOnline push mac[%s] hd_mac[%s] result[%s] ",
						dto.getWifiId(), dto.getMac(), push_successed));
//				businessCacheService.storeQTerminalPushNotifyCacheResult(dto.getWifiId(), dto.getMac());
			}

		}
	}


	/**
	 * 移动设备连接状态sync
	 * 1:清除wifi设备对应handset在线列表redis 并重新写入 (backend)
	 * 2:移动设备基础信息更新 (backend)
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend)
	 * 5:wifi设备接入移动设备的接入数量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * @param message
	 * modified by Edmond Lee for handset storage
	 */
/*	public void handsetDeviceSyncHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s]", message));
		
		HandsetDeviceSyncDTO sync_dto = JsonHelper.getDTO(message, HandsetDeviceSyncDTO.class);
		String wifiId = sync_dto.getMac();
		if(!StringUtils.isEmpty(wifiId)){
			
			List<HandsetDeviceDTO> dtos = sync_dto.getDtos();
			if(dtos != null && !dtos.isEmpty()){
				List<String> ids = new ArrayList<String>();
				for(HandsetDeviceDTO dto : dtos){
					ids.add(dto.getMac().toLowerCase());
				}
				//终端统计模型
				List<DeviceStatistics> ds = new ArrayList<DeviceStatistics>();
				
				List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(ids);
				int cursor = 0;
				for(HandsetDeviceDTO handset : handsets){
					if(handset != null && handset.wasOnline()){
						continue;
					}
					HandsetDeviceDTO dto = dtos.get(cursor);
					if(handset == null){
						ds.add(new DeviceStatistics(dto.getMac(), true));
						//entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
						//handset.setLast_wifi_id(wifiId);
						//entityNewRegisters.add(entity);
					}else{
						ds.add(new DeviceStatistics(dto.getMac(), new Date(handset.getTs())));
						dto.setDhcp_name(handset.getDhcp_name());
						dto.setData_tx_rate(handset.getData_tx_rate());
						dto.setData_rx_rate(handset.getData_rx_rate());
						BeanUtils.copyProperties(dto, entity, HandsetDeviceDTO.copyIgnoreProperties);
						if(!StringUtils.isEmpty(dto.getDhcp_name())){
							entity.setHostname(dto.getDhcp_name());
						}
						entity.setLast_login_at(new Date());
						entity.setLast_wifi_id(wifiId);
						entity.setOnline(true);
						entityNewOnlines.add(entity);
					}
					String handsetId = dto.getMac().toLowerCase();
					//1:wifi设备对应handset在线列表redis 重新写入
					//WifiDeviceHandsetPresentSortedSetService.getInstance().addPresent(wifiId, handsetId, sync_dto.getTs());
					//long rx_rate = StringUtils.isEmpty(entity.getData_rx_rate()) ? 0 : Long.parseLong(entity.getData_rx_rate());
					WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, handsetId, handset.fetchData_rx_rate_double());//entity.getData_rx_rate_double());
					//3:移动设备连接wifi设备的接入记录(非流水)
					int result_status = wifiHandsetDeviceRelationMService.addRelation(wifiId, handsetId, new Date(sync_dto.getTs()));
					//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
					if(result_status == WifiHandsetDeviceRelationMService.AddRelation_Insert){
						//5:wifi设备接入移动设备的接入数量增量
						wifiHandsetDeviceLoginCountMService.incrCount(wifiId);
					}
					//4:移动设备连接wifi设备的流水log
					BusinessWifiHandsetRelationFlowLogger.doFlowMessageLog(wifiId, handsetId, sync_dto.getTs());
					cursor++;
				}
				//有新上线的设备(非新注册)
				if(!entityNewOnlines.isEmpty()){
					//2:移动设备基础信息更新 
					handsetDeviceService.updateAll(entityNewOnlines);
				}
				//新上线的并且是新注册的设备列表
				if(!entityNewRegisters.isEmpty()){
					//2:移动设备基础信息更新
					handsetDeviceService.insertAll(entityNewRegisters);
				}
				HandsetStorageFacadeService.handsetsComming(dtos);
				//终端统计
				deviceFacadeService.deviceStatisticsOnlines(ds, DeviceStatistics.Statis_HandsetDevice_Type);
			}
		}
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s] successful", message));
	}*/
/*	public void handsetDeviceSyncHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s]", message));
		
		HandsetDeviceSyncDTO sync_dto = JsonHelper.getDTO(message, HandsetDeviceSyncDTO.class);
		String wifiId = sync_dto.getMac();
		if(!StringUtils.isEmpty(wifiId)){
			//1:清除wifi设备对应handset在线列表redis
			deviceFacadeService.allHandsetDoOfflines(sync_dto.getMac());
			//WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(sync_dto.getMac());
			//WifiDeviceHandsetPresentSortedSetService.getInstance().clearOnlinePresents(sync_dto.getMac());
			
			List<HandsetDeviceDTO> dtos = sync_dto.getDtos();
			if(dtos != null && !dtos.isEmpty()){
				List<String> ids = new ArrayList<String>();
				for(HandsetDeviceDTO dto : dtos){
					ids.add(dto.getMac().toLowerCase());
				}
				//新上线的设备列表(非新注册)
				List<HandsetDevice> entityNewOnlines = new ArrayList<HandsetDevice>();
				//新上线的并且是新注册的设备列表
				List<HandsetDevice> entityNewRegisters = new ArrayList<HandsetDevice>();
				//终端统计模型
				List<DeviceStatistics> ds = new ArrayList<DeviceStatistics>();
				
				List<HandsetDevice> entitys = handsetDeviceService.findByIds(ids, true, true);
				int cursor = 0;
				for(HandsetDevice entity : entitys){
					if(entity != null && entity.isOnline()){
						continue;
					}
					HandsetDeviceDTO dto = dtos.get(cursor);
					if(entity == null){
						ds.add(new DeviceStatistics(dto.getMac(), true));
						
						entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
						entity.setLast_wifi_id(wifiId);
						entityNewRegisters.add(entity);
						
//						HandsetDevice handset = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
//						handset.setLast_wifi_id(wifiId);
//						entityNewRegisters.add(handset);
					}else{
						ds.add(new DeviceStatistics(dto.getMac(), entity.getLast_login_at()));
						
						BeanUtils.copyProperties(dto, entity, HandsetDeviceDTO.copyIgnoreProperties);
						if(!StringUtils.isEmpty(dto.getDhcp_name())){
							entity.setHostname(dto.getDhcp_name());
						}
						entity.setLast_login_at(new Date());
						entity.setLast_wifi_id(wifiId);
						entity.setOnline(true);
						entityNewOnlines.add(entity);
					}
					String handsetId = dto.getMac().toLowerCase();
					//1:wifi设备对应handset在线列表redis 重新写入
					//WifiDeviceHandsetPresentSortedSetService.getInstance().addPresent(wifiId, handsetId, sync_dto.getTs());
					//long rx_rate = StringUtils.isEmpty(entity.getData_rx_rate()) ? 0 : Long.parseLong(entity.getData_rx_rate());
					WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, handsetId, entity.getData_rx_rate_double());
					//3:移动设备连接wifi设备的接入记录(非流水)
					int result_status = wifiHandsetDeviceRelationMService.addRelation(wifiId, handsetId, new Date(sync_dto.getTs()));
					//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
					if(result_status == WifiHandsetDeviceRelationMService.AddRelation_Insert){
						//5:wifi设备接入移动设备的接入数量增量
						wifiHandsetDeviceLoginCountMService.incrCount(wifiId);
					}
					//4:移动设备连接wifi设备的流水log
					BusinessWifiHandsetRelationFlowLogger.doFlowMessageLog(wifiId, handsetId, sync_dto.getTs());
					cursor++;
				}
				//有新上线的设备(非新注册)
				if(!entityNewOnlines.isEmpty()){
					//2:移动设备基础信息更新 
					handsetDeviceService.updateAll(entityNewOnlines);
				}
				//新上线的并且是新注册的设备列表
				if(!entityNewRegisters.isEmpty()){
					//2:移动设备基础信息更新
					handsetDeviceService.insertAll(entityNewRegisters);
				}
				
				//终端统计
				deviceFacadeService.deviceStatisticsOnlines(ds, DeviceStatistics.Statis_HandsetDevice_Type);
			}
		}
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s] successful", message));
	}*/
	
	/**
	 * 修改设备配置信息
	 * @param message
	 * @throws Exception
	 */
/*	public void wifiDeviceSettingModify(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingModify message[%s]", message));
		
		WifiDeviceSettingModifyDTO dto = JsonHelper.getDTO(message, WifiDeviceSettingModifyDTO.class);
		//String payload = "<dev><sys><config><ITEM sequence=\""+entity.getInnerModel().getSequence()+"\"/></config></sys><wifi><vap><ITEM name=\"wlan0\" radio=\"wifi0\" ssid=\"urouter-lwh\" auth=\"open\" enable=\"enable\" acl_type=\"deny\" acl_name=\"blackList\" guest_en=\"disable\"/><ITEM name=\"wlan2\" radio=\"wifi0\" ssid=\"BHU2\" auth=\"open\" enable=\"disable\" acl_type=\"deny\" acl_name=\"blackList\" guest_en=\"disable\"/><ITEM name=\"wlan3\" radio=\"wifi0\" ssid=\"BHU3\" auth=\"open\" enable=\"disable\" acl_type=\"deny\" acl_name=\"blackList\" guest_en=\"disable\"/></vap><acllist><ITEM name=\"blackList\" macs=\"\" /></acllist></wifi></dev>";
		//String payload = "<dev><sys><config><ITEM sequence=\""+entity.getInnerModel().getSequence()+"\"/></config></sys></dev><dev><wifi><vap><ITEM name=\"wlan0\" radio=\"wifi0\" ssid=\"urouter-lwh\" auth=\"open\" enable=\"enable\" acl_type=\"deny\" acl_name=\"blackList1\" guest_en=\"disable\"/><ITEM name=\"wlan2\" radio=\"wifi0\" ssid=\"BHU2\" auth=\"open\" enable=\"disable\" acl_type=\"deny\" acl_name=\"blackList1\" guest_en=\"disable\"/><ITEM name=\"wlan3\" radio=\"wifi0\" ssid=\"BHU3\" auth=\"open\" enable=\"disable\" acl_type=\"deny\" acl_name=\"blackList1\" guest_en=\"disable\"/></vap><acllist><ITEM name=\"blackList1\" macs=\"11:11:11:11:11:11\" /></acllist></wifi></dev>";
		DaemonHelper.deviceSettingModify(dto.getMac(), dto.getPayload(), daemonRpcService);
		
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingModify message[%s] successful", message));
	}*/
	
	
	/*//added by Edmond Lee @20160106 for mark workmode changed of device
	if(!dto.isNewWifi()){
		//workmode切换只支持uRouter，并且只能在router-ap和bridge-ap之间互相切换
		//判定workmode是否变更
		if(StringUtils.isNotEmpty(dto.getO_wmode()) && StringUtils.isNotEmpty(dto.getN_wmode())){
			if(!dto.getO_wmode().equals(dto.getN_wmode())){
				//在判定workmode变更后打上标记，标记内容代表什么模式切换到什么模式，
				//由于模式切换还需要和设备重置有所相关，所以在查询配置分析中继续进行模式变更的操作内容
				if(WifiDeviceHelper.WorkMode_Router.equals(dto.getN_wmode())){
					BusinessMarkerService.getInstance().deviceWorkmodeChangedMarker(dto.getMac(), WifiDeviceHelper.SwitchMode_Bridge2Router);
				}
				if(WifiDeviceHelper.WorkMode_Bridge.equals(dto.getN_wmode())){
					BusinessMarkerService.getInstance().deviceWorkmodeChangedMarker(dto.getMac(), WifiDeviceHelper.SwitchMode_Bridge2Router);
				}
			}
		}
	}*/
	/**
	 * 设备配置查询后续动作处理
	 * 1、设备重置情况下操作（考虑设备模式切换）如果是设备重置则直接清除设备模式切换标记，如果不是设备重置状态则需要判定是否需要进行模式切换操作下发一些指令
	 * 2、uRouter 设备才进行周边探测是否继续重复下发
	 * @param message
	 * @throws Exception
	 */
	public void wifiDeviceSettingQuery(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingQuery message[%s]", message));
		
		WifiDeviceSettingQueryDTO dto = JsonHelper.getDTO(message, WifiDeviceSettingQueryDTO.class);
		final List<String> cmdPayloads = dto.getPayloads()== null?new ArrayList<String>():dto.getPayloads();
		//if(cmdPayloads == null) cmdPayloads = new ArrayList<String>();
		
		String mac = dto.getMac();
		//只有urouter设备才会执行
		//配置状态如果为恢复出厂 则清空设备的相关业务数据,只限于家用 TU uRouter
/*		if(dto.isDeviceURouter()){//deviceFacadeService.isURouterDevice(dto.getMac())){
			if(DeviceHelper.RefreashDeviceSetting_Normal == dto.getRefresh_status()){
				//判断周边探测是否开启 如果开启 再次下发开启指令
				UserSettingState settingState = userSettingStateService.getById(dto.getMac());
				if(settingState != null){
					UserWifiSinfferSettingDTO wifiSniffer = settingState.getUserSetting(UserWifiSinfferSettingDTO.Setting_Key, UserWifiSinfferSettingDTO.class);
					if(wifiSniffer != null && wifiSniffer.isOn()){
						//if(cmdPayloads == null) cmdPayloads = new ArrayList<String>();
						cmdPayloads.add(CMDBuilder.builderDeviceWifiSnifferSetting(dto.getMac(), WifiDeviceHelper.WifiSniffer_Start_Sta_Sniffer));
					}
				}
			}
		}*/
		
		if(DeviceHelper.RefreashDeviceSetting_RestoreFactory == dto.getRefresh_status()){
			try{
				logger.info(String.format("start execute deviceRestoreFactory mac[%s]", dto.getMac()));
				backendBusinessService.deviceResetFactory(dto.getMac());
				//解绑后需要发送指令通知设备
				//cmdPayloads.add(CMDBuilder.builderClearDeviceBootReset(dto.getMac(),CMDBuilder.AutoGen));
				logger.info(String.format("successed execute deviceRestoreFactory mac[%s]", dto.getMac()));
			}catch(Exception ex){
				//ex.printStackTrace();
				//清除失败后是否需要通知设备清除状态
				//cmdPayloads.add(CMDBuilder.builderClearDeviceBootReset(dto.getMac(),CMDBuilder.AutoGen));
				logger.error(String.format("fail execute deviceRestoreFactory mac[%s]", dto.getMac()), ex);
			}finally{
				cmdPayloads.add(CMDBuilder.builderClearDeviceBootReset(dto.getMac(),CMDBuilder.AutoGen));
			}
			{//在设备重置后的共享网络配置,给此设备下发此用户的共享网络配置 modify by Edmond Lee 20160322
				/*WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
				if(wifiDevice != null){
					if(WifiDeviceHelper.deviceSharedNetworkStrategy(wifiDevice.getOrig_swver())){
						addDevices2SharedNetwork(-1,dto.getMac(),cmdPayloads);
					}
				}*/
				addDevices2SharedNetwork(-1,dto.getMac());
			}
		}else{
			//检查设备配置中的设备绑定数据是否与服务器一致，如果不一致，下发数据同步配置
			WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
			if(entity != null){
				WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
				WifiDeviceSettingSyskeyDTO syskey_dto = setting_dto.getSyskey();
				//只有绑定配置是成功状态的才检测数据同步，如果不是成功状态，设备会主动上报来检测，避免重复
				if(syskey_dto != null && syskey_dto.isSuccessedStatus()){
					WifiDeviceDocument wifiDeviceDoc = wifiDeviceDataSearchService.searchById(mac);
					if(wifiDeviceDoc != null){
						WifiDeviceSettingSyskeyDTO syskey_current_dto = DeviceHelper.builderDeviceSettingSyskeyDTO(
								wifiDeviceDoc.getU_mno(), wifiDeviceDoc.getD_industry());
						//如果设备上报的绑定数据和服务器不一致
						if(!syskey_dto.equals(syskey_current_dto)){
							cmdPayloads.add(CMDBuilder.builderDeviceSettingModify(mac, 0, 
									DeviceHelper.builderDSKeyStatusOuter(syskey_current_dto)));
							//直接进行数据库配置修改
							setting_dto.setSyskey(syskey_current_dto);
							wifiDeviceSettingService.update(entity);
						}
					}
				}
			}
		}

		//分发指令
		this.wifiCmdsDownNotify(dto.getMac(), cmdPayloads);
		logger.info(String.format("AsyncMsgBackendProcessor wifiDeviceSettingQuery message[%s] successful", message));
	}
	
/*	List<String> cmdPayloads = null;
	try{
		WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
		if(wifiDevice != null){
			if(WifiDeviceHelper.deviceSharedNetworkStrategy(wifiDevice.getOrig_swver())){
				cmdPayloads = new ArrayList<>();
				addDevices2SharedNetwork(dto.getUid(),dto.getMac(),cmdPayloads);
			}
		}
		if(cmdPayloads!= null && !cmdPayloads.isEmpty())
			daemonRpcService.wifiDeviceCmdsDown(null, dto.getMac(), cmdPayloads);
	}finally{
		if(cmdPayloads != null){
			cmdPayloads.clear();
			cmdPayloads = null;
		}
	}*/
	
	//private void add
	
	
	/**
	 * uid = -1的时候代表此设备未绑定
	 * @param uid
	 * @param mac
	 * @param cmdPayloads
	 */
	private void addDevices2SharedNetwork(int uid,String mac){
		List<String> dmacs = null;
		try{
			final WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			if(wifiDevice == null) return;
			if(!WifiDeviceHelper.autoDeviceSecureSharedNetworkStrategy(wifiDevice.getOrig_swver())) return;
			dmacs = new ArrayList<String>();
			dmacs.add(mac);
			sharedNetworksFacadeService.addDevices2SharedNetwork(uid,null,null,false,dmacs,
					new ISharedNetworkNotifyCallback(){
						@Override
						public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
							logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", -1,rdmacs,JsonHelper.getJSONString(current)));
							if(rdmacs == null || rdmacs.isEmpty()){
								return;
							}
							for(String mac:rdmacs){
								//WifiDevice wifiDevice = wifiDeviceService.getById(mac);
								//if(wifiDevice == null) continue;
								current.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
								//生成下发指令
								String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, 
										mac, CMDBuilder.AutoGen,JsonHelper.getJSONString(current),
										deviceCMDGenFacadeService);
								daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
							}
							wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate());
						}
					});
		}finally{
			if(dmacs != null){
				dmacs.clear();
				dmacs = null;
			}
		}
	}
	
	/**
	 * 配置变更或者获取设备配置的后续处理
	 * @param message
	 * @throws Exception
	 */
	public void wifiDeviceSettingChanged(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingChanged message[%s]", message));
		
		WifiDeviceSettingChangedDTO dto = JsonHelper.getDTO(message, WifiDeviceSettingChangedDTO.class);
		//分发指令
		this.wifiCmdsDownNotify(dto.getMac(), dto.getPayloads());
		//如果是urouter设备 才会发push
		if(deviceFacadeService.isURouterDevice(dto.getMac())){
			pushService.push(new WifiDeviceSettingChangedPushDTO(dto.getMac()));
			
			/*//如果需要初始化设备的黑名单配置 按照urouter约定来进行
			if(dto.isInit_default_acl()){
				WifiDeviceSetting entity = wifiDeviceSettingService.getById(dto.getMac());
				if(entity != null){
					String modify_urouter_acl = DeviceHelper.builderDSURouterDefaultVapAndAcl(entity.getInnerModel());
					DaemonHelper.deviceSettingModify(dto.getMac(), modify_urouter_acl, daemonRpcService);
				}
			}*/
		}
		ssidModifyWithChangeDeviceName(dto.getMac());
		
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingChanged message[%s] successful", message));
	}
	
	/**
	 * 当设备的ssid发生变化时 判断设备昵称是否已经被用户改过
	 * 如果没有被用户改过 则要随着ssid变化而变化
	 * @param mac
	 */
	public void ssidModifyWithChangeDeviceName(String mac){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("mac", mac);
		List<UserDevice> userDevices = userDeviceService.findModelByModelCriteria(mc);
		if(!userDevices.isEmpty()){
			UserDevice userDevice = userDevices.get(0);
			if(!userDevice.isDevice_name_modifyed()){
				String ssid = deviceFacadeService.getUrouterSSID(mac);
				if(StringUtils.isNotEmpty(ssid) && !ssid.equals(userDevice.getDevice_name())){
					userDevice.setDevice_name(ssid);
					userDeviceService.update(userDevice);
					
					 wifiDeviceStatusIndexIncrementService.bindUserDNickUpdIncrement(mac, ssid);
				}
			}
		}
	}
	
	/**
	 * 根据wifi设备的经纬度调用第三方服务获取地理位置的详细信息
	 * 1:记录wifi设备的坐标 (backend)
	 * 2:根据坐标提取地理位置详细信息 (backend)
	 * 3:增量索引
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceLocationHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceLocationHandle message[%s]", message));
		
		WifiDeviceLocationDTO dto = JsonHelper.getDTO(message, WifiDeviceLocationDTO.class);
		//1:记录wifi设备的坐标 (backend)
		WifiDevice entity = wifiDeviceService.getById(dto.getMac());
		if(entity != null){
			//如果经纬度和记录的一样(如果经纬度有波动,可以考虑按误差值来判定), 并且地理信息已经提取, 就不再进行提取了
			if(dto.getLat().equals(entity.getLat()) && dto.getLon().equals(entity.getLon())){
				return;
			}
			
			entity.setLat(dto.getLat());
			entity.setLon(dto.getLon());
			//2:根据坐标提取地理位置详细信息
			boolean ret = deviceFacadeService.wifiDeiviceGeocoding(entity);
			if(ret){
				try{
					Map<String, String> params = new HashMap<String, String>();
					params.put("title",  StringUtils.isEmpty(entity.getStreet())?entity.getFormatted_address():entity.getStreet());
					params.put("address", entity.getFormatted_address());
					params.put("latitude", dto.getLat());
					params.put("longitude", dto.getLon());
					params.put("extension", JsonHelper.getJSONString(new GeoPoiExtensionDTO(entity.getId(),entity.isOnline()?1:0)));
					String bdid = entity.getBdid();
					GeocodingPoiRespDTO response = null;
					if(StringUtils.isEmpty(bdid)){
						response = GeocodingHelper.geoPoiCreate(params);
						entity.setBdid(String.valueOf(response.getId()));
					}else{
						params.put("id", bdid);
						response = GeocodingHelper.geoPoiUpdate(params);
						entity.setBdid(String.valueOf(response.getId()));
					}
					entity.setIpgen(false);
					logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceLocationHandle baidu geoid[%s] %s successful", response.getId(),StringUtils.isEmpty(bdid)?"Create":"Update"));
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("百度geo 麻点云操作失败",ex);
				}
			}
			wifiDeviceService.update(entity);
			//3:增量索引
			//wifiDeviceIndexIncrementService.wifiDeviceIndexIncrement(entity);
			wifiDeviceIndexIncrementProcesser.locaitionUpdIncrement(entity.getId(), Double.parseDouble(entity.getLat()),
					Double.parseDouble(entity.getLon()), entity.getFormatted_address());
		}
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceLocationHandle message[%s] successful", message));
	}
	
	/**
	 * 获取设备VAP下的终端列表
	 * 1:更新被管理的终端的上下行速率和ssid bssid
	 * 2:更新被管理的终端的限速设置
	 * @param message
	 * modified by Edmond Lee for handset storage
	 */
/*	public void WifiDeviceTerminalNotify(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor WifiDeviceTerminalNotify message[%s]", message));
		WifiDeviceTerminalNotifyDTO dto = JsonHelper.getDTO(message, WifiDeviceTerminalNotifyDTO.class);
		
		//获取设备的配置的dto
		WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(dto.getMac());
		if(setting_entity == null) return;
		WifiDeviceSettingDTO setting_entity_dto = setting_entity.getInnerModel();
		
		List<WifiDeviceTerminalDTO> terminals = dto.getTerminals();
		if(terminals != null && !terminals.isEmpty()){
			List<String> hdIds = new ArrayList<String>();
			for(WifiDeviceTerminalDTO terminal : terminals){
				hdIds.add(terminal.getMac());
			}

			//1:更新被管理的终端的上下行速率和ssid bssid
			int cursor = 0;
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(hdIds);
			//List<HandsetDevice> need_inserts = null;
			//List<HandsetDevice> need_updates = null;
			for(HandsetDeviceDTO handset : handsets){
				WifiDeviceTerminalDTO terminal = terminals.get(cursor);
				//判断是否在黑名单中
				if(DeviceHelper.isAclMac(terminal.getMac(), setting_entity_dto)) 
					continue;
				//匹配终端是否在限速列表中
//				WifiDeviceSettingRateControlDTO rc = DeviceHelper.matchRateControl(
//						setting_entity_dto, terminal.getMac());
				
				if(handset == null){
					HandsetDevice hd_entity = new HandsetDevice();
					hd_entity.setId(terminal.getMac());
					hd_entity.setLast_login_at(new Date());
					hd_entity.setLast_wifi_id(dto.getMac());
					hd_entity.setVapname(terminal.getVapname());
					hd_entity.setOnline(true);
					if(need_inserts == null)
						need_inserts = new ArrayList<HandsetDevice>();
					need_inserts.add(hd_entity);
					handset = new HandsetDeviceDTO();
					handset.setMac(terminal.getMac());
					handset.setAction(HandsetDeviceDTO.Action_Online);
					handset.setTs(System.currentTimeMillis());
					handset.setLast_wifi_id(dto.getMac());
					//handset.setDhcp_name(terminal.getVapname());
				}else{
					entity.setVapname(terminal.getVapname());
					entity.setData_tx_rate(terminal.getData_tx_rate());
					entity.setData_rx_rate(terminal.getData_rx_rate());
					
					//handset.setDhcp_name(terminal.getVapname());
					handset.setData_tx_rate(terminal.getData_tx_rate());
					handset.setData_rx_rate(terminal.getData_rx_rate());
//					entity.setOnline(true);
					//匹配终端是否在限速列表中
//					if(rc != null){
//						entity.setData_tx_limit(rc.getTx());
//						entity.setData_rx_limit(rc.getRx());
//					}
					if(need_updates == null)
						need_updates = new ArrayList<HandsetDevice>();
					need_updates.add(entity);
				}
				
				WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(dto.getMac(), 
						terminal.getMac(), StringUtils.isEmpty(terminal.getData_tx_rate()) ? 0d : Double.parseDouble(terminal.getData_tx_rate()));
				cursor++;
			}
			HandsetStorageFacadeService.handsetsComming(handsets);
			if(need_inserts != null)
				handsetDeviceService.insertAll(need_inserts);
			if(need_updates != null)
				handsetDeviceService.updateAll(need_updates);
		}
		logger.info(String.format("AnsyncMsgBackendProcessor WifiDeviceTerminalNotify message[%s] successful", message));

	}	*/
/*	public void WifiDeviceTerminalNotify(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor WifiDeviceTerminalNotify message[%s]", message));
		WifiDeviceTerminalNotifyDTO dto = JsonHelper.getDTO(message, WifiDeviceTerminalNotifyDTO.class);
		
		//获取设备的配置的dto
		WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(dto.getMac());
		if(setting_entity == null) return;
		WifiDeviceSettingDTO setting_entity_dto = setting_entity.getInnerModel();
		
		List<WifiDeviceTerminalDTO> terminals = dto.getTerminals();
		if(terminals != null && !terminals.isEmpty()){
			List<String> hdIds = new ArrayList<String>();
			for(WifiDeviceTerminalDTO terminal : terminals){
				hdIds.add(terminal.getMac());
			}

			//1:更新被管理的终端的上下行速率和ssid bssid
			int cursor = 0;
			List<HandsetDevice> entitys = handsetDeviceService.findByIds(hdIds, true, true);
			//List<WifiHandsetDeviceMark> entitys = wifiHandsetDeviceMarkService.findByIds(mark_ids, true, true);
			List<HandsetDevice> need_inserts = null;
			List<HandsetDevice> need_updates = null;
			for(HandsetDevice entity : entitys){
				WifiDeviceTerminalDTO terminal = terminals.get(cursor);
				//判断是否在黑名单中
				if(DeviceHelper.isAclMac(terminal.getMac(), setting_entity_dto)) 
					continue;
				
				//匹配终端是否在限速列表中
//				WifiDeviceSettingRateControlDTO rc = DeviceHelper.matchRateControl(
//						setting_entity_dto, terminal.getMac());
				
				if(entity == null){
					HandsetDevice hd_entity = new HandsetDevice();
					hd_entity.setId(terminal.getMac());
					hd_entity.setLast_login_at(new Date());
					hd_entity.setLast_wifi_id(dto.getMac());
					hd_entity.setVapname(terminal.getVapname());
					hd_entity.setOnline(true);
					
//					WifiHandsetDeviceMark insert_entity = new WifiHandsetDeviceMark();
//					insert_entity.setId(new WifiHandsetDeviceMarkPK(dto.getMac(), terminal.getMac()));
//					insert_entity.setVapname(terminal.getVapname());
//					insert_entity.setData_tx_rate(terminal.getData_tx_rate());
//					insert_entity.setData_rx_rate(terminal.getData_rx_rate());

//					if(rc != null){
//						insert_entity.setData_tx_limit(rc.getTx());
//						insert_entity.setData_rx_limit(rc.getRx());
//					}
					if(need_inserts == null)
						need_inserts = new ArrayList<HandsetDevice>();
					need_inserts.add(hd_entity);
				}else{
					entity.setVapname(terminal.getVapname());
					entity.setData_tx_rate(terminal.getData_tx_rate());
					entity.setData_rx_rate(terminal.getData_rx_rate());
//					entity.setOnline(true);
					//匹配终端是否在限速列表中
//					if(rc != null){
//						entity.setData_tx_limit(rc.getTx());
//						entity.setData_rx_limit(rc.getRx());
//					}
					
					if(need_updates == null)
						need_updates = new ArrayList<HandsetDevice>();
					need_updates.add(entity);
				}
				
				WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(dto.getMac(), 
						terminal.getMac(), StringUtils.isEmpty(terminal.getData_tx_rate()) ? 0d : Double.parseDouble(terminal.getData_tx_rate()));
				cursor++;
			}
			
			if(need_inserts != null)
				handsetDeviceService.insertAll(need_inserts);
			if(need_updates != null)
				handsetDeviceService.updateAll(need_updates);
		}
		logger.info(String.format("AnsyncMsgBackendProcessor WifiDeviceTerminalNotify message[%s] successful", message));

	}*/
	
	/*public void wifiCmdDownNotifyHandle(String message){
		logger.info(String.format("wifiCmdDownNotifyHandle message[%s]", message));
		WifiCmdNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdNotifyDTO.class);
		DaemonHelper.daemonCmdDown(dto.getMac(), dto.getPayload(), daemonRpcService);
		//daemonRpcService.wifiDeviceCmdDown(null, dto.getMac(), dto.getPayload());
		logger.info(String.format("wifiCmdDownNotifyHandle message[%s] successful", message));
	}*/
	public void wifiCmdsDownNotifyHandle(String message){
		logger.info(String.format("wifiCmdsDownNotifyHandle message[%s]", message));
		WifiCmdsNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdsNotifyDTO.class);
		this.wifiCmdsDownNotify(dto.getMac(), dto.getPayloads());
		//daemonRpcService.wifiDeviceCmdDown(null, dto.getMac(), dto.getPayload());
		logger.info(String.format("wifiCmdDownNotifyHandle message[%s] successful", message));
	}
	
	public void wifiCmdsDownNotify(String mac, List<String> payloads){
		if(StringUtils.isEmpty(mac)) return;
		if(payloads == null || payloads.isEmpty()) return;

		DaemonHelper.daemonCmdsDown(mac, payloads, daemonRpcService);
	}
	
	public void wifiMultiCmdsDownNotifyHandle(String message){
		logger.info(String.format("wifiMultiCmdsDownNotifyHandle message[%s]", message));
		WifiMultiCmdsNotifyDTO dto = JsonHelper.getDTO(message, WifiMultiCmdsNotifyDTO.class);
		if(dto.getDownCmds() == null || dto.getDownCmds().isEmpty()) return;
		DaemonHelper.daemonMultiCmdsDown(daemonRpcService, dto.getDownCmds().toArray(new DownCmds[0]));
		logger.info(String.format("wifiMultiCmdsDownNotifyHandle message[%s] successful", message));
	}
	/**
	 * 修改黑名单列表内容的后续操作
	 * 黑名单列表里的终端需要删除urouter 终端redis
	 * 保证不出现在urouter app终端列表中显示
	 * @param message
	 */
	public void deviceModifySettingAclMacs(String message){
		logger.info(String.format("deviceModifySettingAclMacs message[%s]", message));
		DeviceModifySettingAclMacsDTO dto = JsonHelper.getDTO(message, DeviceModifySettingAclMacsDTO.class);
		String mac = dto.getMac();
		if(!StringUtils.isEmpty(mac)){
			WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(mac);
			if(setting_entity != null){
				WifiDeviceSettingDTO setting_dto = setting_entity.getInnerModel();
				WifiDeviceSettingAclDTO acl_dto = DeviceHelper.matchDefaultAcl(setting_dto);
				if(acl_dto != null){
					WifiDeviceHandsetPresentSortedSetService.getInstance().removePresents(mac, acl_dto.getMacs());
				}
			}
		}
		logger.info(String.format("deviceModifySettingAclMacs message[%s] successful", message));
	}

	public void deviceModifySettingAalias(String message){
		logger.info(String.format("deviceModifySettingAalias message[%s]", message));
		DeviceModifySettingAliasDTO dto = JsonHelper.getDTO(message, DeviceModifySettingAliasDTO.class);
		int uid = dto.getUid();
		String mac = dto.getMac();
		String content = dto.getContent();
		if(!StringUtils.isEmpty(mac)){
			Map<String, List<WifiDeviceSettingMMDTO>> mm_dto_map = JsonHelper.getDTOMapKeyList(content, WifiDeviceSettingMMDTO.class);
			if(mm_dto_map != null &&  !mm_dto_map.isEmpty()) {
				List<WifiDeviceSettingMMDTO> mm_incr_dtos = mm_dto_map.get(DeviceHelper.DeviceSettingAction_Incr);
				if(mm_incr_dtos != null && !mm_incr_dtos.isEmpty()) {
					for (WifiDeviceSettingMMDTO mm_incr_dto : mm_incr_dtos) {
						WifiDeviceHandsetAliasService.getInstance().hsetHandsetAlias(uid, mm_incr_dto.getMac(), mm_incr_dto.getName());
					}
				}
				List<WifiDeviceSettingMMDTO> mm_del_dtos = mm_dto_map.get(DeviceHelper.DeviceSettingAction_Del);
				if(mm_del_dtos != null && !mm_del_dtos.isEmpty()){
					for(WifiDeviceSettingMMDTO mm_del_dto : mm_del_dtos){
						WifiDeviceHandsetAliasService.getInstance().hdelHandsetAlias(uid, mm_del_dto.getMac());
					}
				}
			}



		}
		logger.info(String.format("deviceModifySettingAalias message[%s] successful", message));
	}
	
	/**
	 * 修改VAP内容的后续操作
	   如果修改了ssid，则判断是否需要修改设备昵称
	 * @param message
	 */
	public void deviceModifySettingVap(String message){
		logger.info(String.format("deviceModifySettingVap message[%s]", message));
		DeviceModifySettingVapDTO dto = JsonHelper.getDTO(message, DeviceModifySettingVapDTO.class);

		ssidModifyWithChangeDeviceName(dto.getMac());
		
		logger.info(String.format("deviceModifySettingVap message[%s] successful", message));
	}
	
	
//	public void wifiDeviceSettingNotify(String message){
//		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingNotify message[%s]", message));
//		WifiDeviceSettingNotifyDTO dto = JsonHelper.getDTO(message, WifiDeviceSettingNotifyDTO.class);
//
//		List<String> vapnames = dto.getVapnames();
//		DaemonHelper.deviceTerminalsQuery(dto.getMac(), vapnames, daemonRpcService);
//		/*if(vapnames != null && !vapnames.isEmpty()){
//			List<String> cmds = CMDBuilder.builderDeviceTerminalsQueryWithAutoTaskid(dto.getMac(), dto.getVapnames());
//			daemonRpcService.wifiDeviceCmdsDown(null, dto.getMac(), cmds);
//		}*/
//		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceSettingNotify message[%s] successful", message));
//
//	}
	
	public void wifiDeviceRealtimeRateFetch(String message){
		logger.info(String.format("wifiDeviceRealtimeRateFetch message[%s]", message));
		WifiRealtimeRateFetchDTO dto = JsonHelper.getDTO(message, WifiRealtimeRateFetchDTO.class);
		//DaemonHelper.daemonCmdDown(dto.getMac(), dto.getPayload(), daemonRpcService);
		//daemonRpcService.wifiDeviceCmdDown(null, dto.getMac(), dto.getPayload());
		DaemonHelper.deviceRateQuery(dto.getMac(), daemonRpcService);
		WifiDeviceRealtimeRateStatisticsStringService.getInstance().addRateWaiting(dto.getMac());
		logger.info(String.format("wifiDeviceRealtimeRateFetch message[%s] successful", message));
	}
	
	public void wifiDeviceHDRateFetch(String message){
		logger.info(String.format("wifiDeviceHDRateFetch message[%s]", message));
		WifiRealtimeRateFetchDTO dto = JsonHelper.getDTO(message, WifiRealtimeRateFetchDTO.class);
		//DaemonHelper.daemonCmdDown(dto.getMac(), dto.getPayload(), daemonRpcService);
		//daemonRpcService.wifiDeviceCmdDown(null, dto.getMac(), dto.getPayload());
		DaemonHelper.deviceTerminalsRateQuery(dto.getMac(), daemonRpcService);
		WifiDeviceRealtimeRateStatisticsStringService.getInstance().addHDRateWaiting(dto.getMac());
		logger.info(String.format("wifiDeviceHDRateFetch message[%s] successful", message));
	}
	
/*	public void wifiDevicePeakRateFetch(String message){
		logger.info(String.format("wifiDevicePeakRateFetch message[%s]", message));
		WifiDeviceSpeedFetchDTO dto = JsonHelper.getDTO(message, WifiDeviceSpeedFetchDTO.class);
		//DaemonHelper.daemonCmdDown(dto.getMac(), dto.getPayload(), daemonRpcService);
		//daemonRpcService.wifiDeviceCmdDown(null, dto.getMac(), dto.getPayload());
		//DaemonHelper.deviceRateQuery(dto.getMac(), daemonRpcService);
		DaemonHelper.deviceSpeedQuery(dto.getMac(), dto.getType(), dto.getPeriod(), dto.getDuration(), daemonRpcService);
		//WifiDeviceRealtimeRateStatisticsStringService.getInstance().addPeakRateWaiting(dto.getMac());
		logger.info(String.format("wifiDevicePeakRateFetch message[%s] successful", message));
	}*/
	
	public void wifiDevicesGrayChanged(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDevicesGrayChanged message[%s]", message));
		WifiDevicesGrayChangedDTO dto = JsonHelper.getDTO(message, WifiDevicesGrayChangedDTO.class);
		this.wifiDeviceIndexIncrementProcesser.graylevelMultiUpdIncrement(dto.getMacs(), String.valueOf(dto.getGl()));
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDevicesGrayChanged message[%s] successful", message));
	}
	
	public void wifiDevicesModuleStyleChanged(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDevicesModuleStyleChanged message[%s]", message));
		WifiDevicesModuleStyleChangedDTO dto = JsonHelper.getDTO(message, WifiDevicesModuleStyleChangedDTO.class);
		this.wifiDeviceIndexIncrementProcesser.templateMultiUpdIncrement(dto.getMacs(), dto.getStyle());
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDevicesModuleStyleChanged message[%s] successful", message));
	}
	
	/**
	 * 用户绑定设备之后 会进行下发配置操作
	 * 下发获取配置，获取设备测速，设备实时速率, 设备终端列表
	 * @param message
	 */
	public void userDeviceRegister(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceRegister message[%s]", message));
		UserDeviceRegisterDTO dto = JsonHelper.getDTO(message, UserDeviceRegisterDTO.class);
		deviceFacadeService.addMobilePresent(dto.getUid(), dto.getMac());
		afterUserSignedonThenCmdDown(dto.getMac());

		userDeviceBindOperateSyskeySync(dto.getMac(), dto.getUid());
		
		{//给此设备下发此用户的共享网络配置 modify by Edmond Lee 20160322
			/*List<String> cmdPayloads = null;
			try{
				WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
				if(wifiDevice != null){
					if(WifiDeviceHelper.deviceSharedNetworkStrategy(wifiDevice.getOrig_swver())){
						cmdPayloads = new ArrayList<>();
						addDevices2SharedNetwork(dto.getUid(),dto.getMac(),cmdPayloads);
					}
				}
				if(cmdPayloads!= null && !cmdPayloads.isEmpty())
					daemonRpcService.wifiDeviceCmdsDown(null, dto.getMac(), cmdPayloads);
			}finally{
				if(cmdPayloads != null){
					cmdPayloads.clear();
					cmdPayloads = null;
				}
			}*/
			addDevices2SharedNetwork(dto.getUid(),dto.getMac());
		}
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceRegister message[%s] successful", message));
	}
	
	/**
	 * 用户解绑设备之后 会清除用户设备的设置数据
	 * @param message
	 */
	public void userDeviceDestory(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceDestory message[%s]", message));
		UserDeviceDestoryDTO dto = JsonHelper.getDTO(message, UserDeviceDestoryDTO.class);
		deviceFacadeService.removeMobilePresent(dto.getUid(), dto.getMac());
		userDeviceBindOperateSyskeySync(dto.getMac(), null);
		{//给此设备下发此用户的共享网络配置 modify by Edmond Lee 20160322
			/*List<String> cmdPayloads = null;
			try{
				WifiDevice wifiDevice = wifiDeviceService.getById(dto.getMac());
				if(wifiDevice != null){
					if(WifiDeviceHelper.deviceSharedNetworkStrategy(wifiDevice.getOrig_swver())){
						cmdPayloads = new ArrayList<>();
						addDevices2SharedNetwork(-1,dto.getMac(),cmdPayloads);
					}
				}
				if(cmdPayloads!= null && !cmdPayloads.isEmpty())
					daemonRpcService.wifiDeviceCmdsDown(null, dto.getMac(), cmdPayloads);
			}finally{
				if(cmdPayloads != null){
					cmdPayloads.clear();
					cmdPayloads = null;
				}
			}*/
			addDevices2SharedNetwork(-1,dto.getMac());
		}
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceDestory message[%s] successful", message));
	}
	
	/**
	 * 用户通过其他方式解绑设备或绑定设备以后
	 * 需要对设备的绑定配置数据进行同步
	 * @param mac 设备mac
	 * @param uid 目前绑定的uid 传null为没有
	 */
	public void userDeviceBindOperateSyskeySync(String mac, Integer uid){
		if(StringUtils.isEmpty(mac)) return;
		
		String keynum = StringHelper.EMPTY_STRING_GAP;
		
		if(uid != null){
			User user = userService.getById(uid);
			if(user != null){
				keynum = user.getMobileno();
			}
		}
		
		WifiDeviceSettingSyskeyDTO syskey_dto = DeviceHelper.builderDeviceSettingSyskeyDTO(keynum, null);
		String cmdPayload = CMDBuilder.builderDeviceSettingModify(mac, 0, DeviceHelper.builderDSKeyStatusOuter(syskey_dto));
		
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity != null){
			entity.getInnerModel().setSyskey(syskey_dto);
			wifiDeviceSettingService.update(entity);
		}
		
		daemonRpcService.wifiDeviceCmdDown(null, mac, cmdPayload);
	}
	
	/**
	 * 设备被用户强制绑定
	 * 如果设备是urouter设备，需要清除用户移动设备push数据
	 * 增量索引用户绑定数据更新
	 * @param message
	 */
	public void userDeviceForceBind(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceForceBind message[%s]", message));
		UserDeviceForceBindDTO dto = JsonHelper.getDTO(message, UserDeviceForceBindDTO.class);
		
		User user = userService.getById(dto.getUid());
		if(user != null){
			DeviceVersion parser = DeviceVersion.parser(dto.getOrig_swver());
			//如果设备是urouter，需要清除用户移动设备push数据
			if(parser.wasDutURouter()){
				if(dto.getOld_uid() != null){
					deviceFacadeService.removeMobilePresent(dto.getOld_uid(), dto.getMac());
				}
			}
			//wifiDeviceIndexIncrementProcesser.bindUserUpdIncrement(dto.getMac(), user);
//			wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(dto.getMac(), user);
		}
		
		logger.info(String.format("AnsyncMsgBackendProcessor userDeviceDestory message[%s] successful", message));
	}
	
	/**
	 * 用户登录after
	 * 根据用户管理的设备
	 * 下发获取配置，获取设备测速，设备实时速率, 设备终端列表
	 * @param message
	 */
	public void userSignedon(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor userSignedon message[%s]", message));
		UserSignedonDTO dto = JsonHelper.getDTO(message, UserSignedonDTO.class);
		//获取用户已经绑定的设备
		List<UserDevicePK> userDevicePks = deviceFacadeService.getUserDevices(dto.getUid());
		if(!userDevicePks.isEmpty()){
			//List<String> macs = new ArrayList<String>();
			for(UserDevicePK userDevicePk : userDevicePks){
				//macs.add(UserDevicePk.getMac());
				afterUserSignedonThenCmdDown(userDevicePk.getMac());
			}
		}
		logger.info(String.format("AnsyncMsgBackendProcessor userSignedon message[%s] successful", message));
	}
	
	/**
	 * 用户注册after
	 * 1：生成用户设置数据初始化
	 * @param message
	 */
	public void userRegister(String message){
		//logger.info(String.format("AnsyncMsgBackendProcessor userRegister message[%s]", message));
		UserRegisteredDTO dto = JsonHelper.getDTO(message, UserRegisteredDTO.class);
		int addret = ExchangeBBSHelper.userAdd2BBS(dto.getMobileno());
		if(addret == 1){
			logger.info("AnsyncMsgBackendProcessor userRegister2BBS successful:"+dto.getMobileno());
		}else{
			logger.info("AnsyncMsgBackendProcessor userRegister2BBS error:"+addret + " mobileno:"+dto.getMobileno());
		}
		
	}
	
	/**
	 * 用户bbs登录 通过发送push消息通知app
	 * 安卓设备推送静默发送
	 * ios设备推送通知发送
	 * @param message
	 */
	public void userBBSsignedon(String message){
		//logger.info(String.format("AnsyncMsgBackendProcessor userBBSsignedon message[%s]", message));
		UserBBSsignedonDTO dto = JsonHelper.getDTO(message, UserBBSsignedonDTO.class);
		UserBBSsignedonPushDTO push_dto = new UserBBSsignedonPushDTO();
//		push_dto.setUid(dto.getUid());
//		push_dto.setCountrycode(dto.getCountrycode());
//		push_dto.setAcc(dto.getAcc());
		push_dto.setSecretkey(dto.getSecretkey());
		push_dto.setTs(System.currentTimeMillis());
		
		DeviceMobilePresentDTO mobile_present_dto = new DeviceMobilePresentDTO();
		BeanUtils.copyProperties(dto, mobile_present_dto);
		
		boolean push_successed = pushService.pushUserBBSsignedon(push_dto, mobile_present_dto);
		logger.info(String.format("AnsyncMsgBackendProcessor userBBSsignedon push uid[%s] acc[%s] sk[%s] result[%s]", 
				dto.getUid(), dto.getAcc(), dto.getSecretkey(), push_successed));
		
		if(!push_successed){
			//如果发送push失败 调用bbs接口通知状态
			int addret = 1;//ExchangeBBSHelper.userAdd2BBS(dto.getMobileno());
			if(addret == 1){
				logger.info("AnsyncMsgBackendProcessor userSignedon2BBS successful");
			}else{
				logger.info("AnsyncMsgBackendProcessor userSignedon2BBS error:"+addret);
			}
		}
		
	}
	
	//设备实时速率, 设备终端列表
	public void afterUserSignedonThenCmdDown(String mac){
		logger.info(String.format("wifiDeviceOnlineHandle afterUserSignedonThenCmdDown[%s]", mac));
		boolean needDeviceUsedQuery = false;
		needDeviceUsedQuery = BusinessMarkerService.getInstance().needNewRequestAndMarker(mac,true);
		DaemonHelper.afterUserSignedon(mac, needDeviceUsedQuery, daemonRpcService);
/*		if(!WifiDeviceRealtimeRateStatisticsStringService.getInstance().isHDRateWaiting(mac)){
			//获取设备的终端列表
			DaemonHelper.deviceTerminalsRateQuery(mac, daemonRpcService);
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().addHDRateWaiting(mac);
		}
		if(!WifiDeviceRealtimeRateStatisticsStringService.getInstance().isRateWaiting(mac)){
			//获取设备的实时速率
			DaemonHelper.deviceRateQuery(mac, daemonRpcService);
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().addRateWaiting(mac);
		}*/
		logger.info(String.format("wifiDeviceOnlineHandle afterUserSignedonThenCmdDown message[%s] successful", mac));
	}
	
	public void sendCaptchaCodeNotifyHandle(String message){
		logger.info(String.format("sendCaptchaCodeNotifyHandle message[%s]", message));
		try{
			UserCaptchaCodeFetchDTO dto = JsonHelper.getDTO(message, UserCaptchaCodeFetchDTO.class);
			if(!RuntimeConfiguration.SecretInnerTest){
				//logger.info("step 1");
				String mobileWithCountryCode = PhoneHelper.format(dto.getCountrycode(), dto.getAcc());
				//logger.info("step 2");
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(mobileWithCountryCode)){
					//logger.info("step 3");
					if(dto.getCountrycode() == PhoneHelper.Default_CountryCode_Int){
						//logger.info("step 4 -1");
						String smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Template, dto.getCaptcha());
						String response = SmsSenderFactory.buildSender(
								BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, dto.getAcc());
						//String response = ChanzorSMSHelper.postSendMsg(smsg, dto.getAcc());
						//String response = WangjianSMSHelper.postSendMsg(smsg, new String[]{dto.getAcc()});
						//logger.info("CaptchaCodeNotifyActHandler Guodu msg:"+message);
						logger.info("sendCaptchaCodeNotifyHandle new Chanzor res:"+response+" msg:"+smsg);
					}else{
						//logger.info("step 4 -2");
						logger.info("sendCaptchaCodeNotifyHandle not supported foreign sms res");
						/*if(dto.getCountrycode() == NexmoSMSHelper.UsAndCanada_CountryCode_Int){
							String response = NexmoSMSHelper.send(NexmoSMSHelper.Default_UsANDCanada_SMS_FROM,mobileWithCountryCode, String.format(RuntimeConfiguration.ForeignCaptchaCodeSMS_Template,dto.getCaptcha()));//.postSendMsg(String.format(RuntimeConfiguration.InternalCaptchaCodeSMS_Template, dto.getCaptcha()), new String[]{dto.getAcc()});
							logger.info("to US and Canada CaptchaCodeNotifyActHandler Nexmo res:"+response);
						}else{
							String response = NexmoSMSHelper.send(mobileWithCountryCode, String.format(RuntimeConfiguration.ForeignCaptchaCodeSMS_Template,dto.getCaptcha()));//.postSendMsg(String.format(RuntimeConfiguration.InternalCaptchaCodeSMS_Template, dto.getCaptcha()), new String[]{dto.getAcc()});
							logger.info("to Other CaptchaCodeNotifyActHandler Nexmo res:"+response);
						}
						//logger.info("CaptchaCodeNotifyActHandler Nexmo msg:"+message);
	*/				}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		//logger.info(String.format("sendCaptchaCodeNotifyHandle message[%s] successful", message));

	}
	/*public void deviceAsyncCmdGen(String message){
		logger.info(String.format("deviceAsyncCmdGen message[%s]", message));
		Set<String> totalDevices = null;
		WifiDeviceGroup dgroup = null;
		List<String> onlineDevices = null;
		try{
			WifiDeviceAsynCmdGenerateDTO dto = JsonHelper.getDTO(message, WifiDeviceAsynCmdGenerateDTO.class);
			totalDevices = new HashSet<String>();
			if(StringUtils.isNotEmpty(dto.getMac())) totalDevices.add(dto.getMac());
			if(dto.getGid() > 0){
				dgroup = wifiDeviceGroupService.getById(dto.getGid());
				if(dgroup != null){
					if(!dto.isDependency()){
						totalDevices.addAll(dgroup.getInnerModels());
					}else{
						List<WifiDeviceGroup> allByPath = wifiDeviceGroupService.fetchAllByPath(dgroup.getPath(), true);
						for(WifiDeviceGroup _dgroup:allByPath){
							totalDevices.addAll(_dgroup.getInnerModels());
						}
					}
				}
			}
			if(totalDevices.isEmpty()) return;
			//只给在线的设备发送指令
			onlineDevices = wifiDeviceService.filterOnlineIdsWith(new ArrayList<String>(totalDevices), true);
			if(onlineDevices.isEmpty()){
				return;
			}
			for(String wifi_id:onlineDevices){
				try{
					WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(dto.getUid(), wifi_id, dto.getOpt(), dto.getSubopt(), 
							dto.getExtparams(), dto.getChannel(), dto.getChannel_taskid());
					DaemonHelper.daemonCmdDown(wifi_id, downTask.getPayload(), daemonRpcService);
				}catch(BusinessI18nCodeException bex){
					bex.printStackTrace(System.out);
					logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
				}
			}
		}finally{
			if(onlineDevices != null){
				onlineDevices.clear();
				onlineDevices = null;
			}
			if(totalDevices != null){
				totalDevices.clear();
				totalDevices = null;
			}
			if(dgroup != null){
				dgroup = null;
			}
		}
		
		logger.info(String.format("deviceAsyncCmdGen message[%s] successful", message));
	}*/
}
