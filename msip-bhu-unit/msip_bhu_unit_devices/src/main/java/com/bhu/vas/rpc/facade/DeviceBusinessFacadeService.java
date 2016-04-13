package com.bhu.vas.rpc.facade;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.HandsetLogDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.dto.redis.SerialTaskDTO;
import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.ModifyDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.ModuleReturnDTO;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.dto.ret.QuerySysinfoSerialReturnDTO;
import com.bhu.vas.api.dto.ret.QueryWifiTimerSerialReturnDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
import com.bhu.vas.api.dto.ret.WifiDevicePeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRateDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceStatusDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceVapReturnDTO;
import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.bhu.vas.api.dto.ret.param.ParamVasPluginDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingSyskeyDTO;
import com.bhu.vas.api.dto.statistics.DeviceStatistics;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceStatus;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceLocationSerialTaskService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceVisitorService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceAlarmService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiDeviceStatusService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceBusinessFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceBusinessFacadeService.class);

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private WifiDeviceAlarmService wifiDeviceAlarmService;
	
	@Resource
	private WifiDeviceStatusService wifiDeviceStatusService;
	
	/*@Resource
	private HandsetDeviceService handsetDeviceService;*/
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	//@Resource
	//private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserFacadeService userFacadeService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	/**
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线状态Redis更新
	 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
	 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 5:统计增量 wifi设备的daily启动次数增量(backend)
	 */
	public void wifiDeviceOnline(String ctx, String payload) {
		WifiDeviceDTO dto = RPCMessageParseHelper.generateDTOFromMessage(payload, WifiDeviceDTO.class);
		
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		//wifi设备是否是新设备
		boolean newWifi = false;
		boolean wanIpChanged = false;
		//boolean workModeChanged = false;
		//String currentWorkmode = dto.getWork_mode();
		String oldWorkmode = null;
		//wifi设备上一次登录时间
		long last_login_at = 0;
		
		//WifiDevice wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
		//wifi_device_entity.setLast_reged_at(new Date());
		String wifiId = dto.getMac().toLowerCase();
		
		//2:wifi设备在线状态Redis更新
		WifiDevicePresentCtxService.getInstance().addPresent(wifiId, ctx);
		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = wifiDeviceService.getById(wifiId);
		WifiDeviceModule wifi_device_module = wifiDeviceModuleService.getOrCreateById(wifiId);
		Date reged_at = new Date();
		if(wifi_device_entity == null){
			wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
			wifi_device_entity.setOnline(true);
			wifi_device_entity.setLast_logout_at(reged_at);
			wifiDeviceService.insert(wifi_device_entity);
			newWifi = true;
			wanIpChanged = true;
		}else{
			String oldWanIp = wifi_device_entity.getWan_ip();
			oldWorkmode = wifi_device_entity.getWork_mode();
			oldWorkmode = wifi_device_entity.getWork_mode();
			//wifi_device_entity.setCreated_at(exist_wifi_device_entity.getCreated_at());
			BeanUtils.copyProperties(dto, wifi_device_entity);
			wifi_device_entity.setLast_reged_at(reged_at);
			wifi_device_entity.setOnline(true);
			wifiDeviceService.update(wifi_device_entity);
			if(StringUtils.isNotEmpty(wifi_device_entity.getWan_ip()) && !wifi_device_entity.getWan_ip().equals(oldWanIp)){
				wanIpChanged = true;
			}
			/*if(StringUtils.isNotEmpty(wifi_device_entity.getWork_mode()) && !wifi_device_entity.getWork_mode().equals(oldWorkMode)){
				workModeChanged = true;
			}*/
		}
		wifi_device_module.setOnline(true);
		wifi_device_module.setLast_module_reged_at(reged_at);
		wifiDeviceModuleService.update(wifi_device_module);
		//设备上线增量索引
		wifiDeviceStatusIndexIncrementService.onlineUpsertIncrement(wifi_device_entity, newWifi);
		//本次wifi设备登录时间
		long this_login_at = wifi_device_entity.getLast_reged_at().getTime();
		boolean needLocationQuery = false;
		if(wanIpChanged || StringUtils.isEmpty(wifi_device_entity.getLat()) || StringUtils.isEmpty(wifi_device_entity.getLon())){
			needLocationQuery = true;
		}
		/*
		 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
		 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
		 * 5:统计增量 wifi设备的daily启动次数增量(backend)
		 */
		deliverMessageService.sendWifiDeviceOnlineActionMessage(wifi_device_entity.getId(), dto.getJoin_reason(),
				this_login_at, last_login_at, newWifi,wanIpChanged,needLocationQuery/*, workModeChanged*/,oldWorkmode,dto.getWork_mode());
	}
	
	/**
	 * CM与控制层的连接断开以后 会分批次批量发送在此CM上的wifi设备在线信息 (backend)
	 * @param ctx
	 * @param dtos
	 */
	public void cmupWithWifiDeviceOnlines(String ctx, List<WifiDeviceDTO> devices) {
		deliverMessageService.sendCMUPWithWifiDeviceOnlinesActionMessage(ctx, devices);
	}
	
	/**
	 * wifi设备离线
	 * 1:wifi设备基础信息表中的在线状态更新为离线
	 * 2:wifi设备在线状态redis移除
	 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
	 * 4:wifi设备对应handset在线列表redis清除 (backend)
	 * 5:统计增量 wifi设备的daily访问时长增量 (backend) TODO:wifi设备由于基本不离线, 可能需要通过后台定时程序每天进行时长结算
	 * @param ctx
	 * @param wifiId
	 */
	public void wifiDeviceOffline(String ctx, String wifiId) {
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(wifiId)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		String lowercase_wifi_id = wifiId.toLowerCase();

		String ctx_present = WifiDevicePresentCtxService.getInstance().getPresent(lowercase_wifi_id);
		if(ctx.equals(ctx_present)){
			//1:wifi设备基础信息表中的在线状态更新
			WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(lowercase_wifi_id);
			WifiDeviceModule exist_wifi_device_module = wifiDeviceModuleService.getOrCreateById(lowercase_wifi_id);
			
			if(exist_wifi_device_entity != null){
				Date logoutDate =new Date(); 
				exist_wifi_device_entity.setOnline(false);
				exist_wifi_device_entity.setLast_logout_at(logoutDate);
				//wifi设备上次登录的时间
				long last_login_at = exist_wifi_device_entity.getLast_reged_at().getTime();
				long current_at = System.currentTimeMillis();
				//5:统计增量 wifi设备的daily访问时长增量
				if(last_login_at > 0){
					long uptime = current_at - last_login_at;
					if(uptime > 0){
						deviceFacadeService.deviceStatisticsOffline(uptime, DeviceStatistics.Statis_Device_Type);
						
						String entity_uptime = exist_wifi_device_entity.getUptime();
						BigInteger bi = new BigInteger(String.valueOf(uptime));
						BigInteger entity_bi = null;
						if(!StringUtils.isEmpty(entity_uptime)){
							entity_bi = new BigInteger(entity_uptime);
							bi = bi.add(entity_bi);
						}
						exist_wifi_device_entity.setUptime(bi.toString());
					}
				}
				wifiDeviceService.update(exist_wifi_device_entity);
				
				exist_wifi_device_module.setOnline(false);
				exist_wifi_device_module.setModule_online(false);
				exist_wifi_device_module.setLast_module_logout_at(logoutDate);
				wifiDeviceModuleService.update(exist_wifi_device_module);
				//2:wifi设备在线状态redis移除 TODO:多线程情况可能下，设备先离线再上线，两条消息并发处理，如果上线消息先完成，可能会清除掉有效数据
				WifiDevicePresentCtxService.getInstance().removePresent(lowercase_wifi_id);
				//设备离线增量索引
				wifiDeviceStatusIndexIncrementService.offlineUpdIncrement(wifiId, exist_wifi_device_entity.getUptime(), 
						exist_wifi_device_entity.getLast_logout_at().getTime());
				/*
				 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
				 * 4:wifi设备对应handset在线列表redis清除 (backend)
				 * 5:统计增量 wifi设备的daily访问时长增量 (backend)
				 */
				//wifi设备上次登录的时间
				//long last_login_at = exist_wifi_device_entity.getLast_reged_at().getTime();
				
				deliverMessageService.sendWifiDeviceOfflineActionMessage(lowercase_wifi_id);
			}
		}
	}
	
	
	/**
	 * wifi设备不存在
	 * @param ctx
	 * @param mac
	 */
	public void wifiDeviceNotExist(String ctx, String wifiId) {
		wifiDeviceOffline(ctx, wifiId);
	}
	
	/**
	 * wifi设备告警信息
	 * 1:告警信息存入告警信息表中
	 * TODO: 
	 * 	a:老版本会发送 告警信息 (包括终端上线等)
	 *  b:新版本默认是不发送的，也可以通过配置开启
	 * 告警消息暂不做处理
	 * @param ctx
	 * @param payload
	 */
	public void wifiDeviceAlarm(String ctx, String payload) {
//		WifiDeviceAlarmDTO dto = RPCMessageParseHelper.generateDTOFromMessage(payload, WifiDeviceAlarmDTO.class);
//		
//		if(StringUtils.isEmpty(dto.getMac_addr()) || StringUtils.isEmpty(ctx))
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
//
//		WifiDeviceAlarm wifi_device_alarm_entity = BusinessModelBuilder.wifiDeviceAlarmDtoToEntity(dto);
//		wifiDeviceAlarmService.insert(wifi_device_alarm_entity);
	}
	/**
	 * 通过连接设备进行的绑定设备操作
			a、如果绑定的key值不存在提示错误信息
			b、设备未在服务器被绑定的情况下，收到设备的绑定消息后，进行绑定操作，并反馈给设备当前设备绑定的手机号并提示相关信息
			c、设备已经在服务器被绑定的情况下，收到设备的绑定消息后，不进行绑定操作，但需要告诉设备当前设备绑定的手机号（可能通过app绑定过），
			并提示相关信息
	 * @param ctx
	 * @param payload
	 */
	public void wifiDeviceDirectBind(String ctx, String payload, ParserHeader parserHeader){
		String mac = parserHeader.getMac().toLowerCase();
		String keynum = StringHelper.EMPTY_STRING_GAP;
		String keystatus = WifiDeviceSettingSyskeyDTO.KEY_STATUS_VALIDATE_FAILED;
		String industry = StringHelper.EMPTY_STRING_GAP;
		WifiDeviceSettingSyskeyDTO dto = null;
		try{
			dto = RPCMessageParseHelper.generateDTOFromMessage(payload, WifiDeviceSettingSyskeyDTO.class);
			if(dto != null){
				//mobileno
				keynum = dto.getKeynum();
				industry = dto.getIndustry();
				if(StringUtils.isNotEmpty(mac)){
					/*int uid = Integer.parseInt(keynum);
			    	User user = userService.getById(uid);*/
					WifiDeviceDocument wifiDeviceDoc = wifiDeviceDataSearchService.searchById(mac);
					if(wifiDeviceDoc != null){
						String exist_uid = wifiDeviceDoc.getU_id();
						if(StringUtils.isNotEmpty(exist_uid)){
			    			keynum = wifiDeviceDoc.getU_mno();
			    			industry = wifiDeviceDoc.getD_industry();
			    			keystatus = WifiDeviceSettingSyskeyDTO.KEY_STATUS_SUCCESSED;
						}else{
							if(StringUtils.isNotEmpty(keynum)){
								User user = userFacadeService.getUserByMobileno(keynum);
								if(user != null){
									String bindDeviceName = deviceFacadeService.getBindDeviceName(mac);
									
						    		UserDevice userDevice = new UserDevice();
							        userDevice.setId(new UserDevicePK(mac, user.getId()));
							        userDevice.setDevice_name(bindDeviceName);
							        userDevice.setCreated_at(new Date());
							        userDeviceService.insert(userDevice);
							        
						    		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
						    		if(wifiDevice != null){
								        wifiDevice.setIndustry(industry);
								        wifiDeviceService.update(wifiDevice);
						    		}
							        
							        wifiDeviceStatusIndexIncrementService.bindUserUpdIncrement(mac, user, bindDeviceName, industry);
									keystatus = WifiDeviceSettingSyskeyDTO.KEY_STATUS_SUCCESSED;
								}
							}
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			keystatus = WifiDeviceSettingSyskeyDTO.KEY_STATUS_FAILED;
		}finally{
			if(dto != null && StringUtils.isNotEmpty(keynum)){
				dto.setKeynum(keynum);
				dto.setKeystatus(keystatus);
				dto.setIndustry(industry);
				String cmdPayload = CMDBuilder.builderDeviceSettingModify(mac, 0, 
						DeviceHelper.builderDSKeyStatusOuter(dto));
				deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, cmdPayload);
			}
		}
	}
	
	/**
	 * 移动设备连接状态请求
	 * 1:online
	 * 2:offline
	 * 3:sync
	 * @param ctx
	 * @param payload
	 * modified by Edmond Lee for handset storage
	 */
	public void handsetDeviceConnectState(String ctx, String payload, ParserHeader parserHeader) {
		//HandsetDeviceDTO dto = RPCMessageParseHelper.generateDTOFromMessage(payload, HandsetDeviceDTO.class);
		List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage(payload, 
				HandsetDeviceDTO.class);
		if(dtos == null || dtos.isEmpty()) return;
		for(HandsetDeviceDTO dto:dtos){
			dto.setLast_wifi_id(parserHeader.getMac().toLowerCase());
			dto.setTs(System.currentTimeMillis());
		}
		HandsetDeviceDTO fristDto = dtos.get(0);
		if(HandsetDeviceDTO.Action_Online.equals(fristDto.getAction())){
			//date: 2015-10-27 新增访客网络认证
			handsetDeviceOnline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
			//System.out.println("acition offline ====");
			handsetDeviceOffline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Sync.equals(fristDto.getAction())){
			handsetDeviceSync(ctx, parserHeader.getMac(), dtos);
		}
		else if(HandsetDeviceDTO.Action_Update.equals(fristDto.getAction())){
			handsetDeviceUpdate(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Authorize.equals(fristDto.getAction())){
			//todo(bluesand)
			handsetDeviceVisitorAuthorize(ctx, fristDto, parserHeader.getMac());
		}
		else{
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_MESSAGE_UNSUPPORT);
		}
	}

	/**
	 * 是否访客网络
	 * @param dto
	 * @return
	 */
	private boolean isVisitorWifi(String ctx, HandsetDeviceDTO dto) {
		return HandsetDeviceDTO.VAPNAME_WLAN3.equals(dto.getVapname()) && !HandsetDeviceDTO.PROTAL_NONE.equals(dto.getPortal());
	}

	private boolean isVisitorWifi(WifiDeviceTerminalDTO dto) {
		return HandsetDeviceDTO.VAPNAME_WLAN3.equals(dto.getVapname()) && !HandsetDeviceDTO.PROTAL_NONE.equals(dto.getPortal());
	}

	/**
	 * 访客网络终端上线
	 * @param ctx
	 * @param dto
	 * @param wifiId
	 */
	private void handsetDeviceVisitorOnline(String ctx, HandsetDeviceDTO dto, String wifiId) {

		String wifiId_lowerCase = wifiId.toLowerCase();
		//System.out.println("handsetDeviceVisitorOnline HandsetDeviceDTO isAuthorized handset[" + dto.getMac() + "],wifiId[" + wifiId + "],==" + dto.getAuthorized());
		if (StringHelper.TRUE.equals(dto.getAuthorized())) {
			WifiDeviceVisitorService.getInstance().addAuthOnlinePresent(wifiId_lowerCase, System.currentTimeMillis(), dto.getMac());
		} else {
			WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent(wifiId_lowerCase, dto.getMac());
		}
	}

	private void handsetDeviceVisitorOnline(String ctx, WifiDeviceTerminalDTO dto, String wifiId) {
		String wifiId_lowerCase = wifiId.toLowerCase();
		//System.out.println("handsetDeviceVisitorOnline WifiDeviceTerminalDTO isAuthorized handset["+ dto.getMac() +"],wifiId[" +wifiId + "],=="+ dto.getAuthorized());
		if (StringHelper.TRUE.equals(dto.getAuthorized())) {
			WifiDeviceVisitorService.getInstance().addAuthOnlinePresent(wifiId_lowerCase, System.currentTimeMillis(), dto.getMac());
		} else {
			WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent(wifiId_lowerCase, dto.getMac());
		}
	}

	/**
	 * 访客网络终端下线
	 *
	 *
	 * 访客网络下线后idle_timeout和force_timeout内都属于离线状态,认证超时后会认为是下线状态
	 *
	 * @param ctx
	 * @param dto
	 * @param wifiId
	 */
	private void handsetDeviceVisitorOffline(String ctx, HandsetDeviceDTO dto, String wifiId) {
		String wifiId_lowerCase = wifiId.toLowerCase();
//		System.out.println("handsetDeviceVisitorOffline WifiDeviceTerminalDTO isAuthorized handset["+ dto.getMac() +"],wifiId[" +wifiId + "],=="+ dto.getAuthorized());
		if (StringHelper.TRUE.equals(dto.getAuthorized())) {
			WifiDeviceVisitorService.getInstance().addVisitorOfflinePresent(wifiId_lowerCase, dto.getMac());
		} else {
			WifiDeviceVisitorService.getInstance().removePresent(wifiId_lowerCase, dto.getMac());
		}

	}

	/**
	 * 清除访客网络列表
	 * @param wifiId
	 */
	private void clearDeviceVisitorList(String wifiId) {
		String wifiId_lowerCase = wifiId.toLowerCase();
		WifiDeviceVisitorService.getInstance().clearPresent(wifiId_lowerCase);
	}

	private void handsetDeviceVisitorAuthorize(String ctx, HandsetDeviceDTO dto, String wifiId) {
		if(dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);

		String wifiId_lowerCase = wifiId.toLowerCase();
//		System.out.println("handsetDeviceVisitorAuthorize isAuthorized" + StringHelper.TRUE.equals(dto.getAuthorized()));
		if (StringHelper.TRUE.equals(dto.getAuthorized())) {
			WifiDeviceVisitorService.getInstance().addAuthOnlinePresent(wifiId_lowerCase, System.currentTimeMillis(), dto.getMac());
			deliverMessageService.sendHandsetDeviceVisitorAuthorizeOnlineMessage(wifiId_lowerCase, dto.getMac(), dto.getTs());
		} else { //踢掉
			//WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent(wifiId_lowerCase, dto.getMac());
			WifiDeviceVisitorService.getInstance().removePresent(wifiId_lowerCase, dto.getMac());
		}
	}

	/**
	 * 访客网络设备上线
	 * 1:移动设备基础信息更新
	 *
	 * 移动设备上线
	 * 1:移动设备基础信息更新
	 * 2:wifi设备对应handset在线列表redis添加，在终端上线后需要清除掉以前dhcpname和ip
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend) removed
	 * 5:wifi设备接入移动设备的接入数量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		//移动设备是否是新设备
		boolean newHandset = false;
		//移动设备上一次登录时间
		long last_login_at = 0;
		//1:移动设备基础信息更新
		String wifiId_lowerCase = wifiId.toLowerCase();
		//System.out.println("message:"+JsonHelper.getJSONString(dto));
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(dto.getMac().toLowerCase());
		long this_login_at = System.currentTimeMillis();
		//HandsetDevice handset_device_entity = handsetDeviceService.getById(dto.getMac().toLowerCase());
		if(handset == null){
			//System.out.println("before update message:null");
			last_login_at = this_login_at;
			dto.setLast_wifi_id(wifiId_lowerCase);
			dto.setTs(this_login_at);
			HandsetStorageFacadeService.handsetComming(dto);
			newHandset = true;
		}else{
			//System.out.println("before update message:"+JsonHelper.getJSONString(handset));
			last_login_at = handset.getTs();
			handset.setLast_wifi_id(wifiId_lowerCase);
			handset.setTs(this_login_at);
			handset.setAction(HandsetDeviceDTO.Action_Online);
			handset.setChannel(dto.getChannel());
			handset.setSsid(dto.getSsid());
			handset.setBssid(dto.getBssid());
			handset.setPhy_rate(dto.getPhy_rate());
			handset.setVapname(dto.getVapname());
			handset.setRssi(dto.getRssi());
			handset.setSnr(dto.getSnr());
			handset.setEthernet(dto.getEthernet());
			handset.setAuthorized(dto.getAuthorized());
			//在终端上线后需要清除掉以前dhcpname和ip,由于上线消息中没有dhcpname和ip，所以这些值在上线时都是空，直接用
			if(!StringUtils.isEmpty(dto.getDhcp_name())){
				handset.setDhcp_name(dto.getDhcp_name());
			}
			if(!StringUtils.isEmpty(dto.getIp())){
				handset.setIp(dto.getIp());
			}
			handset.setIp(dto.getIp());
			HandsetStorageFacadeService.handsetComming(handset);
		}
		boolean isNew4This = false;
		int result_status = HandsetStorageFacadeService.wifiDeviceHandsetOnline(wifiId_lowerCase, dto.getMac(), this_login_at);//wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(),new Date(dto.getLogin_ts()));
		if(result_status == HandsetLogDTO.Element_NewHandset)
			isNew4This = true;
		if(isVisitorWifi(ctx, dto)) { //访客网络
			handsetDeviceVisitorOnline(ctx, dto, wifiId);
		} else {
			//2:wifi设备对应handset在线列表redis添加
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId_lowerCase, dto.getMac(),
					dto.fetchData_rx_rate_double());
		/*
		 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
		 * 4:移动设备连接wifi设备的流水log (backend)
		 * 5:wifi设备接入移动设备的接入数量 (backend)
		 * 6:统计增量 移动设备的daily新增用户或活跃用户增量
		 * 7:统计增量 移动设备的daily启动次数增量(backend)
		 */
			deliverMessageService.sendHandsetDeviceOnlineActionMessage(wifiId_lowerCase, dto.getMac(),
					this_login_at, last_login_at, newHandset,isNew4This);
		}

	}

	/**
	 *
	 * 访客网络移动设备下线
	 * 1:更新移动设备的online状态为false
	 *
	 *
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * 2:wifi设备对应handset在线列表redis移除
	 * 3:统计增量 移动设备的daily访问时长增量 (backend)
	 * @param ctx
	 * @param dto
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto, String wifiId){
//		System.out.println("HandsetStorageFacadeService.wifiDeviceHandsetOffline" + wifiId);
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);

		String lowercase_mac = wifiId.toLowerCase();
		String lowercase_d_mac = dto.getMac().toLowerCase();
		//1:更新移动设备的online状态为false
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(lowercase_d_mac);
//		System.out.println("HandsetStorageFacadeService.wifiDeviceHandsetOffline 0" + JsonHelper.getJSONString(dto) + "===" + isVisitorWifi(ctx, dto));
		if(handset != null) {
			//dto.setVapname(handset.getVapname());
			dto.setIp(handset.getIp());
			dto.setDhcp_name(handset.getDhcp_name());
			dto.setData_tx_rate(handset.getData_tx_rate());
			dto.setData_rx_rate(handset.getData_rx_rate());
		}
		HandsetStorageFacadeService.handsetComming(dto);
//		System.out.println("HandsetStorageFacadeService.wifiDeviceHandsetOffline 1" + JsonHelper.getJSONString(dto) + "===" + isVisitorWifi(ctx, dto));
		//修改为redis实现终端上下线日志 2015-12-11 从backend 移植过来 20160121
		HandsetStorageFacadeService.wifiDeviceHandsetOffline(lowercase_mac, lowercase_d_mac, dto.getTx_bytes(), dto.getTs());
//		System.out.println("HandsetStorageFacadeService.wifiDeviceHandsetOffline 2" + JsonHelper.getJSONString(dto)  + "===" + isVisitorWifi(ctx, dto));
		if(isVisitorWifi(ctx, dto)) { //访客网络
			handsetDeviceVisitorOffline(ctx, dto, wifiId);
		} else {
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(lowercase_mac,
					lowercase_d_mac, dto.fetchData_rx_rate_double());
			deliverMessageService.sendHandsetDeviceOfflineActionMessage(lowercase_mac,
					lowercase_d_mac, dto.getUptime(), dto.getRx_bytes(), dto.getTx_bytes());
		}
	}
	
	/**
	 * 更新终端的hostname和ip地址
	 * @param ctx
	 * @param dto
	 * @param wifiId
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceUpdate(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) /*|| StringUtils.isEmpty(dto.getDhcp_name())*/ || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		String lowercase_d_mac = dto.getMac().toLowerCase();
		//1:更新终端的hostname
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(lowercase_d_mac);
		if(handset != null){
			handset.setAction(HandsetDeviceDTO.Action_Online);
			handset.setDhcp_name(dto.getDhcp_name());
			handset.setIp(dto.getIp());
			handset.setLast_wifi_id(dto.getLast_wifi_id());
			HandsetStorageFacadeService.handsetComming(handset);
		}
	}
	
	/**
	 * 移动设备连接状态sync
	 * 1:清除wifi设备对应handset在线列表redis 并重新写入 (backend)
	 * 2:移动设备基础信息更新 (backend)
	 * 3:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 4:统计增量 移动设备的daily启动次数增量(backend)
	 * 	a:如果移动设备目前不在线或者不存在移动设备数据，则执行设备上线相同操作
	 * 		1:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 		2:移动设备连接wifi设备的流水log (backend)
	 * 		3:wifi设备接入移动设备的接入数量 (backend)
	 * 5. 终端
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String mac, List<HandsetDeviceDTO> dtos){
		if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		//deliverMessageService.sendHandsetDeviceSyncActionMessage(wifiId.toLowerCase(), dtos);
		//1:清除wifi设备对应handset在线列表redis
		deviceFacadeService.allHandsetDoOfflines(mac);
		//清除访客网络列表
		clearDeviceVisitorList(mac);
		if(dtos != null && !dtos.isEmpty()){
			List<String> allIds = new ArrayList<String>();
			//过滤访客网络，默认网络下的终端
			List<HandsetDeviceDTO> defaultWlanDTOs = new ArrayList<HandsetDeviceDTO>();
			for(HandsetDeviceDTO dto : dtos){
				String lowerCaseMac = dto.getMac().toLowerCase();
				allIds.add(lowerCaseMac);
				if(!isVisitorWifi(ctx, dto)) {
					defaultWlanDTOs.add(dto);
				}
			}
			//1
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(allIds);
			int cursor = 0;
			for(HandsetDeviceDTO handset : handsets){
				HandsetDeviceDTO dto = dtos.get(cursor);
				if(handset != null){
					//dto.setDhcp_name(handset.getDhcp_name());
					//dto.setIp(handset.getIp());
					dto.setData_tx_rate(handset.getData_tx_rate());
					dto.setData_rx_rate(handset.getData_rx_rate());
				}
				String handsetId = dto.getMac().toLowerCase();
				//1:wifi设备对应handset在线列表redis 重新写入
				//WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(mac, handsetId, data_rx_rate);

				if(isVisitorWifi(ctx, dto)) { //访客网络
					handsetDeviceVisitorOnline(ctx, dto, mac);
				} else {
					WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(mac,
							handsetId, dto.fetchData_rx_rate_double());
				}
				//修改为redis实现终端上下线日志 2015-12-11 从backend 移植过来 20160121
				HandsetStorageFacadeService.wifiDeviceHandsetOnline(mac, dto.getMac(), dto.getTs());//wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(),new Date(dto.getLogin_ts()));
				cursor++;
			}
			HandsetStorageFacadeService.handsetsComming(dtos);
			//相关统计数据，业务日志，终端接入流水日志，访客网络不统计
			deliverMessageService.sendHandsetDeviceSyncActionMessage(mac, defaultWlanDTOs);
		}
	}
	
	
	/******************           task response         *************************/
	
	/**
	 * 获取wifi设备地理位置任务响应处理
	 * 1:记录wifi设备的坐标 (backend)
	 * 2:根据坐标提取地理位置详细信息 (backend)
	 * 3:任务callback
	 * @param ctx
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceLocationS2(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			LocationDTO locationDto = RPCMessageParseHelper.generateDTOFromQueryDeviceLocationS2(doc);
			if(locationDto != null && locationDto.validate()){
				deliverMessageService.sendQueryDeviceLocationActionMessage(wifiId, locationDto.getLat(), locationDto.getLon());
			}
		}
		//2:任务callback
		doTaskCallback(taskid, serialDto.getStatus(),response);
	}
	
	/**
	 * 以设备notify的方式获取地理位置
	 * @param ctx
	 * @param doc
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceLocationNotify(String ctx, Document doc, QuerySerialReturnDTO serialDto, 
			String wifiId, long taskid){
		LocationDTO locationDto = RPCMessageParseHelper.generateDTOFromQueryDeviceLocationS2(doc);
		if(locationDto != null && locationDto.validate()){
			deliverMessageService.sendQueryDeviceLocationActionMessage(wifiId, locationDto.getLat(), locationDto.getLon());
		}
	}
	
	/**
	 * 以设备notify的方式获取网速
	 * @param ctx
	 * @param doc
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceSpeedNotify(String ctx, Document doc, QuerySerialReturnDTO serialDto, 
			String wifiId, long taskid){
/*		String rate = serialDto.getRate();
		if(StringUtils.isEmpty(rate) || "0".equals(rate)) return;
		WifiDeviceRealtimeRateStatisticsStringService.getInstance().addPeak(wifiId, rate);*/
		WifiDevicePeakSectionDTO dto = RPCMessageParseHelper.generateDTOFromQuerySpeedTest(doc);
		if(dto != null){
			WifiDeviceRxPeakSectionDTO rx_dto = dto.getRx_dto();
			if(rx_dto != null){
				WifiDeviceRealtimeRateStatisticsStringService.getInstance().appendRxPeakSection(wifiId, rx_dto);
			}
			WifiDeviceTxPeakSectionDTO tx_dto = dto.getTx_dto();
			if(tx_dto != null){
				WifiDeviceRealtimeRateStatisticsStringService.getInstance().appendTxPeakSection(wifiId, tx_dto);
			}
		}
	}
	
	/**
	 * 以设备notify的方式获取设备的实时速率
	 * @param ctx
	 * @param doc
	 * @param serialDto
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceRateNotify(String ctx, Document doc, QuerySerialReturnDTO serialDto, 
			String wifiId, long taskid){
		//如果设备实时速率报送完成 则直接清除waiting mark 以便于下发下次的设备速率查询指令
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().removeRateWaiting(wifiId);
		}
		
		WifiDeviceRateDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceRate(doc);
		if(dto != null){
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().addRate(wifiId, dto.getTx_rate(), dto.getRx_rate());
		}
	}
	
	/**
	 * 以设备notify的方式获取设备的终端实时速率
	 * 暂时去除掉 设备终端所有都下线 再 根据列表重新上线的机制，
	 * 不做额外的上下线操作
	 * 不做终端实体的状态更新
	 * @param ctx
	 * @param doc
	 * @param serialDto
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceTerminalsNotify(String ctx, Document doc, QuerySerialReturnDTO serialDto, 
			String wifiId, long taskid){
		List<WifiDeviceTerminalDTO> terminals = RPCMessageParseHelper.generateDTOFromQueryDeviceTerminals(doc);
		/*if(CMDBuilder.auto_special_query_commercial_terminals_taskid_fragment.wasInFragment(taskid)){
			//TODO:特殊处理商业wifi终端在线列表
			return;
		}*/
		//1:清除wifi设备对应handset在线列表redis
		//deviceFacadeService.allHandsetDoOfflines(wifiId);
		//清除访客网络列表
		//clearDeviceVisitorList(wifiId);
		//long this_login_at = System.currentTimeMillis();
		if(terminals != null && !terminals.isEmpty()){
			//获取设备的配置的dto
			WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(wifiId);
			if(setting_entity == null) return;
			WifiDeviceSettingDTO setting_entity_dto = setting_entity.getInnerModel();
			
			List<String> hdIds = new ArrayList<String>();
			for(WifiDeviceTerminalDTO terminal : terminals){
				hdIds.add(terminal.getMac());
			}
			//1:更新被管理的终端的上下行速率和ssid bssid
			//int cursor = 0;
			//List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(hdIds);
			//for(HandsetDeviceDTO handset : handsets){
			for(int cursor = 0; cursor<terminals.size();cursor++){
				WifiDeviceTerminalDTO terminal = terminals.get(cursor);
				//判断是否在黑名单中
				if(DeviceHelper.isAclMac(terminal.getMac(), setting_entity_dto)) 
					continue;
				/*if(handset == null){
					handset = new HandsetDeviceDTO();
					handset.setMac(terminal.getMac());
					handset.setAction(HandsetDeviceDTO.Action_Online);
					handset.setTs(this_login_at);
					handset.setLast_wifi_id(wifiId);
				}else{
					handset.setAction(HandsetDeviceDTO.Action_Online); // 清除的时候设置成 offline了
					handset.setData_tx_rate(terminal.getData_tx_rate());
					handset.setData_rx_rate(terminal.getData_rx_rate());
				}
<<<<<<< HEAD
				//1:更新被管理的终端的上下行速率和ssid bssid
				int cursor = 0;
				List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(hdIds);
				for(HandsetDeviceDTO handset : handsets){
					WifiDeviceTerminalDTO terminal = terminals.get(cursor);
					//判断是否在黑名单中
					if(DeviceHelper.isAclMac(terminal.getMac(), setting_entity_dto)) 
						continue;
					if(handset == null){
						handset = new HandsetDeviceDTO();
						handset.setMac(terminal.getMac());
						handset.setAction(HandsetDeviceDTO.Action_Online);
						handset.setTs(this_login_at);
						handset.setLast_wifi_id(wifiId);
					}else{
						handset.setAction(HandsetDeviceDTO.Action_Online); // 清除的时候设置成 offline了
						handset.setData_tx_rate(terminal.getData_tx_rate());
						handset.setData_rx_rate(terminal.getData_rx_rate());
					}
					//修改终端的流量
					logger.info("terminal" + terminal.getMac() + terminal.getRx_bytes() + terminal.getTx_bytes());
					handset.setRx_bytes(terminal.getRx_bytes());
					handset.setTx_bytes(terminal.getTx_bytes());
					logger.info("handset" + handset.getMac() + handset.getRx_bytes() + handset.getTx_bytes());
					if(isVisitorWifi(terminal)) {
						handsetDeviceVisitorOnline(ctx, terminal, wifiId);
					} else {
						WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId,
								terminal.getMac(), StringUtils.isEmpty(terminal.getData_tx_rate()) ? 0d : Double.parseDouble(terminal.getData_tx_rate()));
					}
					//修改为redis实现终端上下线日志 2015-12-11 从backend 移植过来 20160121 很频繁
					HandsetStorageFacadeService.wifiDeviceHandsetOnline(wifiId, terminal.getMac(), this_login_at);
					cursor++;
				handset.setRx_bytes(terminal.getRx_bytes());
				handset.setTx_bytes(terminal.getTx_bytes());
				logger.info("handset" + handset.getMac() + handset.getRx_bytes() + handset.getTx_bytes());*/
				
				//修改终端的流量
				logger.info("terminal" + terminal.getMac() + terminal.getRx_bytes() + terminal.getTx_bytes());
				if(isVisitorWifi(terminal)) {
					handsetDeviceVisitorOnline(ctx, terminal, wifiId);
				} else {
					WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId,
							terminal.getMac(), StringUtils.isEmpty(terminal.getData_tx_rate()) ? 0d : Double.parseDouble(terminal.getData_tx_rate()));
				}
				//修改为redis实现终端上下线日志 2015-12-11 从backend 移植过来 20160121 很频繁
				//HandsetStorageFacadeService.wifiDeviceHandsetOnline(wifiId, terminal.getMac(), this_login_at);
				//cursor++;
			}
			//HandsetStorageFacadeService.handsetsComming(handsets);
		}
	}

	/**
	 * 获取wifi设备的当前状态任务响应处理 (比如cpu,内存利用率)
	 * 1:记录wifi设备的当前状态数据
	 * 2:任务callback
	 * @param ctx
	 * @param payload
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceStatus(String ctx, String response, String wifiId, long taskid){
		WifiDeviceStatusDTO dto = RPCMessageParseHelper.generateDTOFromMessage(response, WifiDeviceStatusDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(dto.getStatus())){
			//1:记录wifi设备的当前状态数据
			//WifiDeviceStatus entity = BusinessModelBuilder.wifiDeviceStatusDtoToEntity(dto);
			
			WifiDeviceStatus entity = wifiDeviceStatusService.getById(wifiId);
			if(entity == null){
				entity = BusinessModelBuilder.wifiDeviceStatusDtoToEntity(dto);
				entity.setId(wifiId);
				wifiDeviceStatusService.insert(entity);
			}else{
				BeanUtils.copyProperties(dto, entity);
				wifiDeviceStatusService.update(entity);
			}
		}
		//2:任务callback
		doTaskCallback(taskid, dto.getStatus(), response);
	}
	
	/**
	 * 获取wifi设备流量任务响应处理
	 * 1:计算并记录wifi设备的上下行流量
	 * 2:任务callback
	 * @param ctx
	 * @param payload
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceFlow(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			//1:计算并记录wifi设备的上下行流量
			List<WifiDeviceFlowDTO> dtos = RPCMessageParseHelper.generateDTOFromQueryDeviceFlow(doc);
			if(dtos != null && !dtos.isEmpty()){
				WifiDevice entity = wifiDeviceService.getById(wifiId);
				if(entity != null){
					//下行总流量 (合并多个网卡的记录)
					//long total_rx_bytes = 0;
					BigInteger total_rx_bytes = new BigInteger("0");
					//上行总流量 (合并多个网卡的记录)
					//long total_tx_bytes = 0;
					BigInteger total_tx_bytes = new BigInteger("0");
					
					for(WifiDeviceFlowDTO dto : dtos){
						//total_rx_bytes = total_rx_bytes + Long.parseLong(dto.getRx_bytes());
						total_rx_bytes = total_rx_bytes.add(new BigInteger(dto.getRx_bytes()));
						//total_tx_bytes = total_tx_bytes + Long.parseLong(dto.getTx_bytes());
						total_tx_bytes = total_tx_bytes.add(new BigInteger(dto.getTx_bytes()));
					}
					entity.setRx_bytes(total_rx_bytes.toString());
					entity.setTx_bytes(total_tx_bytes.toString());
					wifiDeviceService.update(entity);
				}
			}
		}
		//2:任务callback
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	/**
	 * 变更配置的响应处理
	 * 1：更新配置数据
	 * 2：如果设备是urouter，检查配置是否满足约定 不满足则下发修改配置
	 * 	  此下发配置为区间任务 不等回应直接修改配置数据
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void deviceSettingChanged(String ctx, String response, String wifiId, long taskid){
		WifiDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(response);
		//设备配置变成后的指令分发
		List<String> afterChangePayloads = new ArrayList<String>();
		refreshDeviceSetting(wifiId, dto, afterChangePayloads);
		//如果是dhcp模式 则下发指令查询dhcp相关数据
		String queryDHCPStatus = updateDeviceModeStatusWithMode(wifiId, dto);
		if(!StringUtils.isEmpty(queryDHCPStatus)){
			//if(afterChangePayloads == null) afterChangePayloads = new ArrayList<String>();
			afterChangePayloads.add(queryDHCPStatus);
		}
		deliverMessageService.sendDeviceSettingChangedActionMessage(wifiId, afterChangePayloads);
	}
	/**
	 * 获取设备配置
	 * 1：更新配置数据
	 * 2：如果设备是urouter，检查配置是否满足约定 不满足则下发修改配置
	 * 	  此下发配置为区间任务 不等回应直接修改配置数据
	 * 3:根据设备的mode更新status数据
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceSetting(String ctx, String response, String wifiId, long taskid){
		//System.out.println("~~~~~~~~~~~~~1:mac:"+wifiId);
		WifiDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(response);
		//获取设备配置之后的指令分发
		List<String> afterQueryPayloads = new ArrayList<String>();
		int refresh_status = refreshDeviceSetting(wifiId, dto,afterQueryPayloads);
		try{
			WifiDevice wifiDevice = wifiDeviceService.getById(wifiId);
			if(wifiDevice != null){
				boolean deviceURouter = false;
				//只有URouter的设备才需进行此操作
				if(WifiDeviceHelper.isURouterDevice(wifiDevice.getOrig_swver())){
					deviceURouter = true;
					/*//验证URouter设备配置是否符合约定
					if(!DeviceHelper.validateURouterBlackList(dto)){
						//if(afterQueryPayloads == null) afterQueryPayloads = new ArrayList<String>();
						String modify_urouter_acl = DeviceHelper.builderDSURouterDefaultVapAndAcl(dto);
						afterQueryPayloads.add(CMDBuilder.builderDeviceSettingModify(wifiId, 
								CMDBuilder.auto_taskid_fragment.getNextSequence(), modify_urouter_acl));
					}*/
					//如果是uRouter 插件更新下发策略
					//查询配置上报的过程中，配置>0的情况下,并且 不存在指定的plugin
					if("H106".equals(wifiDevice.getHdtype())){//uRouter才下发，uRouter plus不下发
						if((StringUtils.isNotEmpty(dto.getSequence()) && Integer.parseInt(dto.getSequence()) > 0) && !dto.hasPlugin(BusinessRuntimeConfiguration.Devices_Plugin_Samba_Name)){
							String pluginCmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_Plugins,wifiId,
									CMDBuilder.auto_taskid_fragment.getNextSequence(),JsonHelper.getJSONString(ParamVasPluginDTO.builderDefaultSambaPlugin()),
									deviceCMDGenFacadeService);
							//System.out.println("~~~~~~~~~~~~~2:cmd:"+pluginCmd);
							afterQueryPayloads.add(pluginCmd);
						}
					}
					
					//System.out.println("~~~~~~~~~~~~~3:mac:"+wifiId);
					
				}
				//如果是dhcp模式 则下发指令查询dhcp相关数据
				String queryDHCPStatus = updateDeviceModeStatusWithMode(wifiId, dto);
				if(!StringUtils.isEmpty(queryDHCPStatus)){
					//if(afterQueryPayloads == null) afterQueryPayloads = new ArrayList<String>();
					afterQueryPayloads.add(queryDHCPStatus);
				}
				
				//设备持久指令分发
				List<String> persistencePayloads = deviceCMDGenFacadeService.fetchWifiDevicePersistenceExceptVapModuleCMD(wifiId);
				if((persistencePayloads != null && !persistencePayloads.isEmpty()) ||
						(afterQueryPayloads != null && !afterQueryPayloads.isEmpty())){
					if(persistencePayloads != null) afterQueryPayloads.addAll(persistencePayloads);
					//if(afterQueryPayloads != null) cmdPaylaods.addAll(afterQueryPayloads);
				}
				deliverMessageService.sendDeviceSettingQueryActionMessage(wifiId,deviceURouter, refresh_status, afterQueryPayloads);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	/**
	 * 根据设备的mode更新status数据
	 * 1:如果是dhcp 需要下发指令查询
	 * 2:如果不是dhcp的 直接从配置中获取
	 * @param wifiId
	 * @param dto
	 */
	public String updateDeviceModeStatusWithMode(String wifiId, WifiDeviceSettingDTO dto){
		//根据设备的mode更新status数据
		WifiDeviceSettingLinkModeDTO linkmode = dto.getLinkmode();
		WifiDeviceSettingModeDTO mode = dto.getMode();
		if(linkmode != null && mode != null){
			//dhcpc
			if(WifiDeviceSettingDTO.Mode_Dhcpc.equals(linkmode.getModel())){
				long taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
				if(WifiDeviceHelper.isWorkModeRouter(mode.getMode())){
					if(StringUtils.isNotEmpty(linkmode.getWan_interface())){
						return CMDBuilder.builderDhcpcStatusQuery(wifiId, taskid, linkmode.getWan_interface());
					}
				}else{
					return CMDBuilder.builderDhcpcStatusQuery(wifiId, taskid, "br-lan");
				}

//					deliverMessageService.sendWifiCmdsCommingNotifyMessage(wifiId/*, taskid, 
//							OperationCMD.QueryDhcpcStatus.getNo()*/, cmdPayload);
				
			}else{
				deviceFacadeService.updateDeviceModeStatus(wifiId, dto.getLinkmode());
			}
		}
		return null;
	}
	

	/**
	 * 全量更新设备配置数据
	 * 1：更新配置数据
	 *  2：如果设备是urouter，检查配置是否满足约定 不满足则下发修改配置
	 * 	  此下发配置为区间任务 不等回应直接修改配置数据
	 * @param mac
	 * @param dto
	 * @param changed 是否是配置变更
	 * @return
	 */
	public int refreshDeviceSetting(String mac, WifiDeviceSettingDTO dto,List<String> afterQueryPayloads){
		//System.out.println("#####################taskQueryDeviceSetting:"+dto.getRadios().get(0).getPower());
/*		boolean init_default_acl = false;
		//只有URouter的设备才需进行此操作
		if(deviceFacadeService.isURooterDevice(mac)){
			//验证URouter设备配置是否符合约定
			if(!DeviceHelper.validateURouterBlackList(dto)){
				//modify_urouter_acl = DeviceHelper.builderDSURouterDefaultVapAndAcl(dto);
				init_default_acl = true;
			}
		}*/
		int state = DeviceHelper.RefreashDeviceSetting_Normal;
		if(dto.getBoot_on_reset() == WifiDeviceHelper.Boot_On_Reset_Happen) {
			state = DeviceHelper.RefreashDeviceSetting_RestoreFactory;
		}
		//todo(bluesand)新增&&更新 配置
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity == null){
			entity = new WifiDeviceSetting();
			entity.setId(mac);
			entity.putInnerModel(dto);
			wifiDeviceSettingService.insert(entity);
		}else{
/*			WifiDeviceSettingDTO currentDto = entity.getInnerModel();
			if(currentDto != null){
				String current_sequence = currentDto.getSequence();
				if(StringUtils.isNotEmpty(current_sequence) && StringUtils.isNotEmpty(dto.getSequence())){
					//如果获取的设备配置序列号小于当前序列号 认为是设备恢复出厂
					if(Integer.parseInt(dto.getSequence()) < Integer.parseInt(current_sequence)){
						state = DeviceHelper.RefreashDeviceSetting_RestoreFactory;
					}
				}
				//List<WifiDeviceSettingMMDTO> mms = currentDto.getMms();
			}*/
/*			if(dto.getBoot_on_reset() == WifiDeviceHelper.Boot_On_Reset_NotHappen){
				try{
					WifiDeviceSettingDTO currentDto = entity.getInnerModel();
					int switchAct = needGenerate4WorkModeChanged(currentDto, dto);
					logger.info(String.format("device[%s] workModeChanged switchAct[%s]", mac,switchAct));
					if(switchAct != WifiDeviceHelper.SwitchMode_NoAction){
						//模式切换需要下发的指令集合
						afterQueryPayloads.addAll(cmdGenerate4WorkModeChanged(mac,switchAct));
					}
					
					int switchAct = BusinessMarkerService.getInstance().deviceWorkmodeChangedStatusGetAndClear(mac);
					if(switchAct != WifiDeviceHelper.SwitchMode_NoAction){
						//特殊处理 切换工作模式以后 直接合并要修改的配置信息到数据库, 因为同步配置下发采用taskid为0
						WifiDeviceSettingDTO currentDto = entity.getInnerModel();
						if(currentDto != null){
							//String new_mode = dto.getMode().getMode();
							DeviceHelper.mergeDSOnWorkModeChanged(currentDto, dto);
							//dto.getMode().setMode(new_mode);
						}
						//模式切换需要下发的指令集合
						afterQueryPayloads.addAll(cmdGenerate4WorkModeChanged(mac,switchAct));
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}else{
				//如果设备reset了,要去掉同步设备配置的标记 避免重置以后，又恢复了配置
				BusinessMarkerService.getInstance().deviceWorkmodeChangedStatusClear(mac);
			}*/
			entity.putInnerModel(dto);
			wifiDeviceSettingService.update(entity);
		}
		//检查设备配置中的设备绑定数据是否与服务器一致，如果不一致，下发数据同步配置
		//checkSyskey(mac, dto);
		//如果不符合urouter的配置约定 则下发指定修改配置
/*		if(!StringUtils.isEmpty(modify_urouter_acl)){
			deliverMessageService.sendActiveDeviceSettingModifyActionMessage(mac, modify_urouter_acl);
		}*/
		//deliverMessageService.sendDeviceSettingChangedActionMessage(mac, init_default_acl);
		return state;
	}
	
	/**
	 * 检查设备配置中的设备绑定数据是否与服务器一致，如果不一致，下发数据同步配置
	 * @param mac
	 * @param dto
	 */
/*	public void checkSyskey(String mac, WifiDeviceSettingDTO dto){
		String cmdPayload = null;
		WifiDeviceSettingSyskeyDTO syskey_dto = dto.getSyskey();
		if(syskey_dto != null){
			String keynum = syskey_dto.getKeynum();
			//String keystatus = syskey_dto.getKeystatus();
			
			Integer uid = userDeviceService.fetchBindUid(mac);
			if(uid == null){
				if(StringUtils.isNotEmpty(keynum)){
					cmdPayload = CMDBuilder.builderDeviceSettingModify(mac, 0, 
							DeviceHelper.builderDSKeyStatusOuter(new WifiDeviceSettingSyskeyDTO(
									StringHelper.EMPTY_STRING_GAP, WifiDeviceSettingSyskeyDTO.KEY_STATUS_SUCCESSED)));
					
				}
			}else{
				User user = userService.getById(uid);
				if(user != null){
					if(StringUtils.isNotEmpty(user.getMobileno())){
						if(!user.getMobileno().equals(keynum)){
							cmdPayload = CMDBuilder.builderDeviceSettingModify(mac, 0, 
									DeviceHelper.builderDSKeyStatusOuter(new WifiDeviceSettingSyskeyDTO(
											user.getMobileno(), WifiDeviceSettingSyskeyDTO.KEY_STATUS_SUCCESSED)));
						}
					}
				}
			}
			
			if(StringUtils.isNotEmpty(cmdPayload))
				deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, cmdPayload);
		}
	}*/
	
	/**
	 * 获取配置信息进行设备是否切换工作模式的判断
	 * 如果切换工作模式，需要下发部分配置同步信息
	 * 如果切换工作模式，特殊处理 切换工作模式以后 直接合并要修改的配置信息到数据库
	 * 	//1、访客网络指令
		//2、wifi定时开关
		//3、黑名单
		//4、别名(暂时不需要下发指令)
		//5、限速
		//6、功率
	 * @param currentDto
	 * @param new_dto
	 * @return
	 */
/*	private int needGenerate4WorkModeChanged(WifiDeviceSettingDTO currentDto, WifiDeviceSettingDTO new_dto){
		if(currentDto != null){
			WifiDeviceSettingModeDTO current_mode_dto = currentDto.getMode();
			if(current_mode_dto != null && StringUtils.isNotEmpty(current_mode_dto.getMode())){
				WifiDeviceSettingModeDTO new_mode_dto = new_dto.getMode();
				String new_mode = new_mode_dto.getMode();
				if(!current_mode_dto.getMode().equals(new_mode)){
					//特殊处理 切换工作模式以后 直接合并要修改的配置信息到数据库
					DeviceHelper.mergeDS(currentDto, new_dto);
					new_dto.getMode().setMode(new_mode);
					if(WifiDeviceHelper.WorkMode_Router.equals(new_mode)) 
						return WifiDeviceHelper.SwitchMode_Bridge2Router_Act;
					if(WifiDeviceHelper.WorkMode_Bridge.equals(new_mode)) 
						return WifiDeviceHelper.SwitchMode_Router2Bridge_Act;
				}
			}
		}
		return WifiDeviceHelper.SwitchMode_NoAction;
	}*/
	
/*	private List<String> cmdGenerate4WorkModeChanged(String dmac,int switchAct){
		List<String> payloads = new ArrayList<String>();
		//1、访客网络指令
		//2、wifi定时开关
		{
			UserSettingState settingState = userSettingStateService.getById(dmac);
			if(settingState != null){
				UserVistorWifiSettingDTO vistorWifi = settingState.getUserSetting(UserVistorWifiSettingDTO.Setting_Key, UserVistorWifiSettingDTO.class);
				if(vistorWifi != null && vistorWifi.isOn()){
					//TODO:ParamVapVistorWifiDTO block_mode变更并且更新配置 或者数据库中就不存ParamVapVistorWifiDTO字段block_mode
					vistorWifi.getVw().switchWorkMode(switchAct);
					payloads.add(CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_VistorWifi_Start, dmac, 
							0l,JsonHelper.getJSONString(vistorWifi.getVw()),deviceFacadeService));
				}
				UserWifiTimerSettingDTO timerWifi = settingState.getUserSetting(UserWifiTimerSettingDTO.Setting_Key, UserWifiTimerSettingDTO.class);
				if(timerWifi != null && timerWifi.isOn()){
					ParamCmdWifiTimerStartDTO dto = timerWifi.toParamCmdWifiTimerStartDTO();
					if(dto != null){
						payloads.add(CMDBuilder.autoBuilderCMD4Opt(OperationCMD.DeviceWifiTimerStart, dmac, 
								0l, JsonHelper.getJSONString(dto)));
					}
				}			
				userSettingStateService.update(settingState);
			}
		}
		//3、ssid 密码
		//4、黑名单
		//5、别名(暂时不需要下发指令)
		//6、限速
		//7、功率
		{
			WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(dmac);
			if(setting_entity != null){
				List<String> dsworkModelChangedList = DeviceHelper.builderDSWorkModeChanged(setting_entity);
				if(dsworkModelChangedList != null && !dsworkModelChangedList.isEmpty()){
					for(String dsworkModelChanged : dsworkModelChangedList){
						payloads.add(CMDBuilder.builderDeviceSettingModify(dmac, 0l, dsworkModelChanged));
					}
					
				}
			}
		}
		return payloads;
	}*/
	
	/**
	 * 获取dhcp模式下的状态信息 (ip,网关,dns,子网掩码)
	 * 更新设备mode的状态信息
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDhcpcStatus(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			WifiDeviceSettingLinkModeDTO dto = RPCMessageParseHelper.generateDTOFromMessage(doc, WifiDeviceSettingLinkModeDTO.class);
			if(dto != null){
				dto.setModel(WifiDeviceSettingDTO.Mode_Dhcpc);
				deviceFacadeService.updateDeviceModeStatus(wifiId, dto);
			}
		}
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	public void taskQueryDeviceUsedStatus(String ctx, String response, String mac, long taskid){
		/*Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus()) 
				&& OperationCMD.QueryDeviceUsedStatus.getCmd().equals(serialDto.getCmd())){
			DeviceUsedStatisticsDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceUsedStatus(doc);
			BusinessMarkerService.getInstance().deviceUsedStatisticsSet(mac, dto);
		}*/
		//ff
		deliverMessageService.sendWifiDeviceUsedStatusActionMessage(ctx, mac, response, taskid);
	}
	
	public void taskWifiTimerStart(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QueryWifiTimerSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QueryWifiTimerSerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			WifiDeviceDownTask downTask = this.taskFacadeService.findWifiDeviceDownTaskById(taskid);
			ParamCmdWifiTimerStartDTO dto = JsonHelper.getDTO(downTask.getContext_var(), ParamCmdWifiTimerStartDTO.class);
			//更新app设置到数据库中
			UserWifiTimerSettingDTO innerDTO = new UserWifiTimerSettingDTO();
			innerDTO.setOn(true);
			innerDTO.setDs(true);
			innerDTO.setTimeslot(dto.getTimeslot());
			innerDTO.setDays(dto.getDays());
			//innerDTO.setStart(dto.getStart_time());
			//innerDTO.setEnd(dto.getEnd_time());
			userSettingStateService.updateUserSetting(wifiId, UserWifiTimerSettingDTO.Setting_Key, JsonHelper.getJSONString(innerDTO));
		}
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	public void taskWifiTimerStop(String ctx, String response, String wifiId, long taskid){
		//System.out.println("taskWifiTimerStop: ctx"+ctx+" response:"+response+" wifiid:"+wifiId+" taskid:"+taskid);
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QueryWifiTimerSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QueryWifiTimerSerialReturnDTO.class);
		//System.out.println("taskWifiTimerStop: serialDto status:"+serialDto.getStatus());
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			//WifiDeviceDownTask downTask = this.taskFacadeService.findWifiDeviceDownTaskById(taskid);
			//去除app设置到数据库中
			UserSettingState setting = userSettingStateService.getById(wifiId);
			UserWifiTimerSettingDTO timerStarSetting = setting.getUserSetting(UserWifiTimerSettingDTO.Setting_Key, UserWifiTimerSettingDTO.class);
			if(timerStarSetting == null){
				timerStarSetting = new UserWifiTimerSettingDTO();
				timerStarSetting.setTimeslot(WifiDeviceHelper.WifiTimer_Default_Timeslot);
			}
			timerStarSetting.setOn(false);
			timerStarSetting.setDs(true);
			setting.putUserSetting(timerStarSetting);
			userSettingStateService.update(setting);
			//System.out.println("taskWifiTimerStop: Setting update ok");
		}
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	public void taskWifiTimerQuery(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QueryWifiTimerSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QueryWifiTimerSerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			//WifiDeviceDownTask downTask = this.taskFacadeService.findWifiDeviceDownTaskById(taskid);
			//更新app设置到数据库中
			UserWifiTimerSettingDTO innerDTO = new UserWifiTimerSettingDTO();
			String enable = serialDto.getEnable();
			if("enable".equals(enable)){
				innerDTO.setOn(true);
			}else{
				innerDTO.setOn(false);
			}
			innerDTO.setDs(true);
			if(serialDto.hasRule()){
				innerDTO.setTimeslot(serialDto.getStart().concat("-").concat(serialDto.getEnd()));
			}else{
				innerDTO.setTimeslot(WifiDeviceHelper.WifiTimer_Default_Timeslot);
			}
			innerDTO.setDays(serialDto.getDays());
			userSettingStateService.updateUserSetting(wifiId, UserWifiTimerSettingDTO.Setting_Key, JsonHelper.getJSONString(innerDTO));
		}
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	
	public void processVapModuleResponse(String ctx,String mac, WifiDeviceVapReturnDTO vapDTO,long taskid){
		if(vapDTO != null){
			if(vapDTO.getRegister() != null){
				List<String> cmdPayloads = new ArrayList<>();
				//module 版本信息，判定是否需要升级
				WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(mac);
				if(wifiDeviceModule != null){
					//if(!vapDTO.getRegister().getVersion().equals(wifiDevice.getOrig_vap_module())){//不同则覆盖
					wifiDeviceModule.setOrig_vap_module(vapDTO.getRegister().getVersion());
					wifiDeviceModule.setOnline(true);
					wifiDeviceModule.setModule_online(true);
					wifiDeviceModuleService.update(wifiDeviceModule);
					//}
					//模块上线增量索引
					wifiDeviceStatusIndexIncrementService.moduleOnlineUpdIncrement(mac, wifiDeviceModule.getOrig_vap_module());
				}
				cmdPayloads.add(CMDBuilder.builderVapModuleRegisterResponse(mac));
				if(vapDTO.getModules() != null && !vapDTO.getModules().isEmpty()){
					//比对本地内容，看是否需要重新下发增值指令，以服务器内容为基准，所以直接生成指令下发，此部分操作设备在登录后查询配置响应的时候会做相关操作，所以这里就不做了
					List<String> persistencePayloads = deviceCMDGenFacadeService.fetchWifiDevicePersistenceVapModuleCMD(mac);
					if(persistencePayloads != null && !persistencePayloads.isEmpty()){
						cmdPayloads.addAll(persistencePayloads);
						/*deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, persistencePayloads);
						System.out.println("~~~~~~~~~~~~~~~:VapModule persistencePayloads "+persistencePayloads.size());*/
					}
				}
				if(!cmdPayloads.isEmpty()){
					deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, cmdPayloads);
					//System.out.println("~~~~~~~~~~~~~~~:VapModule persistencePayloads "+cmdPayloads.size());
				}
				deliverMessageService.sendWifiDeviceModuleOnlineMessage(mac,wifiDeviceModule.getOrig_vap_module());
			}
		}
	}
	
	/**
	 * 设备测速指令回应
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceSpeed(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		//如果返回状态为doing 表示新下发的测速指令开始执行 需清除点之前的测速分段数据
		if(WifiDeviceDownTask.State_Doing.equals(serialDto.getStatus())){
			//WifiDeviceRealtimeRateStatisticsStringService.getInstance().clearPeakSections(wifiId);
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().setPeakSections(wifiId, serialDto.getSerial());
		}
	}
	
	/**
	 * 设备系统信息指令回应
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQuerySysinfoSpeed(String ctx, String response, String wifiId, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySysinfoSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySysinfoSerialReturnDTO.class);
		//如果返回状态为doing 表示新下发的测速指令开始执行 需清除点之前的测速分段数据
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			//uptime
			WifiDevice wifiDevice = wifiDeviceService.getById(wifiId);
			wifiDevice.setTfcard_usage(serialDto.getTfcard_usage());
			try {
				String[] uptime = serialDto.getUptime().split(":");
				long last_start_at = System.currentTimeMillis() -
						((Long.parseLong(uptime[0]) * 3600 + Long.parseLong(uptime[1]) * 60 + Long.parseLong(uptime[2])) * 1000);
				wifiDevice.setLast_start_at(String.valueOf(last_start_at));
			} catch (Exception e) {
				e.printStackTrace(System.out);
				wifiDevice.setLast_start_at(String.valueOf(System.currentTimeMillis()));
			}
			wifiDeviceService.update(wifiDevice);


		}
	}
	
	/**
	 * 修改设备配置的响应处理
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskModifyDeviceSetting(String ctx, String response, String wifiId, long taskid){
		//解析返回的数据
		ModifyDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromMessage(response, ModifyDeviceSettingDTO.class);
		
		String status = null;
		//如果是成功
		if(ModifyDeviceSettingDTO.Result_Success.equals(dto.getResult())){
			status = WifiDeviceDownTask.State_Done;
		}else{
			status = WifiDeviceDownTask.State_Failed;
			//如果是配置序列号匹配错误 重新下发指令获取配置
			if(dto.isConfigSequenceMatchError()){
				long new_taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
				String cmdPayload = CMDBuilder.builderDeviceSettingQuery(wifiId, new_taskid);
				deliverMessageService.sendWifiCmdsCommingNotifyMessage(wifiId/*, new_taskid, 
						OperationCMD.QueryDeviceSetting.getNo()*/, cmdPayload);
			}
		}
		
		if(CMDBuilder.wasNormalTaskid(taskid)){
			//任务callback
			WifiDeviceDownTask task_with_paylaod = doTaskCallback(taskid, status, response);
			//如果任务数据转移出现异常 而设备配置又修改成功 也需要更新设备配置数据 从taskdown表中提取任务payload
			if(task_with_paylaod == null){
				if(WifiDeviceDownTask.State_Done.equals(status)){
					task_with_paylaod = taskFacadeService.findWifiDeviceDownTaskById(taskid);
				}
			}
			//如果任务是成功完成的 进行新配置数据合并
			if(task_with_paylaod != null && 
					(WifiDeviceDownTask.State_Done.equals(status)||WifiDeviceDownTask.State_Ok.equals(status))){
				WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
				if(entity != null){
					WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
					if(setting_dto != null){
						//新配置数据合并
						String payload = task_with_paylaod.getPayload();
						if(!StringUtils.isEmpty(dto.getConfig_sequence()) && !StringUtils.isEmpty(payload)){
							String cmdWithoutHeader = CMDBuilder.builderCMDWithoutHeader(payload);
							if(!StringUtils.isEmpty(cmdWithoutHeader)){
								WifiDeviceSettingDTO modify_setting_dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(
										cmdWithoutHeader);
								if(modify_setting_dto != null){
									DeviceHelper.mergeDS(modify_setting_dto, setting_dto);

									setting_dto.setSequence(dto.getConfig_sequence());
									entity.putInnerModel(setting_dto);
									wifiDeviceSettingService.update(entity);
									//修改配置成功的后续业务操作
									this.taskModifyDeviceSettingCompletedDeliverMessage(task_with_paylaod.getUid(), 
												wifiId, task_with_paylaod.getSubopt(), task_with_paylaod.getContext_var(),
												setting_dto, modify_setting_dto);
								}
							}
						}
					}
				}
				//通过任务的opt和subopt可以判定出什么类型的指令，进行具体操作
				
				if(task_with_paylaod != null){
					//TODO:增加共享网络设备响应处理
					OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(task_with_paylaod.getOpt());
					OperationDS ods_cmd = OperationDS.getOperationDSFromNo(task_with_paylaod.getSubopt());
					if(ods_cmd == null) return;
					if(OperationCMD.ModifyDeviceSetting == opt_cmd){
						switch(ods_cmd){
							case DS_SharedNetworkWifi_Limit:
							case DS_SharedNetworkWifi_Start:
							case DS_SharedNetworkWifi_Stop:
								sharedNetworksFacadeService.remoteResponseNotifyFromDevice(wifiId);

								clearDeviceVisitorList(wifiId);
								
								break;
							default:
								break;
						}
					}
				}
				
			}
		}else{//特殊处理，自动下发的增值指令中会修改配置
			if(WifiDeviceDownTask.State_Done.equals(status)){
				if(StringUtils.isNotEmpty(dto.getConfig_sequence())){
					WifiDeviceSetting entity = wifiDeviceSettingService.getOrCreateById(wifiId);
					WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
					setting_dto.setSequence(dto.getConfig_sequence());
					entity.putInnerModel(setting_dto);
					wifiDeviceSettingService.update(entity);
				}
				if(CMDBuilder.wasAutoSharedNetworkTaskid(taskid)){//共享网络
					//更新 t_wifi_devices_sharednetwork ds = true
					sharedNetworksFacadeService.remoteResponseNotifyFromDevice(wifiId);

					clearDeviceVisitorList(wifiId);
				}
			}
		}

/*		WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
		if(entity != null){
			WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
			if(setting_dto != null){
				ModifyDeviceSettingDTO dto = null;
				WifiDeviceDownTaskCompleted task_completed = null;
				try{
					dto = RPCMessageParseHelper.generateDTOFromMessage(response, ModifyDeviceSettingDTO.class);
					String status = WifiDeviceDownTask.State_Failed;
					if(ModifyDeviceSettingDTO.Result_Success.equals(dto.getResult())){
						status = WifiDeviceDownTask.State_Done;
					}else{
						if(dto.isConfigSequenceMatchError()
					}
			
					//任务callback
					task_completed = doTaskCallback(taskid, status, response);
					//通过任务记录的上下文来进行设备配置数据变更
					if(task_completed != null){
						String payload = task_completed.getPayload();
						if(!StringUtils.isEmpty(dto.getConfig_sequence()) && !StringUtils.isEmpty(payload)){
							String cmdWithoutHeader = CMDBuilder.builderCMDWithoutHeader(payload);
							if(!StringUtils.isEmpty(cmdWithoutHeader)){
								WifiDeviceSettingDTO modify_setting_dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(
										cmdWithoutHeader);
								if(modify_setting_dto != null){
									DeviceHelper.mergeDS(modify_setting_dto, setting_dto);
								}
							}
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//修改配置序列号
				if(dto != null){
					if(ModifyDeviceSettingDTO.Result_Success.equals(dto.getResult())){
						if(!dto.getConfig_sequence().equals(setting_dto.getSequence())){
							setting_dto.setSequence(dto.getConfig_sequence());
							entity.putInnerModel(setting_dto);
							wifiDeviceSettingService.update(entity);
							if(task_completed != null){
								//修改配置成功的后续业务操作
								taskModifyDeviceSettingCompletedDeliverMessage(task_completed.getUid(), 
										wifiId, task_completed.getSubopt());
							}
						}
					}
				}
			}
		}*/
	}
	
	/**
	 * 修改配置成功后 根据不同的操作类型
	 * 可能会有后续操作
	 * @param mac
	 * @param subopt
	 */
	public void taskModifyDeviceSettingCompletedDeliverMessage(Integer uid, String mac, String subopt, 
			String content, WifiDeviceSettingDTO setting_dto, WifiDeviceSettingDTO modify_setting_dto){
		OperationDS ods = OperationDS.getOperationDSFromNo(subopt);
		if(ods != null){
			switch(ods){
				case DS_AclMacs:
					deliverMessageService.sendDeviceModifySettingAclMacsActionMessage(uid, mac);
					break;
				case DS_MM: //修改昵称
					deliverMessageService.sendDeviceModifySettingAaliasActionMessage(uid, mac, content);
					break;
				case DS_VapPassword: //修改vap ssid
					deliverMessageService.sendDeviceModifySettingVapActionMessage(uid, mac);
					break;
/*				case DS_PassThrough:
					taskModifyDeviceSettingPassThroughCompletedDeliverMessage(setting_dto, modify_setting_dto);
					break;*/
				default:
					break;
			}
		}
	}
	
/*	public void taskModifyDeviceSettingPassThroughCompletedDeliverMessage(WifiDeviceSettingDTO setting_dto, 
			WifiDeviceSettingDTO modify_setting_dto){
		if(setting_dto != null && modify_setting_dto != null){
			
		}
	}*/
	
	/**
	 * 获取VAP下的终端列表的响应处理
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
//	public void taskQueryDeviceTerminals(String ctx, String response, String wifiId, int taskid){
//		Document doc = RPCMessageParseHelper.parserMessage(response);
//		QueryTerminalSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
//				QueryTerminalSerialReturnDTO.class);
//		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
//			String ssid = serialDto.getSsid();
//			String bssid = serialDto.getAp();
//			List<WifiDeviceTerminalDTO> dtos = RPCMessageParseHelper.generateDTOFromQueryDeviceTerminals(doc);
//			if(dtos != null && !dtos.isEmpty()){
//				deliverMessageService.sendQueryDeviceTerminalsActionMessage(wifiId, ssid, bssid, dtos);
//			}
//		}
//
//		//2:任务callback
//		doTaskCallback(taskid, serialDto.getStatus(), response);
//	}
	/*public void taskTriggerHttp404Processor(String ctx, String response, String mac, int taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
				QuerySerialReturnDTO.class);
		taskNotifyTriggerHttp404Processor(ctx,response,mac,taskid);
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	public void taskNotifyTriggerHttp404Processor(String ctx, String response, String mac, int taskid){
		try{
			WifiDeviceDownTask downTask = this.taskFacadeService.findWifiDeviceDownTaskById(taskid);
			WifiDeviceDownTask newdownTask = taskFacadeService.systemTaskGenerate(0, mac, OperationCMD.ModifyDeviceSetting.getNo(), OperationDS.DS_Http_404.getNo(), downTask.getContext_var());
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac, newdownTask.getId(), OperationCMD.ModifyDeviceSetting.getNo(), newdownTask.getPayload());
		}catch(BusinessI18nCodeException ex){
			ex.printStackTrace(System.out);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}*/
	
	/*public void taskTriggerHttpPortalProcessor(String ctx, String response, String mac, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
				QuerySerialReturnDTO.class);
		taskNotifyTriggerHttpPortalProcessor(ctx,response,mac,taskid);
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	public void taskNotifyTriggerHttpPortalProcessor(String ctx, String response, String mac, long taskid){
		try{
			WifiDeviceDownTask downTask = this.taskFacadeService.findWifiDeviceDownTaskById(taskid);
			WifiDeviceDownTask newdownTask = taskFacadeService.systemTaskGenerate(0, mac, OperationCMD.ModifyDeviceSetting.getNo(), OperationDS.DS_Http_Portal_Start.getNo(), downTask.getContext_var());
			deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, taskid, OperationCMD.ModifyDeviceSetting.getNo(), newdownTask.getPayload());
		}catch(BusinessI18nCodeException ex){
			ex.printStackTrace(System.out);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}*/
	
	public void taskCommonProcessor(String ctx, String response, String mac, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO resultDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
				QuerySerialReturnDTO.class);
		/*if(WifiDeviceDownTask.State_Done.equals(resultDto.getStatus())){
			String ssid = serialDto.getSsid();
			String bssid = serialDto.getAp();
			List<WifiDeviceTerminalDTO> dtos = RPCMessageParseHelper.generateDTOFromQueryDeviceTerminals(doc);
			if(dtos != null && !dtos.isEmpty()){
				deliverMessageService.sendQueryDeviceTerminalsActionMessage(wifiId, ssid, bssid, dtos);
			}
		}*/
		//2:任务callback
		doTaskCallback(taskid, (resultDto.getStatus()!=null)?resultDto.getStatus():resultDto.getResult(), response);
	}

	public void taskModuleProcessor(String ctx, String response, String mac, long taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		ModuleReturnDTO resultDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
				ModuleReturnDTO.class);
		//2:任务callback
		WifiDeviceDownTaskCompleted task = doTaskCallback(taskid, resultDto.getResult(), response);
		if (WifiDeviceDownTask.State_Ok.equals(resultDto.getResult())) {
			if(task != null){//任务下发的指令响应
				if (WifiDeviceDownTask.State_Ok.equals(resultDto.getResult())) {
					try{
						//如果是增值指令开启response ok的情况下需要回写数据库相关设备ds标记
						if(OperationCMD.ModifyDeviceSetting.getNo().equals(task.getOpt()) 
								&& (OperationDS.DS_Http_VapModuleCMD_Start.getNo().equals(task.getSubopt()) || OperationDS.DS_Http_VapModuleCMD_Stop.getNo().equals(task.getSubopt()))){
							wifiDevicePersistenceCMDStateService.updateDs4PersistenceDetailCMD(mac, task.getOpt(), task.getSubopt());
						}
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}else{//可能是自动下发的指令响应
				if(CMDBuilder.wasAutoVapStartTaskid(taskid)){
					wifiDevicePersistenceCMDStateService.updateDs4PersistenceDetailCMD(mac, OperationCMD.ModifyDeviceSetting.getNo(), OperationDS.DS_Http_VapModuleCMD_Start.getNo());
				}
				if(CMDBuilder.wasAutoVapStartTaskid(taskid)){
					wifiDevicePersistenceCMDStateService.updateDs4PersistenceDetailCMD(mac, OperationCMD.ModifyDeviceSetting.getNo(), OperationDS.DS_Http_VapModuleCMD_Stop.getNo());
				}
			}
		}
		
		/*if (WifiDeviceDownTask.State_Ok.equals(resultDto.getResult())) {
			try{
				//如果是增值指令开启response ok的情况下需要回写数据库相关设备ds标记
				if(OperationCMD.ModifyDeviceSetting.getNo().equals(task.getOpt()) 
						&& (OperationDS.DS_Http_VapModuleCMD_Start.getNo().equals(task.getSubopt()) || OperationDS.DS_Http_VapModuleCMD_Stop.getNo().equals(task.getSubopt()))){
					//WifiDevicePersistenceCMDState cmdState = this.getById(mac);
					wifiDevicePersistenceCMDStateService.updateDs4PersistenceDetailCMD(mac, task.getOpt(), task.getSubopt());
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
		}*/
	}
	
	/**
	 * 如果设备升级失败，或者操作命令不支持处理。
	 *
	 * @param ctx
	 * @param response
	 * @param mac
	 * @param taskid
	 */
	public void taskDeviceUpgrade(String ctx, String response, String mac ,long taskid) {
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO resultDto = RPCMessageParseHelper.generateDTOFromMessage(doc,
				QuerySerialReturnDTO.class);
		if (resultDto.getStatus().equals(WifiDeviceDownTask.State_None) ||
				resultDto.getStatus().equals(WifiDeviceDownTask.State_Error)) {
			doTaskCallback(taskid, WifiDeviceDownTask.State_Failed, response);
		}
	}


	/**
	 * 处理任务数据相应的callback函数 
	 * 针对任务数据的状态修改和转移
	 * @param serialDto
	 */
	public WifiDeviceDownTaskCompleted doTaskCallback(long taskid, String status,String response){
		if(StringUtils.isEmpty(status)) return null;
		if(CMDBuilder.wasNormalTaskid(taskid)){//查看taskid是否是触发性任务id
			try{
				return taskFacadeService.taskExecuteCallback(taskid, status,response);
			}catch(BusinessI18nCodeException bex){
				bex.printStackTrace(System.out);
				System.out.println("~~~~~~~~:taskid"+taskid +" response:"+response);
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				logger.error("DeviceBusinessFacadeService doTaskCallback exception", ex);
			}
		}
		return null;
	}
	
	/**
	 * 处理查询地理位置第一步的返回指令
	 * @param ctx
	 * @param response
	 * @param mac
	 * @param taskid
	 * TODO:taskQueryOldDeviceLocationNotifyProcessor mac[84:82:f4:1a:c3:00] taskid[7298] serial[2147483647] update2redis successfully
	 */
	public void taskQueryOldDeviceLocationNotifyProcessor(String ctx, String response, String mac, long taskid){
		System.out.println(String.format("taskQueryOldDeviceLocationNotifyProcessor mac[%s] taskid[%s] response[%s]", mac,taskid,response));
		try{
			QuerySerialReturnDTO retDTO = RPCMessageParseHelper.parserMessageByDom4j(response, QuerySerialReturnDTO.class);
			if(StringUtils.isNotEmpty(retDTO.getSerial())){//如果此类消息没有serial则忽略掉
				WifiDeviceLocationSerialTaskService.getInstance().addSerialTask(mac, new SerialTaskDTO(mac,taskid,retDTO.getSerial()));
				System.out.println(String.format("taskQueryOldDeviceLocationNotifyProcessor mac[%s] taskid[%s] serial[%s] update2redis successfully", mac,taskid,retDTO.getSerial()));
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
}
