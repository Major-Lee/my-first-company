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
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
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
				break;
			case WD_BindUserStatus:
				incrementDocumentByOnlineStatus(id);
				break;
			case WD_DTagsChanged:
				incrementDocumentByDTagsChanged(id);
				break;
			case WD_UCExtensionChanged:
				incrementDocumentByUCExtensionChanged(id);
				break;
			case WD_LocationChanged:
				incrementDocumentByLocationChanged(id);
				break;
			case WD_TemplateChanged:
				incrementDocumentByTemplateChanged(id);
				break;
			case WD_GraylevelChanged:
				incrementDocumentByGraylevelChanged(id);
				break;
			case WD_SharedNetworkChanged:
				incrementDocumentBySharedNetworkChanged(id);
				break;
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
	 * 设备模块在线状态发生变更
	 * 变更涉及的更改索引字段是
	 * @param id 设备mac
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

	
	/**
	 * 设备绑定或解绑的变更
	 * 变更涉及的更改索引字段是
	 * 1) u_id
	 * 2) u_nick
	 * 3) u_mno
	 * 4) u_mcc
	 * 5) u_type
	 * 6) u_binded
	 * 7) u_dnick
	 * 8) d_industry 
	 * @param id 设备mac
	 */
	public void incrementDocumentByBindUserStatus(String id){
		logger.info(String.format("IncrementDocumentByBindUserStatus Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		WifiDevice wifiDevice = wifiDeviceService.getById(id);
		if(wifiDevice != null){
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			UserWifiDevice userWifiDevice = userWifiDeviceService.getById(id);
			if(userWifiDevice != null){
				User bindUser = userService.getById(userWifiDevice.getUid());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), bindUser.getId());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), bindUser.getNick());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), bindUser.getMobileno());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), bindUser.getCountrycode());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), bindUser.getUtype());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UBinded.getType());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), userWifiDevice.getDevice_name());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_INDUSTRY.getName(), wifiDevice.getIndustry());
			}else{
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UNOBinded.getType());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), null);
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.T_UC_EXTENSION.getName(), null);
			}
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

			wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		}
		logger.info(String.format("IncrementDocumentByBindUserStatus Request id [%s] Successful", id));
	}
	
	
	/**
	 * 设置绑定的设备的tags
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * */
	public void incrementDocumentByDTagsChanged(String id){
		logger.info(String.format("IncrementDocumentByDTagsChanged Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		
		TagDevices tagDevices = tagDevicesService.getById(id);
		if(tagDevices != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TAGS.getName(), tagDevices.getTag2ES());
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TAGS.getName(), null);
		}		
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		logger.info(String.format("IncrementDocumentByDTagsChanged Request id [%s] Successful", id));
	}
	
	
	/**
	 * 设备的ucloud业务搜索字段的变更
	 * 变更涉及的更改索引字段是
	 * 1) t_uc_extension
	 * @param id
	 */
	public void incrementDocumentByUCExtensionChanged(String id) {
		logger.info(String.format("IncrementDocumentByUCExtension Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		String t_uc_extension = tagGroupRelationService.fetchPathWithMac(id);
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.T_UC_EXTENSION.getName(), t_uc_extension);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(),  DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		
		logger.info(String.format("IncrementDocumentByUCExtension Request id [%s] Successful", id));
	}
	
	/**
	 * 设备位置发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_address
	 * 2) d_geopoint
	 * @param id 设备mac
	 */
	public void incrementDocumentByLocationChanged(String id){
		logger.info(String.format("IncrementDocumentByLocation Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		WifiDevice wifiDevice = wifiDeviceService.getById(id);
		if(wifiDevice != null){
			if(StringUtils.isNotEmpty(wifiDevice.getLat()) && StringUtils.isNotEmpty(wifiDevice.getLon())){
				double lat = Double.parseDouble(wifiDevice.getLat());
				double lon = Double.parseDouble(wifiDevice.getLon());
				Map<String, Object> sourceMap = new HashMap<String, Object>();
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_GEOPOINT.getName(), new double[]{lon, lat});
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ADDRESS.getName(), wifiDevice.getFormatted_address());
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
				wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
			}
		}
		logger.info(String.format("IncrementDocumentByLocation Request id [%s] Successful", id));
	}
	
	/**
	 * 设备运营模板的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param id
	 */
	public void incrementDocumentByTemplateChanged(String id){
		logger.info(String.format("IncrementDocumentByTemplateChanged Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(id);
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_TEMPLATE.getName(), o_template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		logger.info(String.format("IncrementDocumentByTemplateChanged Request id [%s] Successful", id));
	}
	
	/**
	 * 设备运营灰度级别的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param id
	 */
	public void incrementDocumentByGraylevelChanged(String id){
		logger.info(String.format("IncrementDocumentByGraylevelChanged Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		
		WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(id);
		if(wifiDeviceGray != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), String.valueOf(wifiDeviceGray.getGl()));
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), String.valueOf(GrayLevel.Other.getIndex()));
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		logger.info(String.format("IncrementDocumentByGraylevelChanged Request id [%s] Successful", id));
	}
	
	/**
	 * 设备的共享网络的变更
	 * 变更涉及的更改索引字段是
	 * 1) d_snk_type
	 * 2) d_snk_template
	 * 3) d_snk_turnstate
	 */
	public void incrementDocumentBySharedNetworkChanged(String id) {
		logger.info(String.format("IncrementDocumentBysharedNetworkChanged Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		
		String d_snk_turnstate = WifiDeviceDocumentEnumType.SnkTurnStateEnum.Off.getType();
		WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(id);
		if(wifiDeviceSharedNetwork != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TYPE.getName(), wifiDeviceSharedNetwork.getSharednetwork_type());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TEMPLATE.getName(), wifiDeviceSharedNetwork.getTemplate());
			SharedNetworkSettingDTO sharedNetworkSettingDto = wifiDeviceSharedNetwork.getInnerModel();
			if(sharedNetworkSettingDto != null){
				if(sharedNetworkSettingDto.isOn()){
					d_snk_turnstate = WifiDeviceDocumentEnumType.SnkTurnStateEnum.On.getType();
				}
			}
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TYPE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TEMPLATE.getName(), null);
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TURNSTATE.getName(), d_snk_turnstate);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(),  DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
		logger.info(String.format("IncrementDocumentBysharedNetworkChanged Request id [%s] Successful", id));
	}
}
