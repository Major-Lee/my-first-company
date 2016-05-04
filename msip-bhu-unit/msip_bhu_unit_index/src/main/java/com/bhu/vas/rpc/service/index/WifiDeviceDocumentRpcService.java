package com.bhu.vas.rpc.service.index;

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
import com.bhu.vas.api.rpc.index.iservice.IWifiDeviceDocumentRpcService;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.rpc.asyncprocessor.dto.AsyncWifiDeviceBlukFullIndexDTO;
import com.bhu.vas.rpc.asyncprocessor.queue.AsyncIndexProcessorQueue;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * wifidevice index rpc组件服务service 对外暴露接口
 * 处理设备上行消息
 * @author tangzichao
 *
 */
@Service("wifiDeviceDocumentRpcService")
public class WifiDeviceDocumentRpcService implements IWifiDeviceDocumentRpcService {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceDocumentRpcService.class);

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private AsyncIndexProcessorQueue asyncIndexProcessorQueue;
	/**
	 * rpc message dispatch
	 * @param type 消息类型
	 * @param payload 消息内容
	 * @param parserHeader 其他header信息
	 */
	@Override
	public void messageDispose(String id, String message) {
		logger.info(String.format("messageDispose invoke id [%s] message [%s]", id, message));
		
		try{
			System.out.println("MessageDispose recept " + id);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("messageDispose failed id [%s] message [%s]", id, message));
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("messageDispose exception id [%s] message [%s] exmsg[%s]", id, message,  ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
		
	}
	
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
		
		if(entity.getLast_reged_at() != null)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), entity.getLast_reged_at().getTime());
		
		DeviceVersion parser = DeviceVersion.parser(entity.getOrig_swver());
		if(parser != null){
			String dut = parser.getDut();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), dut);
			DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, entity.getHdtype());
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
	 * 设备位置发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_address
	 * 2) d_geopoint
	 * @param id 设备mac
	 * @param lat 纬度
	 * @param lon 经度
	 * @param d_address 详细地址
	 */
	@Override
	public void locaitionUpdIncrement(String id, double lat, double lon, String d_address){
		logger.info(String.format("LocaitionUpdIncrement Request id [%s] d_address [%s]", id, d_address));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_GEOPOINT.getName(), new double[]{lon, lat});
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ADDRESS.getName(), d_address);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 设备上线发生变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_type_sname
	 * 7) d_lastregedat
	 * 8) d_dut
	 * @param entitys 设备实体集合
	 */
	@Override
	public void onlineMultiUpsertIncrement(List<WifiDevice> entitys){
		if(entitys == null) return;
		int size = entitys.size();
		logger.info(String.format("OnlineMultiUpdIncrement Request size [%s]", entitys.size()));
		if(size <= 0) return;
		
		List<String> ids = new ArrayList<String>();
		List<Map<String, Object>> sourceMaps = new ArrayList<Map<String, Object>>();
		String updatedat = DateTimeHelper.getDateTime();
		for(WifiDevice entity : entitys){
			if(entity == null || StringUtils.isEmpty(entity.getId())) continue;
			ids.add(entity.getId());
			
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.ID.getName(), entity.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), entity.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), entity.getOrig_swver());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGMODEL.getName(), entity.getOrig_model());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), entity.getWork_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), entity.getConfig_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), entity.getHdtype());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WANIP.getName(), entity.getWan_ip());
//			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
			if(entity.getLast_reged_at() != null)
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), entity.getLast_reged_at().getTime());
			
			DeviceVersion parser = DeviceVersion.parser(entity.getOrig_swver());
			if(parser != null){
				String dut = parser.getDut();
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), dut);
				DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, entity.getHdtype());
				if(deviceUnitType != null){
					sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE_SNAME.getName(), deviceUnitType.getSname());
				}
			}
			if(entity.getCreated_at() != null)
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CREATEDAT.getName(), entity.getCreated_at().getTime());

			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), updatedat);
			sourceMaps.add(sourceMap);
		}
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, true, true, true);
	}
	
	/**
	 * 设备认领上线处理或首次上线，按照全字段重建覆盖标准
	 * @param entity
	 */
/*	@Deprecated
	@Override
	public void onlineCrdIncrement(WifiDevice entity){
		if(entity == null) return;
		logger.info(String.format("OnlineCrdIncrement Request id [%s]", entity.getId()));
		String mac = entity.getId();
		if(StringUtils.isEmpty(entity.getId())) return;
		
		WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
		WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
		AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(entity.getSn());
		long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);
		User agentUser = null;
		if(entity.getAgentuser() > 0){
			agentUser = userService.getById(entity.getAgentuser());
		}
		
		User bindUser = null;
		Integer bindUserId = userDeviceService.fetchBindUid(mac);
		if(bindUserId != null && bindUserId > 0){
			bindUser = userService.getById(bindUserId);
		}
		WifiDeviceDocument doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(entity, deviceModule, agentDeviceClaim, 
				wifiDeviceGray, bindUser, agentUser, (int)hoc);
		
		wifiDeviceDataSearchService.getRepository().save(doc);
	}*/
	
	/**
	 * 设备绑定或解绑的变更
	 * 变更涉及的更改索引字段是
	 * 1) u_id
	 * 2) u_nick
	 * 3) u_mno
	 * 4) u_mcc
	 * 5) u_type
	 * 6) u_binded
	 * @param id 设备mac
	 * @param bindUser 如果为null表示解绑设备
	 */
/*	@Override
	public void bindUserUpdIncrement(String id, User bindUser){
		logger.info(String.format("bindUserUpdIncrement Request id [%s] bindUser [%s]", id, bindUser));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		if(bindUser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), bindUser.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), bindUser.getNick());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), bindUser.getMobileno());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), bindUser.getCountrycode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), bindUser.getUtype());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UBinded.getType());
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocumentEnumType.UBindedEnum.UNOBinded.getType());
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}*/
	
	/**
	 * 设备运营模板的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param id
	 * @param o_template 运营模板编号
	 */
	@Override
	public void templateUpdIncrement(String id, String o_template){
		logger.info(String.format("TemplateUpdIncrement Request id [%s] o_template [%s]", id, o_template));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_TEMPLATE.getName(), o_template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 设备运营模板的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param ids 设备mac的集合
	 * @param o_template
	 */
	@Override
	public void templateMultiUpdIncrement(List<String> ids, String o_template){
		logger.info(String.format("TemplateMultiUpdIncrement Request ids [%s] o_template [%s]", ids, o_template));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_TEMPLATE.getName(), o_template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}

	/**
	 * 设备运营灰度级别的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param id
	 * @param o_graylevel
	 */
	@Override
	public void graylevelUpdIncrement(String id, String o_graylevel){
		logger.info(String.format("GraylevelUpdIncrement Request id [%s] o_graylevel [%s]", id, o_graylevel));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), o_graylevel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 设备运营灰度级别的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param ids 设备mac的集合
	 * @param o_graylevel
	 */
	@Override
	public void graylevelMultiUpdIncrement(List<String> ids, String o_graylevel){
		logger.info(String.format("GraylevelMultiUpdIncrement Request ids [%s] o_graylevel [%s]", ids, o_graylevel));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), o_graylevel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}
	
	/**
	 * 设备的终端数量的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_hoc
	 * @param ids 设备mac的集合
	 * @param hocs 设备的终端数量的集合
	 */
	@Override
	public void hocMultiUpdIncrement(List<String> ids, List<Integer> hocs){
		logger.info(String.format("HocMultiUpdIncrement Request ids [%s] hocs [%s]", ids, hocs));
		if(ids == null || ids.isEmpty()) return;
		if(hocs == null || hocs.isEmpty()) return;
		if(ids.size() != hocs.size()) return;
		
		List<Map<String, Object>> sourceMaps = new ArrayList<Map<String, Object>>();
		String updatedat = DateTimeHelper.getDateTime();
		for(Integer hoc : hocs){
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), hoc);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), updatedat);
			sourceMaps.add(sourceMap);
		}
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, false, true, true);
	}

	/**
	 * 设备的共享网络的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_snk_type
	 */
	@Override
	public void sharedNetworkMultiUpdIncrement(List<String> ids, String sharedNetwork_type, String template) {
		logger.info(String.format("SharedNetworkMultiUpdIncrement Request ids [%s] d_snk_type [%s] d_snk_template [%s]", ids, sharedNetwork_type, template));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TYPE.getName(), sharedNetwork_type);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TEMPLATE.getName(), template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(),  DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}

	/**
	 * 设备的共享网络的变更
	 * 变更涉及的更改索引字段是
	 * 1) d_snk_type
	 * @param id
	 * @param sharedNetwork_type
	 * @param template
	 */
	@Override
	public void sharedNetworkUpdIncrement(String id, String sharedNetwork_type, String template) {
		logger.info(String.format("sharedNetworkUpdIncrement Request id [%s] d_snk_type [%s] d_snk_template [%s]", id, sharedNetwork_type, template));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TYPE.getName(), sharedNetwork_type);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SHAREDNETWORK_TEMPLATE.getName(), template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(),  DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 批量打包全量覆盖索引
	 * @param macs
	 */
	@Override
	public void blukIndexs(List<String> macs) {
		logger.info(String.format("blukIndexs Request macs [%s]", macs));
		if(macs == null || macs.isEmpty()) return;
		System.out.println("blukIndexs Request macs");
		AsyncWifiDeviceBlukFullIndexDTO async_action_dto = new AsyncWifiDeviceBlukFullIndexDTO();
		async_action_dto.setMacs(macs);
		async_action_dto.setTs(System.currentTimeMillis());
		asyncIndexProcessorQueue.addQueue(async_action_dto);
	}
}
