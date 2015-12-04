package com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * wifi设备增量索引service
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceIndexIncrementService {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementService.class);
	
	//@Resource
	//private WifiDeviceIndexService wifiDeviceIndexService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
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
	private UserDeviceService userDeviceService;
	
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
	public void locaitionUpdIncrement(String id, double lat, double lon, String d_address){
		logger.info(String.format("LocaitionUpdIncrement Request id [%s] d_address [%s]", id, d_address));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_GEOPOINT.getName(), new double[]{lon, lat});
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ADDRESS.getName(), d_address);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
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
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), WifiDeviceDocument.D_MOnline_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGVAPMODULE.getName(), d_origvapmodule);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_OPERATE.getName(), WifiDeviceDocument.O_Operate_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
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
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocument.D_Online_False);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_MODULEONLINE.getName(), WifiDeviceDocument.D_MOnline_False);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_UPTIME.getName(), d_uptime);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTLOGOUTAT.getName(), d_lastlogoutat);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_lastregedat
	 * 7) d_dut
	 * @param entity
	 */
	public void onlineUpdIncrement(WifiDevice entity){
		if(entity == null || StringUtils.isEmpty(entity.getId())) return;
		
		logger.info(String.format("OnlineUpdIncrement Request id [%s] d_origswver [%s] d_workmodel [%s] d_configmodel [%s] d_type [%s] d_lastregedat [%s]",
				entity.getId(), entity.getOrig_swver(), entity.getWork_mode(), entity.getConfig_mode(), 
				entity.getHdtype(), entity.getLast_reged_at()));
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocument.D_Online_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), entity.getOrig_swver());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), entity.getWork_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), entity.getConfig_mode());
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), entity.getHdtype());
//		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
		if(entity.getLast_reged_at() != null)
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), entity.getLast_reged_at().getTime());
		
		DeviceVersion parser = DeviceVersion.parser(entity.getOrig_swver());
		if(parser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), parser.getDut());
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.updateIndex(entity.getId(), sourceMap, true, true);
	}
	
	/**
	 * 设备上线发生变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_lastregedat
	 * 7) d_dut
	 * @param entitys 设备实体集合
	 */
	public void onlineMultiUpdIncrement(List<WifiDevice> entitys){
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
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ONLINE.getName(), WifiDeviceDocument.D_Online_True);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), entity.getOrig_swver());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), entity.getWork_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_CONFIGMODEL.getName(), entity.getConfig_mode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_TYPE.getName(), entity.getHdtype());
//			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
			if(entity.getLast_reged_at() != null)
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_LASTREGEDAT.getName(), entity.getLast_reged_at().getTime());
			
			DeviceVersion parser = DeviceVersion.parser(entity.getOrig_swver());
			if(parser != null){
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_DEVICEUNITTYPE.getName(), parser.getDut());
			}
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), updatedat);
			sourceMaps.add(sourceMap);
		}
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, true, true);
	}
	
	/**
	 * 设备认领上线处理，按照全字段重建覆盖标准
	 * @param entity
	 */
	public void onlineClaimCrdIncrement(WifiDevice entity){
		if(entity == null) return;
		logger.info(String.format("OnlineCrdIncrement Request id [%s]", entity.getId()));
		String mac = entity.getId();
		if(StringUtils.isEmpty(entity.getId())) return;
		
		WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
		WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
		AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(mac);
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
	 * @param id 设备mac
	 * @param bindUser 如果为null表示解绑设备
	 */
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
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocument.U_Binded_True);
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_NICK.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILENO.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_MOBILECOUNTRYCODE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_TYPE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field.U_BINDED.getName(), WifiDeviceDocument.U_Binded_False);
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备运营模板的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param id
	 * @param o_template 运营模板编号
	 */
	public void templateUpdIncrement(String id, String o_template){
		logger.info(String.format("TemplateUpdIncrement Request id [%s] o_template [%s]", id, o_template));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_TEMPLATE.getName(), o_template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备运营模板的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param ids 设备mac的集合
	 * @param o_template
	 */
	public void templateMultiUpdIncrement(List<String> ids, String o_template){
		logger.info(String.format("TemplateMultiUpdIncrement Request ids [%s] o_template [%s]", ids, o_template));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_TEMPLATE.getName(), o_template);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, true, true);
	}
	
	/**
	 * 设备运营灰度级别的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param id
	 * @param o_graylevel
	 */
	public void graylevelUpdIncrement(String id, String o_graylevel){
		logger.info(String.format("GraylevelUpdIncrement Request id [%s] o_graylevel [%s]", id, o_graylevel));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), o_graylevel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备运营灰度级别的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param ids 设备mac的集合
	 * @param o_graylevel
	 */
	public void graylevelMultiUpdIncrement(List<String> ids, String o_graylevel){
		logger.info(String.format("GraylevelMultiUpdIncrement Request ids [%s] o_graylevel [%s]", ids, o_graylevel));
		if(ids == null || ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.O_GRAYLEVEL.getName(), o_graylevel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMap, true, true);
	}
	
	/**
	 * 设备的终端数量的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_hoc
	 * @param ids 设备mac的集合
	 * @param hocs 设备的终端数量的集合
	 */
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
		wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, true, true);
	}

}
