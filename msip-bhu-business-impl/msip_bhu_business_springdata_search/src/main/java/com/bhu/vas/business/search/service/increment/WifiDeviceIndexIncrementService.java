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
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * wifi设备增量索引service
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceIndexIncrementService implements IWifiDeviceIndexIncrement{
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementService.class);
	
	//@Resource
	//private WifiDeviceIndexService wifiDeviceIndexService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
/*	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private WifiDeviceGrayService wifiDeviceGrayService;
	
	@Resource
	private AgentDeviceClaimService agentDeviceClaimService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserDeviceService userDeviceService;*/
	
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
	 * 设备模块上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_monline
	 * 2) d_origvapmodule
	 * 3) o_operate
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	@Deprecated
	@Override
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
	@Deprecated
	@Override
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
	 * 创建在导入的确认的设备数据multi
	 * @param importId 导入批次
	 * @param agentDeviceClaims
	 */
	@Deprecated
	@Override
	public void batchConfirmMultiUpsertIncrement(long importId, List<AgentDeviceClaim> agentDeviceClaims){

		if(agentDeviceClaims == null || agentDeviceClaims.isEmpty()) return;
		
		logger.info(String.format("BatchConfirmMultiCrdIncrement Request importid [%s] size [%s]", importId, agentDeviceClaims.size()));
		
//		List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
		List<String> ids = new ArrayList<String>();
		List<Map<String, Object>> sourceMaps = new ArrayList<Map<String, Object>>();
		for(AgentDeviceClaim agentDeviceClaim : agentDeviceClaims){
			if(agentDeviceClaim == null || StringUtils.isEmpty(agentDeviceClaim.getMac())) continue;
			ids.add(agentDeviceClaim.getMac());
			
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.ID.getName(), agentDeviceClaim.getMac());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), agentDeviceClaim.getMac());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_SN.getName(), agentDeviceClaim.getId());
			
			String[] parserHdtypes = VapEnumType.DeviceUnitType.parserIndex(agentDeviceClaim.getHdtype());
			if(parserHdtypes != null && parserHdtypes.length == 2){
				String dut = parserHdtypes[0];
				String hdtype = parserHdtypes[1];
				if(!StringUtils.isEmpty(hdtype)){
					sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), hdtype);
					DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, hdtype);
					if(deviceUnitType != null){
						sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE_SNAME.getName(), deviceUnitType.getSname());
					}
				}
			}
			
			if(agentDeviceClaim.getSold_at() != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CREATEDAT.getName(), agentDeviceClaim.getSold_at().getTime());
			}
			
			if(agentDeviceClaim.getImport_id() > 0){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_BATCH.getName(), agentDeviceClaim.getImport_id());
			}
			sourceMaps.add(sourceMap);
		}
		
		if(!ids.isEmpty()){
			wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, true, true, true);
		}
		//wifiDeviceDataSearchService.bulkIndex(docs, true, true);
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
	 */
	@Deprecated
	@Override
	public void onlineUpdIncrement(WifiDevice entity){
		if(entity == null || StringUtils.isEmpty(entity.getId())) return;
		
		logger.info(String.format("OnlineUpdIncrement Request id [%s] d_origswver [%s] d_workmodel [%s] d_configmodel [%s] d_type [%s] d_lastregedat [%s]",
				entity.getId(), entity.getOrig_swver(), entity.getWork_mode(), entity.getConfig_mode(), 
				entity.getHdtype(), entity.getLast_reged_at()));
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), entity.getOrig_swver());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), entity.getWork_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), entity.getConfig_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), entity.getHdtype());
//		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
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
		wifiDeviceDataSearchService.updateIndex(entity.getId(), sourceMap, false, true, true);
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
	 * 更新设备的批次号
	 * 变更涉及的更改索引字段是
	 * 1) o_batch
	 * @param id
	 * @param importId
	 * @param agentUser
	 */
	@Override
	public void agentUpdIncrement(String id, long importId, User agentUser){
		logger.info(String.format("AgentUpdIncrement Request id [%s] importId [%s]", id, importId));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_BATCH.getName(), importId);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		if(agentUser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_ID.getName(), agentUser.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_NICK.getName(), agentUser.getNick());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_ORG.getName(), agentUser.getOrg());
		}
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	/**
	 * 批量更新设备的批次号multi
	 * 变更涉及的更改索引字段是
	 * 1) o_batch
	 * @param ids
	 * @param importId
	 * @param agentUser
	 */
	@Override
	public void agentMultiUpdIncrement(List<String> ids, long importId, User agentUser){
		logger.info(String.format("AgentMultiUpdIncrement Request ids [%s] importId [%s]", ids, importId));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_BATCH.getName(), importId);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		if(agentUser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_ID.getName(), agentUser.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_NICK.getName(), agentUser.getNick());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.A_ORG.getName(), agentUser.getOrg());
		}
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
}
