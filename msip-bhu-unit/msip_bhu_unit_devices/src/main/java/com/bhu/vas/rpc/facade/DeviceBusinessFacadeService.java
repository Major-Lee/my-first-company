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
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.dto.redis.SerialTaskDTO;
import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.ModifyDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.dto.ret.QueryWifiTimerSerialReturnDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
import com.bhu.vas.api.dto.ret.WifiDevicePeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRateDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceStatusDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.statistics.DeviceStatistics;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceStatus;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceLocationSerialTaskService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceAlarmService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiDeviceStatusService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
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
	private WifiDeviceAlarmService wifiDeviceAlarmService;
	
	@Resource
	private WifiDeviceStatusService wifiDeviceStatusService;
	
	/*@Resource
	private HandsetDeviceService handsetDeviceService;*/
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	@Resource
	private UserSettingStateService userSettingStateService;
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
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		//wifi设备是否是新设备
		boolean newWifi = false;
		//wifi设备上一次登录时间
		long last_login_at = 0;
		
		//WifiDevice wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
		//wifi_device_entity.setLast_reged_at(new Date());
		String wifiId = dto.getMac().toLowerCase();
		
		//2:wifi设备在线状态Redis更新
		WifiDevicePresentCtxService.getInstance().addPresent(wifiId, ctx);
		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = wifiDeviceService.getById(wifiId);
		if(wifi_device_entity == null){
			wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
			wifi_device_entity.setLast_logout_at(new Date());
			wifiDeviceService.insert(wifi_device_entity);
			newWifi = true;
		}else{
			//wifi_device_entity.setCreated_at(exist_wifi_device_entity.getCreated_at());
			BeanUtils.copyProperties(dto, wifi_device_entity);
			wifi_device_entity.setLast_reged_at(new Date());
			wifi_device_entity.setOnline(true);
			wifiDeviceService.update(wifi_device_entity);
		}
		//本次wifi设备登录时间
		long this_login_at = wifi_device_entity.getLast_reged_at().getTime();
		boolean needLocationQuery = false;
		if(StringUtils.isEmpty(wifi_device_entity.getLat()) || StringUtils.isEmpty(wifi_device_entity.getLon())){
			needLocationQuery = true;
		}
		/*
		 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
		 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
		 * 5:统计增量 wifi设备的daily启动次数增量(backend)
		 */
		deliverMessageService.sendWifiDeviceOnlineActionMessage(wifi_device_entity.getId(), dto.getJoin_reason(),
				this_login_at, last_login_at, newWifi,needLocationQuery);
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
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		String lowercase_wifi_id = wifiId.toLowerCase();

		String ctx_present = WifiDevicePresentCtxService.getInstance().getPresent(lowercase_wifi_id);
		if(ctx.equals(ctx_present)){
			//1:wifi设备基础信息表中的在线状态更新
			WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(lowercase_wifi_id);
			if(exist_wifi_device_entity != null){
				exist_wifi_device_entity.setOnline(false);
				exist_wifi_device_entity.setLast_logout_at(new Date());
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
				
				//2:wifi设备在线状态redis移除 TODO:多线程情况可能下，设备先离线再上线，两条消息并发处理，如果上线消息先完成，可能会清除掉有效数据
				WifiDevicePresentCtxService.getInstance().removePresent(lowercase_wifi_id);
				
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
			handsetDeviceOnline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
			handsetDeviceOffline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Sync.equals(fristDto.getAction())){
			handsetDeviceSync(ctx, parserHeader.getMac(), dtos);
		}
		else if(HandsetDeviceDTO.Action_Update.equals(fristDto.getAction())){
			handsetDeviceUpdate(ctx, fristDto, parserHeader.getMac());
		}
		else{
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_MESSAGE_UNSUPPORT.code());
		}
	}
	
	/**
	 * 移动设备上线
	 * 1:移动设备基础信息更新
	 * 2:wifi设备对应handset在线列表redis添加
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend)
	 * 5:wifi设备接入移动设备的接入数量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		//移动设备是否是新设备
		boolean newHandset = false;
		//移动设备上一次登录时间
		long last_login_at = 0;
		//1:移动设备基础信息更新
		String wifiId_lowerCase = wifiId.toLowerCase();
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(dto.getMac().toLowerCase());
		long this_login_at = System.currentTimeMillis();
		//HandsetDevice handset_device_entity = handsetDeviceService.getById(dto.getMac().toLowerCase());
		if(handset == null){
			dto.setLast_wifi_id(wifiId_lowerCase);
			dto.setTs(this_login_at);
			HandsetStorageFacadeService.handsetComming(dto);
			newHandset = true;
		}else{
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
			HandsetStorageFacadeService.handsetComming(handset);
			//last_login_at = handset_device_entity.getLast_login_at().getTime();
			//		<ITEM action="online" mac="d4:f4:6f:4c:ce:e6" channel="2" ssid="居无忧-海道生态水族馆" bssid="84:82:f4:18:df:79" location="" phy_rate="72M" rssi="-92dBm" snr="15dB" />
			//BeanUtils.copyProperties(dto, handset_device_entity);
			/*handset_device_entity.setChannel(dto.getChannel());
			handset_device_entity.setSsid(dto.getSsid());
			handset_device_entity.setBssid(dto.getBssid());
			handset_device_entity.setPhy_rate(dto.getPhy_rate());
			handset_device_entity.setVapname(dto.getVapname());
			handset_device_entity.setRssi(dto.getRssi());
			handset_device_entity.setSnr(dto.getSnr());
			handset_device_entity.setLast_login_at(new Date());
			handset_device_entity.setLast_wifi_id(wifiId_lowerCase);
			handset_device_entity.setOnline(true);
			handsetDeviceService.update(handset_device_entity);*/
		}
		//本次移动设备登录时间
		//long this_login_at = handset_device_entity.getLast_login_at().getTime();
		
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
				this_login_at, last_login_at, newHandset);
	}
	/*private void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		//移动设备是否是新设备
		boolean newHandset = false;
		//移动设备上一次登录时间
		long last_login_at = 0;
		//1:移动设备基础信息更新
		String wifiId_lowerCase = wifiId.toLowerCase();
		HandsetDevice handset_device_entity = handsetDeviceService.getById(dto.getMac().toLowerCase());
		if(handset_device_entity == null){
			handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
			handset_device_entity.setLast_wifi_id(wifiId_lowerCase);
			handsetDeviceService.insert(handset_device_entity);
			newHandset = true;
		}else{
			last_login_at = handset_device_entity.getLast_login_at().getTime();
			//		<ITEM action="online" mac="d4:f4:6f:4c:ce:e6" channel="2" ssid="居无忧-海道生态水族馆" bssid="84:82:f4:18:df:79" location="" phy_rate="72M" rssi="-92dBm" snr="15dB" />
			//BeanUtils.copyProperties(dto, handset_device_entity);
			handset_device_entity.setChannel(dto.getChannel());
			handset_device_entity.setSsid(dto.getSsid());
			handset_device_entity.setBssid(dto.getBssid());
			handset_device_entity.setPhy_rate(dto.getPhy_rate());
			handset_device_entity.setVapname(dto.getVapname());
			handset_device_entity.setRssi(dto.getRssi());
			handset_device_entity.setSnr(dto.getSnr());
			handset_device_entity.setLast_login_at(new Date());
			handset_device_entity.setLast_wifi_id(wifiId_lowerCase);
			handset_device_entity.setOnline(true);
			handsetDeviceService.update(handset_device_entity);
		}
		//本次移动设备登录时间
		long this_login_at = handset_device_entity.getLast_login_at().getTime();
		
		//2:wifi设备对应handset在线列表redis添加
		WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId_lowerCase, handset_device_entity.getId(), 
				handset_device_entity.getData_rx_rate_double());
		
		
		 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
		 * 4:移动设备连接wifi设备的流水log (backend)
		 * 5:wifi设备接入移动设备的接入数量 (backend)
		 * 6:统计增量 移动设备的daily新增用户或活跃用户增量
		 * 7:统计增量 移动设备的daily启动次数增量(backend)
		 
		deliverMessageService.sendHandsetDeviceOnlineActionMessage(wifiId_lowerCase, handset_device_entity.getId(),
				this_login_at, last_login_at, newHandset);
	}*/
	
	/**
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * 2:wifi设备对应handset在线列表redis移除
	 * 3:统计增量 移动设备的daily访问时长增量 (backend)
	 * @param ctx
	 * @param dto
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		String lowercase_mac = wifiId.toLowerCase();
		String lowercase_d_mac = dto.getMac().toLowerCase();
		//1:更新移动设备的online状态为false
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(lowercase_d_mac);
		if(handset == null) {
			//TODO:是否需要补齐数据
			return;
		}
		
		/*if(handset != null){
			dto.setVapname(handset.getVapname());
			dto.setDhcp_name(handset.getDhcp_name());
			dto.setData_tx_rate(handset.getData_tx_rate());
			dto.setData_rx_rate(handset.getData_rx_rate());
			//String old_Last_wifi_id = handset.getLast_wifi_id();
		}*/
		dto.setVapname(handset.getVapname());
		dto.setDhcp_name(handset.getDhcp_name());
		dto.setData_tx_rate(handset.getData_tx_rate());
		dto.setData_rx_rate(handset.getData_rx_rate());
		//handset.setAction(HandsetDeviceDTO.Action_Offline);
		//handset.setLast_wifi_id(dto.getLast_wifi_id());
		HandsetStorageFacadeService.handsetComming(dto);
		WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(lowercase_mac, 
				lowercase_d_mac, dto.fetchData_rx_rate_double());
		/*
		 * 3:统计增量 移动设备的daily访问时长增量
		 * 如果最后接入时间是今天才会记入daily访问时长
		 */
		if(DateTimeHelper.isSameDay(handset.getTs(), 
				System.currentTimeMillis())){
			deliverMessageService.sendHandsetDeviceOfflineActionMessage(lowercase_mac, 
					handset.getMac(), dto.getUptime(), dto.getRx_bytes(), dto.getTx_bytes());
		}
		/*HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(lowercase_d_mac);
		if(exist_handset_device_entity != null){
			BeanUtils.copyProperties(dto, exist_handset_device_entity, HandsetDeviceDTO.copyIgnoreProperties);
			exist_handset_device_entity.setOnline(false);
			handsetDeviceService.update(exist_handset_device_entity);
			
			//2:wifi设备对应handset在线列表redis移除
//			WifiDeviceHandsetPresentSortedSetService.getInstance().removePresent(exist_handset_device_entity.
//					getLast_wifi_id(), lowercase_mac);
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(lowercase_mac, 
					lowercase_d_mac, exist_handset_device_entity.getData_rx_rate_double());
			
			 * 3:统计增量 移动设备的daily访问时长增量
			 * 如果最后接入时间是今天才会记入daily访问时长
			 
			if(DateTimeHelper.isSameDay(exist_handset_device_entity.getLast_login_at().getTime(), 
					System.currentTimeMillis())){
				deliverMessageService.sendHandsetDeviceOfflineActionMessage(exist_handset_device_entity.getLast_wifi_id(), 
						exist_handset_device_entity.getId(), dto.getUptime(), dto.getRx_bytes(), dto.getTx_bytes());
			}

		}*/

	}
	
	/**
	 * 更新终端的hostname
	 * @param ctx
	 * @param dto
	 * @param wifiId
	 * modified by Edmond Lee for handset storage
	 */
	private void handsetDeviceUpdate(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getDhcp_name()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		String lowercase_d_mac = dto.getMac().toLowerCase();
		//1:更新终端的hostname
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(lowercase_d_mac);
		if(handset != null){
			handset.setDhcp_name(dto.getDhcp_name());
			handset.setLast_wifi_id(dto.getLast_wifi_id());
			HandsetStorageFacadeService.handsetComming(handset);
		}
		/*HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(lowercase_d_mac);
		if(exist_handset_device_entity != null){
			exist_handset_device_entity.setHostname(dto.getDhcp_name());
			handsetDeviceService.update(exist_handset_device_entity);
		}*/

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
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String mac, List<HandsetDeviceDTO> dtos){
		if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		
		//deliverMessageService.sendHandsetDeviceSyncActionMessage(wifiId.toLowerCase(), dtos);
		//1:清除wifi设备对应handset在线列表redis
		deviceFacadeService.allHandsetDoOfflines(mac);
		
		if(dtos != null && !dtos.isEmpty()){
			List<String> ids = new ArrayList<String>();
			for(HandsetDeviceDTO dto : dtos){
				ids.add(dto.getMac().toLowerCase());
			}
			
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(ids);
			int cursor = 0;
			for(HandsetDeviceDTO handset : handsets){
				HandsetDeviceDTO dto = dtos.get(cursor);
				double data_rx_rate = 0d;
				if(handset != null){
					dto.setDhcp_name(handset.getDhcp_name());
					dto.setData_tx_rate(handset.getData_tx_rate());
					dto.setData_rx_rate(handset.getData_rx_rate());
					data_rx_rate = handset.fetchData_rx_rate_double();
				}
				String handsetId = dto.getMac().toLowerCase();
				//1:wifi设备对应handset在线列表redis 重新写入
				WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(mac, handsetId, data_rx_rate);
				cursor++;
			}
			HandsetStorageFacadeService.handsetsComming(dtos);
			//相关统计数据，业务日志，终端接入流水日志
			//deliverMessageService.sendHandsetDeviceSyncActionMessage(mac, dtos);
		}
	}
	
	
	/******************           task response         *************************/
	
	/**
	 * 获取wifi设备地理位置任务响应处理
	 * 1:记录wifi设备的坐标 (backend)
	 * 2:根据坐标提取地理位置详细信息 (backend)
	 * 3:任务callback
	 * @param ctx
	 * @param payload
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
	 * @param ctx
	 * @param doc
	 * @param serialDto
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceTerminalsNotify(String ctx, Document doc, QuerySerialReturnDTO serialDto, 
			String wifiId, long taskid){
		List<WifiDeviceTerminalDTO> terminals = RPCMessageParseHelper.generateDTOFromQueryDeviceTerminals(doc);
		if(terminals != null && !terminals.isEmpty()){
			//获取设备的配置的dto
			WifiDeviceSetting setting_entity = wifiDeviceSettingService.getById(wifiId);
			if(setting_entity == null) return;
			WifiDeviceSettingDTO setting_entity_dto = setting_entity.getInnerModel();
			
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
					if(handset == null){
						handset = new HandsetDeviceDTO();
						handset.setMac(terminal.getMac());
						handset.setAction(HandsetDeviceDTO.Action_Online);
						handset.setTs(System.currentTimeMillis());
						handset.setLast_wifi_id(wifiId);
					}else{
						handset.setData_tx_rate(terminal.getData_tx_rate());
						handset.setData_rx_rate(terminal.getData_rx_rate());
					}
					
					WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, 
							terminal.getMac(), StringUtils.isEmpty(terminal.getData_tx_rate()) ? 0d : Double.parseDouble(terminal.getData_tx_rate()));
					cursor++;
				}
				HandsetStorageFacadeService.handsetsComming(handsets);
			}
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
		doTaskCallback(taskid, dto.getStatus(),response);
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
		doTaskCallback(taskid, serialDto.getStatus(),response);
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
		dto = refreshDeviceSetting(wifiId, dto);
		updateDeviceModeStatusWithMode(wifiId, dto);
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
		WifiDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(response);
		dto = refreshDeviceSetting(wifiId, dto);
		updateDeviceModeStatusWithMode(wifiId, dto);
		
		//设备持久指令分发
		try{
			List<String> persistencePayloads = deviceFacadeService.fetchWifiDevicePersistenceCMD(wifiId);
			if(persistencePayloads != null && !persistencePayloads.isEmpty()){
				for(String payload:persistencePayloads){
					deliverMessageService.sendWifiCmdCommingNotifyMessage(wifiId, 0, 
							null, payload);
				}
				//DaemonHelper.daemonCmdsDown(mac,persistencePayloads,daemonRpcService);
				System.out.println("~~~~~~~~~~~~~~~:persistencePayloads "+persistencePayloads.size());
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
	public void updateDeviceModeStatusWithMode(String wifiId, WifiDeviceSettingDTO dto){
		//根据设备的mode更新status数据
		WifiDeviceSettingLinkModeDTO mode = dto.getMode();
		if(mode != null){
			//dhcpc
			if(WifiDeviceSettingDTO.Mode_Dhcpc.equals(mode.getModel())){
				if(!StringUtils.isEmpty(mode.getWan_interface())){
					long taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
					String cmdPayload = CMDBuilder.builderDhcpcStatusQuery(wifiId, taskid, mode.getWan_interface());
					deliverMessageService.sendWifiCmdCommingNotifyMessage(wifiId, taskid, 
							OperationCMD.QueryDhcpcStatus.getNo(), cmdPayload);
				}
			}else{
				deviceFacadeService.updateDeviceModeStatus(wifiId, dto.getMode());
			}
		}
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
	public WifiDeviceSettingDTO refreshDeviceSetting(String mac, WifiDeviceSettingDTO dto){
		//System.out.println("#####################taskQueryDeviceSetting:"+dto.getRadios().get(0).getPower());
		boolean init_default_acl = false;
		//只有URouter的设备才需进行此操作
		if(deviceFacadeService.isURooterDevice(mac)){
			//验证URouter设备配置是否符合约定
			if(!DeviceHelper.validateURouterBlackList(dto)){
				//modify_urouter_acl = DeviceHelper.builderDSURouterDefaultVapAndAcl(dto);
				init_default_acl = true;
			}
		}
		
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity == null){
			entity = new WifiDeviceSetting();
			entity.setId(mac);
			entity.putInnerModel(dto);
			wifiDeviceSettingService.insert(entity);
		}else{
			entity.putInnerModel(dto);
			wifiDeviceSettingService.update(entity);
		}
		
		//如果不符合urouter的配置约定 则下发指定修改配置
/*		if(!StringUtils.isEmpty(modify_urouter_acl)){
			deliverMessageService.sendActiveDeviceSettingModifyActionMessage(mac, modify_urouter_acl);
		}*/
		deliverMessageService.sendDeviceSettingChangedActionMessage(mac, init_default_acl);
		return dto;
	}
	
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
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus()) 
				&& OperationCMD.QueryDeviceUsedStatus.getCmd().equals(serialDto.getCmd())){
			DeviceUsedStatisticsDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceUsedStatus(doc);
			BusinessMarkerService.getInstance().deviceUsedStatisticsSet(mac, dto);
		}
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
			UserWifiTimerSettingDTO timerStarSetting = setting.getUserSetting( UserWifiTimerSettingDTO.Setting_Key, UserWifiTimerSettingDTO.class);
			if(timerStarSetting == null){
				timerStarSetting = new UserWifiTimerSettingDTO();
				timerStarSetting.setTimeslot(ParamCmdWifiTimerStartDTO.Default_Timeslot);
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
				innerDTO.setTimeslot(ParamCmdWifiTimerStartDTO.Default_Timeslot);
			}
			userSettingStateService.updateUserSetting(wifiId, UserWifiTimerSettingDTO.Setting_Key, JsonHelper.getJSONString(innerDTO));
		}
		doTaskCallback(taskid, serialDto.getStatus(), response);
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
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().clearPeakSections(wifiId);
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
				deliverMessageService.sendWifiCmdCommingNotifyMessage(wifiId, new_taskid, 
						OperationCMD.QueryDeviceSetting.getNo(), cmdPayload);
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
			if(task_with_paylaod != null && WifiDeviceDownTask.State_Done.equals(status)){
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
												wifiId, task_with_paylaod.getSubopt());
								}
							}
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
	public void taskModifyDeviceSettingCompletedDeliverMessage(Integer uid, String mac, String subopt){
		OperationDS ods = OperationDS.getOperationDSFromNo(subopt);
		if(ods != null){
			switch(ods){
				case DS_AclMacs:
					deliverMessageService.sendDeviceModifySettingAclMacsActionMessage(uid, mac);
					break;
				default:
					break;
			}
		}
	}
	
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
	
	public void taskTriggerHttpPortalProcessor(String ctx, String response, String mac, long taskid){
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
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac, taskid, OperationCMD.ModifyDeviceSetting.getNo(), newdownTask.getPayload());
		}catch(BusinessI18nCodeException ex){
			ex.printStackTrace(System.out);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
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
		doTaskCallback(taskid, resultDto.getStatus(), response);
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
	 */
	public void taskQueryOldDeviceLocationNotifyProcessor(String ctx, String response, String mac, long taskid){
		System.out.println(String.format("taskQueryOldDeviceLocationNotifyProcessor mac[%s] taskid[%s]", mac,taskid));
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
