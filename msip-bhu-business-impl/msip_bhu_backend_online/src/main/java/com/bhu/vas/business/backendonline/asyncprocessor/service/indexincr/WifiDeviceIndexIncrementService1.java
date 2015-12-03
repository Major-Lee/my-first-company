package com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
import com.bhu.vas.business.search.model.WifiDeviceDocument1;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService1;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * wifi设备增量索引service
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceIndexIncrementService1 {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementService1.class);
	
	//@Resource
	//private WifiDeviceIndexService wifiDeviceIndexService;
	@Resource
	private WifiDeviceDataSearchService1 wifiDeviceDataSearchService1;
	
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
			
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_GEOPOINT.getName(), new double[]{lon, lat});
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_ADDRESS.getName(), d_address);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService1.updateIndex(id, sourceMap, true, true);
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
			
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_MODULEONLINE.getName(), WifiDeviceDocument1.D_MOnline_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_ORIGVAPMODULE.getName(), d_origvapmodule);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.O_OPERATE.getName(), WifiDeviceDocument1.O_Operate_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService1.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备下线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_monline
	 * 3) d_uptime
	 * 4) d_lastlogoutat
	 * @param id 设备mac
	 * @param d_uptime 设备运行总时长
	 * @param d_lastlogoutat 设备的最后下线的时间
	 */
	public void offlineUpdIncrement(String id, String d_uptime, long d_lastlogoutat){
		logger.info(String.format("OfflineUpdIncrement Request id [%s] d_uptime [%s] d_lastlogoutat [%s]", id, d_uptime, d_lastlogoutat));
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_ONLINE.getName(), WifiDeviceDocument1.D_Online_False);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_MODULEONLINE.getName(), WifiDeviceDocument1.D_MOnline_False);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_UPTIME.getName(), d_uptime);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_LASTLOGOUTAT.getName(), d_lastlogoutat);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService1.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_hoc
	 * 7) d_lastregedat
	 * 8) d_dut
	 * @param id 设备mac
	 * @param d_origswver 设备原始软件版本号
	 * @param d_workmodel 设备工作模式
	 * @param d_configmodel 设备配置模式
	 * @param d_type 设备类型
	 * @param d_lastregedat 设备最新的上线时间
	 */
	public void onlineUpdIncrement(String id, String d_origswver, String d_workmodel, String d_configmodel, 
			String d_type, long d_lastregedat){
		logger.info(String.format("OnlineUpdIncrement Request id [%s] d_origswver [%s] d_workmodel [%s] d_configmodel [%s] d_type [%s] d_lastregedat [%s]",
					id, d_origswver, d_workmodel, d_configmodel, d_type, d_lastregedat));

		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_ONLINE.getName(), WifiDeviceDocument1.D_Online_True);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_ORIGSWVER.getName(), d_origswver);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_WORKMODEL.getName(), d_workmodel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_CONFIGMODEL.getName(), d_configmodel);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_TYPE.getName(), d_type);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_HANDSETONLINECOUNT.getName(), 0);
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_LASTREGEDAT.getName(), d_lastregedat);
		DeviceVersion parser = DeviceVersion.parser(d_origswver);
		if(parser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.D_DEVICEUNITTYPE.getName(), parser.getDut());
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.UPDATEDAT.getName(), DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService1.updateIndex(id, sourceMap, true, true);
	}
	
	/**
	 * 设备首次上线处理，按照全字段重建覆盖标准
	 * @param entity
	 */
	public void onlineCrdIncrement(WifiDevice entity){
		if(entity == null) return;
		
		logger.info(String.format("OnlineCrdIncrement Request id [%s]", entity.getId()));
		
		String mac = entity.getId();
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
		WifiDeviceDocument1 doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(entity, deviceModule, agentDeviceClaim, 
				wifiDeviceGray, bindUser, agentUser, (int)hoc);
		
		wifiDeviceDataSearchService1.getRepository().save(doc);
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
	public void bindUserCrdIncrement(String id, User bindUser){
		logger.info(String.format("BindUserCrdIncrement Request id [%s] bindUser [%s]", id, bindUser));
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		if(bindUser != null){
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_ID.getName(), bindUser.getId());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_NICK.getName(), bindUser.getNick());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_MOBILENO.getName(), bindUser.getMobileno());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_MOBILECOUNTRYCODE.getName(), bindUser.getCountrycode());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_TYPE.getName(), bindUser.getUtype());
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_BINDED.getName(), WifiDeviceDocument1.U_Binded_True);
		}else{
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_ID.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_NICK.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_MOBILENO.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_MOBILECOUNTRYCODE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_TYPE.getName(), null);
			sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.U_BINDED.getName(), WifiDeviceDocument1.U_Binded_False);
		}
		sourceMap.put(BusinessIndexDefine.WifiDevice.Field1.UPDATEDAT.getName(), DateTimeHelper.getDateTime());

		wifiDeviceDataSearchService1.updateIndex(id, sourceMap, true, true);
	}

}
