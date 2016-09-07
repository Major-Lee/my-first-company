package com.bhu.vas.business.search.service.increment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * wifi设备增量索引状态的service,同步进行处理，需要数据实时性强的可以使用此service
 * 只在设备状态发生变化时触发的索引增量处理
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceStatusIndexIncrementService{
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceStatusIndexIncrementService.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	/**
	 * 设备模块上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_monline
	 * 2) d_origvapmodule
	 * 3) o_operate
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	public void moduleOnlineUpdIncrement(String id, String d_origvapmodule){
		logger.info(String.format("ModuleOnlineUpdIncrement Request id [%s] d_origvapmodule [%s]", id, d_origvapmodule));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), WifiDeviceDocumentEnumType.MOnlineEnum.MOnline.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGVAPMODULE.getName(), d_origvapmodule);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_OPERATE.getName(), WifiDeviceDocumentEnumType.OperateEnum.Operate.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 设备下线发生变更
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
	public void offlineUpdIncrement(String id, String d_uptime, long d_lastlogoutat){
		logger.info(String.format("OfflineUpdIncrement Request id [%s] d_uptime [%s] d_lastlogoutat [%s]", id, d_uptime, d_lastlogoutat));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocumentEnumType.OnlineEnum.Offline.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), WifiDeviceDocumentEnumType.MOnlineEnum.MOffline.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_UPTIME.getName(), d_uptime);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTLOGOUTAT.getName(), d_lastlogoutat);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	

	/**
	 * 设备上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_type_sname
	 * 7) d_lastregedat
	 * 8) d_dut
	 * @param entity
	 * @param newWifi
	 */
	public void onlineUpsertIncrement(WifiDevice entity, boolean newWifi){
		if(entity == null || StringUtils.isEmpty(entity.getId())) return;
		
		logger.info(String.format("OnlineUpsertIncrement Request id [%s] d_origswver [%s] d_workmodel [%s] d_configmodel [%s] d_type [%s] d_lastregedat [%s]",
				entity.getId(), entity.getOrig_swver(), entity.getWork_mode(), entity.getConfig_mode(), 
				entity.getHdtype(), entity.getLast_reged_at()));
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.ID.getName(), entity.getId());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SN.getName(), entity.getSn());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), entity.getId());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), entity.getOrig_swver());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGMODEL.getName(), entity.getOrig_model());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), entity.getWork_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), entity.getConfig_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), entity.getHdtype());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WANIP.getName(), entity.getWan_ip());
//		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
		if(newWifi)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), VapEnumType.GrayLevel.Other.getIndex());
		
		if(entity.getFirst_reged_at() != null)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_FIRSTREGEDAT.getName(), entity.getFirst_reged_at().getTime());

		if(entity.getLast_reged_at() != null)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), entity.getLast_reged_at().getTime());
		
		DeviceVersion parser = DeviceVersion.parser(entity.getOrig_swver());
		if(parser != null){
			String st = parser.getSt();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), st);
			DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromVersionPrefix(st, parser.getMn(), entity.getHdtype());
			if(deviceUnitType != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE_SNAME.getName(), deviceUnitType.getSname());
			}
		}
		if(entity.getCreated_at() != null)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CREATEDAT.getName(), entity.getCreated_at().getTime());
			
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(entity.getId(), sourceMap, true, true, true);
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
	 * @param bindUser 如果为null表示解绑设备
	 * @param bindUserDNick 用户绑定的设备的昵称
	 * @param d_industry 设备的行业信息
	 */
	public void bindUserUpdIncrement(String id, User bindUser, String bindUserDNick, String d_industry){
		logger.info(String.format("BindUserUpdIncrement Request id [%s] bindUser [%s] bindUserDNick [%s] d_industry [%s]", id, bindUser, bindUserDNick, d_industry));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		if(bindUser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), bindUser.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), bindUser.getNick());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), bindUser.getMobileno());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), bindUser.getCountrycode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), bindUser.getUtype());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UBinded.getType());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), bindUserDNick);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_INDUSTRY.getName(), d_industry);
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
	
	/**
	 * 用户设置绑定的设备的昵称
	 * 变更涉及的更改索引字段是
	 * 1) u_dnick
	 * @param id 设备mac
	 * @param bindUserDNick 用户绑定的设备的昵称
	 */
	public void bindUserDNickUpdIncrement(String id, String bindUserDNick){
		logger.info(String.format("BindUserDNickUpdIncrement Request id [%s] bindUserDNick [%s]", id, bindUserDNick));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), bindUserDNick);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 清除设备的搜索引擎记录的相关数据
	 * 1：绑定数据关系
	 * 2：设备业务信息
	 * @param id
	 */
	public void resetUpdIncrement(String id){
		logger.info(String.format("ResetUpdIncrement Request id [%s]", id));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UNOBinded.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_INDUSTRY.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.T_UC_EXTENSION.getName(), null);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 用户设置绑定的设备的tags
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * @param d_tags 设备tags
	 */
	public void bindDTagsUpdIncrement(String id, String d_tags){
		logger.info(String.format("BindDTagsUpdIncrement Request id [%s] d_tags [%s]", id, d_tags));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TAGS.getName(), d_tags);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 用户设置绑定的设备的tags multi
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * @param d_tags 设备tags
	 */
	public void bindDTagsMultiUpdIncrement(List<String> ids, List<String> d_tags){
		logger.info(String.format("bindDTagsMultiUpdIncrement Request ids [%s] d_tags [%s]", ids, d_tags));
		if(ids == null || ids.isEmpty()) return;
		if(d_tags == null || d_tags.isEmpty()) return;
		if(ids.size() != d_tags.size()) return;
		
		List<Map<String, Object>> sourceMaps = new ArrayList<Map<String, Object>>();
		String updatedat = DateTimeHelper.getDateTime();
		for(String d_tag : d_tags){
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TAGS.getName(), d_tag);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), updatedat);
			sourceMaps.add(sourceMap);
		}
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, false, true, true);
	}
	
	/**
	 * 用户设置绑定的设备的tags multi
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * @param d_tag 设备tags
	 */
	public void bindDTagsMultiUpdIncrement(List<String> ids, String d_tag){
		logger.info(String.format("bindDTagsMultiUpdIncrement Request ids [%s] d_tag [%s]", ids, d_tag));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TAGS.getName(), d_tag);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}
	
	/**
	 * 设备的ucloud业务搜索字段的变更
	 * 变更涉及的更改索引字段是
	 * 1) t_uc_extension
	 * @param ids
	 * @param t_uc_extension
	 */
	public void ucExtensionMultiUpdIncrement(List<String> ids, String t_uc_extension) {
		logger.info(String.format("UcExtensionMultiUpdIncrement Request ids [%s] t_uc_extension [%s]", ids, t_uc_extension));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.T_UC_EXTENSION.getName(), t_uc_extension);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(),  DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}
	
}
