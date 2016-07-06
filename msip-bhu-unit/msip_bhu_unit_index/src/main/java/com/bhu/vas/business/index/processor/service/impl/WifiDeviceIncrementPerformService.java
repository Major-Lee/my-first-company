package com.bhu.vas.business.index.processor.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.index.processor.service.IncrementPerformActionService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

@Service
public class WifiDeviceIncrementPerformService extends IncrementPerformActionService{
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIncrementPerformService.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGrayService wifiDeviceGrayService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private TagDevicesService tagDevicesService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private UserWifiDeviceService userWifiDeviceService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
	
	@Resource
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	

	@Override
	public void incrementDocumentActionHandle(String id, IncrementActionEnum actionEnum) {
		switch(actionEnum){
			case WD_FullCreate:
				incrementDocumentByFullCreate(id);
				break;
			case WD_ModuleOnlineStatus:
				incrementDocumentByModuleOnlineStatus(id);
				break;
			case WD_OnlineStatus:
				incrementDocumentByOnlineStatus(id);
			default:
				break;
		}
	}
	
	/**
	 * 设备索引完整创建数据
	 * 变更涉及的更改索引字段是全部变更
	 * @param id
	 */
	public void incrementDocumentByFullCreate(String id){
		logger.info(String.format("IncrementDocumentByFullCreate Request id [%s]", id));
		WifiDevice wifiDevice = wifiDeviceService.getById(id);
		if(wifiDevice != null){
			WifiDeviceDocument doc = new WifiDeviceDocument();
			String mac = wifiDevice.getId();
			WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
			WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
			TagDevices tagDevices = tagDevicesService.getById(mac);
			String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(mac);
			long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);

			User bindUser = null;
			String bindUserDNick = null;
			
			UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
			if(userWifiDevice != null){
				bindUserDNick = userWifiDevice.getDevice_name();
				Integer bindUserId = userWifiDevice.getUid();
				if(bindUserId != null){
					bindUser = userService.getById(bindUserId);
				}
			}
			
			WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
			WifiDeviceSharedealConfigs wifiDeviceShareConfig = wifiDeviceSharedealConfigsService.getById(mac);
			String t_uc_extension = tagGroupRelationService.fetchPathWithMac(mac);
			
			doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
					wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
					o_template, (int)hoc, wifiDeviceSharedNetwork, wifiDeviceShareConfig, t_uc_extension);
			wifiDeviceDataSearchService.save(doc);
		}
		logger.info(String.format("IncrementDocumentByFullCreate Request id [%s] Successful", id));
	}

	/**
	 * 设备模块在线状态发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_monline
	 * 2) d_origvapmodule
	 * 3) o_operate
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	public void incrementDocumentByModuleOnlineStatus(String id){
		logger.info(String.format("ModuleOnlineUpdIncrement Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(id);
		if(wifiDeviceModule != null){
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), 
					wifiDeviceModule.isModule_online() ? WifiDeviceDocumentEnumType.MOnlineEnum.MOnline.getType() : 
						WifiDeviceDocumentEnumType.MOnlineEnum.MOffline.getType());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGVAPMODULE.getName(), wifiDeviceModule.getOrig_vap_module());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_OPERATE.getName(), WifiDeviceDocumentEnumType.OperateEnum.Operate.getType());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
			wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		}
		logger.info(String.format("ModuleOnlineUpdIncrement Request id [%s] Successful", id));
	}
	
	
	/**
	 * 设备发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_monline
	 * 3) d_uptime
	 * 4) d_lastlogoutat
	 * 5) d_hoc
	 * @param id 设备mac
	 * @param d_uptime 设备运行总时长
	 * @param d_lastlogoutat 设备的最后下线的时间
	 */
	public void incrementDocumentByOnlineStatus(String id){
		logger.info(String.format("incrementDocumentByOnlineStatus Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		WifiDevice wifiDevice = wifiDeviceService.getById(id);
		if(wifiDevice != null){
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.ID.getName(), wifiDevice.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SN.getName(), wifiDevice.getSn());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), wifiDevice.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), 
					wifiDevice.isOnline() ? WifiDeviceDocumentEnumType.OnlineEnum.Online.getType() : 
						WifiDeviceDocumentEnumType.OnlineEnum.Offline.getType());
			
			WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(id);
			if(wifiDeviceModule != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), 
						wifiDeviceModule.isModule_online() ? WifiDeviceDocumentEnumType.MOnlineEnum.MOnline.getType() : 
							WifiDeviceDocumentEnumType.MOnlineEnum.MOffline.getType());
			}
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), wifiDevice.getOrig_swver());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGMODEL.getName(), wifiDevice.getOrig_model());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), wifiDevice.getWork_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), wifiDevice.getConfig_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), wifiDevice.getHdtype());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WANIP.getName(), wifiDevice.getWan_ip());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_UPTIME.getName(), wifiDevice.getUptime());
			if(wifiDevice.getLast_logout_at() != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTLOGOUTAT.getName(), wifiDevice.getLast_logout_at().getTime());
			}

			WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(id);
			if(wifiDeviceGray != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), String.valueOf(wifiDeviceGray.getGl()));
			}else{
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), String.valueOf(GrayLevel.Other.getIndex()));
			}
			
			if(wifiDevice.getLast_reged_at() != null)
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), wifiDevice.getLast_reged_at().getTime());
			
			DeviceVersion parser = DeviceVersion.parser(wifiDevice.getOrig_swver());
			if(parser != null){
				String dut = parser.getDut();
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), dut);
				DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, wifiDevice.getHdtype());
				if(deviceUnitType != null){
					sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE_SNAME.getName(), deviceUnitType.getSname());
				}
			}
			if(wifiDevice.getCreated_at() != null)
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CREATEDAT.getName(), wifiDevice.getCreated_at().getTime());
				
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
			wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true, true);
		}
		logger.info(String.format("incrementDocumentByOnlineStatus Request id [%s] Successful", id));
	}

}
