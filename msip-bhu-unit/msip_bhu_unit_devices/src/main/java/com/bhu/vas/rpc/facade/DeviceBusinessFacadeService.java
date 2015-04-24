package com.bhu.vas.rpc.facade;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.ModifyDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.dto.ret.QueryTerminalSerialReturnDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRateDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceStatusDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceStatus;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceAlarmService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiDeviceStatusService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
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
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
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
		WifiDevicePresentService.getInstance().addPresent(wifiId, ctx);
		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = wifiDeviceService.getById(wifiId);
		if(wifi_device_entity == null){
			wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
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

		/*
		 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
		 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
		 * 5:统计增量 wifi设备的daily启动次数增量(backend)
		 */
		deliverMessageService.sendWifiDeviceOnlineActionMessage(wifi_device_entity.getId(), 
				this_login_at, last_login_at, newWifi);
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

		String ctx_present = WifiDevicePresentService.getInstance().getPresent(lowercase_wifi_id);
		if(ctx.equals(ctx_present)){
			//1:wifi设备基础信息表中的在线状态更新
			WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(lowercase_wifi_id);
			if(exist_wifi_device_entity != null){
				exist_wifi_device_entity.setOnline(false);
				wifiDeviceService.update(exist_wifi_device_entity);
				
				//2:wifi设备在线状态redis移除 TODO:多线程情况可能下，设备先离线再上线，两条消息并发处理，如果上线消息先完成，可能会清除掉有效数据
				WifiDevicePresentService.getInstance().removePresent(lowercase_wifi_id);
				
				/*
				 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
				 * 4:wifi设备对应handset在线列表redis清除 (backend)
				 * 5:统计增量 wifi设备的daily访问时长增量 (backend)
				 */
				//wifi设备上次登录的时间
				long last_login_at = exist_wifi_device_entity.getLast_reged_at().getTime();
				
				deliverMessageService.sendWifiDeviceOfflineActionMessage(lowercase_wifi_id, last_login_at);
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
	 */
	public void handsetDeviceConnectState(String ctx, String payload, ParserHeader parserHeader) {
		//HandsetDeviceDTO dto = RPCMessageParseHelper.generateDTOFromMessage(payload, HandsetDeviceDTO.class);
		List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage(payload, 
				HandsetDeviceDTO.class);
		if(dtos == null || dtos.isEmpty()) return;
		
		HandsetDeviceDTO fristDto = dtos.get(0);
		if(HandsetDeviceDTO.Action_Online.equals(fristDto.getAction())){
			handsetDeviceOnline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
			handsetDeviceOffline(ctx, fristDto, parserHeader.getMac());
		}
		else if(HandsetDeviceDTO.Action_Sync.equals(fristDto.getAction())){
			handsetDeviceSync(ctx, parserHeader.getMac(), dtos);
		}else{
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
	 */
	public void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto, String wifiId){
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
		
		/*
		 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
		 * 4:移动设备连接wifi设备的流水log (backend)
		 * 5:wifi设备接入移动设备的接入数量 (backend)
		 * 6:统计增量 移动设备的daily新增用户或活跃用户增量
		 * 7:统计增量 移动设备的daily启动次数增量(backend)
		 */
		deliverMessageService.sendHandsetDeviceOnlineActionMessage(wifiId_lowerCase, handset_device_entity.getId(),
				this_login_at, last_login_at, newHandset);
	}
	
	/**
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * 2:wifi设备对应handset在线列表redis移除
	 * 3:统计增量 移动设备的daily访问时长增量 (backend)
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		String lowercase_mac = wifiId.toLowerCase();
		//1:更新移动设备的online状态为false
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac().toLowerCase());
		if(exist_handset_device_entity != null){
			BeanUtils.copyProperties(dto, exist_handset_device_entity, HandsetDeviceDTO.copyIgnoreProperties);
			exist_handset_device_entity.setOnline(false);
			handsetDeviceService.update(exist_handset_device_entity);
			
			//2:wifi设备对应handset在线列表redis移除
//			WifiDeviceHandsetPresentSortedSetService.getInstance().removePresent(exist_handset_device_entity.
//					getLast_wifi_id(), lowercase_mac);
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(exist_handset_device_entity.
					getLast_wifi_id(), lowercase_mac, exist_handset_device_entity.getData_rx_rate_double());
			/*
			 * 3:统计增量 移动设备的daily访问时长增量
			 * 如果最后接入时间是今天才会记入daily访问时长
			 */
			if(DateTimeHelper.isSameDay(exist_handset_device_entity.getLast_login_at().getTime(), 
					System.currentTimeMillis())){
				deliverMessageService.sendHandsetDeviceOfflineActionMessage(exist_handset_device_entity.getLast_wifi_id(), 
						exist_handset_device_entity.getId(), dto.getUptime());
			}

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
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String wifiId, List<HandsetDeviceDTO> dtos){
		if(StringUtils.isEmpty(wifiId) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		
		deliverMessageService.sendHandsetDeviceSyncActionMessage(wifiId.toLowerCase(), dtos);
//		//a:如果移动设备目前不在线或者不存在移动设备数据，则执行设备上线相同操作
//		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac());
//		if(exist_handset_device_entity == null || !exist_handset_device_entity.isOnline()){
//			this.handsetDeviceOnline(ctx, dto);
//			return;
//		}
//		//b:如果移动设备目前在线
//		if(exist_handset_device_entity.isOnline()){
//			//1:移动设备基础信息更新
//			HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
//			handsetDeviceService.update(handset_device_entity);
//			
//			String wifiId = handset_device_entity.getBssid();
//			//2:wifi设备对应handset在线列表redis更新
//			WifiDeviceHandsetPresentSortedSetService.getInstance().addPresent(wifiId, handset_device_entity.getId(), 
//					handset_device_entity.getLast_login_at().getTime());
//		}
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
	public void taskQueryDeviceLocationS2(String ctx, String response, String wifiId, int taskid){
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
			String wifiId, int taskid){
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
			String wifiId, int taskid){
		String rate = serialDto.getRate();
		if(StringUtils.isEmpty(rate)) return;
		
		String peak_rate = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getPeak(wifiId);
		if(StringUtils.isEmpty(peak_rate) || rate.compareTo(peak_rate) > 0){
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().addPeak(wifiId, rate);
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
			String wifiId, int taskid){
		WifiDeviceRateDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceRate(doc);
		if(dto != null){
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().addRate(wifiId, dto.getTx_rate(), dto.getRx_rate());
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
	public void taskQueryDeviceStatus(String ctx, String response, String wifiId, int taskid){
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
	public void taskQueryDeviceFlow(String ctx, String response, String wifiId, int taskid){
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
	 * 获取设备配置或者变更配置的响应处理
	 * 1：更新配置数据
	 * 2：如果设备是urouter，检查配置是否满足约定 不满足则下发修改配置
	 * 	  此下发配置为区间任务 不等回应直接修改配置数据
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskQueryDeviceSetting(String ctx, String response, String wifiId, int taskid){
		WifiDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(response);
		
		//只有URouter的设备才需进行此操作
		if(deviceFacadeService.isURooterDevice(wifiId)){
			//验证URouter设备配置是否符合约定
			if(!DeviceHelper.validateURouterBlackList(dto)){
				String modify_payload = DeviceHelper.builderDSURouterDefaultVapAndAcl(dto);
				if(!StringUtils.isEmpty(modify_payload)){
					deliverMessageService.sendActiveDeviceSettingModifyActionMessage(wifiId, modify_payload);
				}
			}
		}
		
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
		if(entity == null){
			entity = new WifiDeviceSetting();
			entity.setId(wifiId);
			entity.putInnerModel(dto);
			wifiDeviceSettingService.insert(entity);
		}else{
			entity.putInnerModel(dto);
			wifiDeviceSettingService.update(entity);
		}
		
		//2:任务callback
		doTaskCallback(taskid, WifiDeviceDownTask.State_Done, response);
	}
	
	/**
	 * 修改设备配置的响应处理
	 * @param ctx
	 * @param response
	 * @param wifiId
	 * @param taskid
	 */
	public void taskModifyDeviceSetting(String ctx, String response, String wifiId, int taskid){
		ModifyDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromMessage(response, ModifyDeviceSettingDTO.class);
		String status = WifiDeviceDownTask.State_Failed;
		if(ModifyDeviceSettingDTO.Result_Success.equals(dto.getResult())){
			status = WifiDeviceDownTask.State_Done;
		}

		//任务callback
		WifiDeviceDownTaskCompleted task_completed = doTaskCallback(taskid, status, response);
		//通过任务记录的上下文来进行设备配置数据变更
		if(task_completed != null){
			String payload = task_completed.getPayload();
			if(!StringUtils.isEmpty(dto.getConfig_sequence()) && !StringUtils.isEmpty(payload)){
				String cmdWithoutHeader = CMDBuilder.builderCMDWithoutHeader(payload);
				if(!StringUtils.isEmpty(cmdWithoutHeader)){
					WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
					if(entity != null){
						WifiDeviceSettingDTO setting_dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(
								cmdWithoutHeader, entity.getInnerModel());
						DeviceHelper.modifyDSConfigSequence(setting_dto, dto.getConfig_sequence());
						entity.putInnerModel(setting_dto);
						wifiDeviceSettingService.update(entity);
					}
				}
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
	public void taskQueryDeviceTerminals(String ctx, String response, String wifiId, int taskid){
		Document doc = RPCMessageParseHelper.parserMessage(response);
		QueryTerminalSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, 
				QueryTerminalSerialReturnDTO.class);
		if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())){
			String ssid = serialDto.getSsid();
			String bssid = serialDto.getAp();
			List<WifiDeviceTerminalDTO> dtos = RPCMessageParseHelper.generateDTOFromQueryDeviceTerminals(doc);
			if(dtos != null && !dtos.isEmpty()){
				deliverMessageService.sendQueryDeviceTerminalsActionMessage(wifiId, ssid, bssid, dtos);
			}
		}

		//2:任务callback
		doTaskCallback(taskid, serialDto.getStatus(), response);
	}
	
	/**
	 * 处理任务数据相应的callback函数 
	 * 针对任务数据的状态修改和转移
	 * @param serialDto
	 */
	public WifiDeviceDownTaskCompleted doTaskCallback(int taskid, String status,String response){
		if(StringUtils.isEmpty(status)) return null;
		if(CMDBuilder.wasNormalTaskid(taskid)){//查看taskid是否是触发性任务id
			return taskFacadeService.taskExecuteCallback(taskid, status,response);
		}
		return null;
	}
}
