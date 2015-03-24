package com.bhu.vas.business.backendonline.asyncprocessor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.CMUPWithWifiDeviceOnlinesDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceSyncDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceLocationDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr.WifiDeviceIndexIncrementService;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.bhu.vas.business.logger.BusinessWifiHandsetRelationFlowLogger;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingAddressDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingResultDTO;

@Service
public class AsyncMsgHandleService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	
	@Resource
	private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	/**
	 * wifi设备上线
	 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
	 * 4:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 5:统计增量 wifi设备的daily启动次数增量 (backend)
	 * 6:wifi设备增量索引
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceOnlineHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOnlineHandle message[%s]", message));
		
		WifiDeviceOnlineDTO dto = JsonHelper.getDTO(message, WifiDeviceOnlineDTO.class);
		//3:wifi设备对应handset在线列表初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备
		WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(dto.getMac(), dto.getLogin_ts());
		//判断移动设备是否是新设备
		if(dto.isNewWifi()){
			//4:统计增量 wifi设备的daily新增设备
			DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
					DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_News, 1);
		}else{
			//判断本次登录时间和上次登录时间是否是同一天, 如果不是, 则wifi设备的daily增量活跃wifi设备
			if(!DateTimeHelper.isSameDay(dto.getLast_login_at(), dto.getLogin_ts())){
				//4:统计增量 wifi设备的daily活跃设备增量
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Actives, 1);
			}
		}
		//5:统计增量 wifi设备的daily启动次数增量
		DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
				DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, 1);
		
		wifiDeviceIndexIncrementService.wifiDeviceOnlineIndexIncrement(dto.getMac());
		
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOnlineHandle message[%s] successful", message));
	}
	
	/**
	 *  a:如果设备是新上线的
	 * 	1：wifi设备基础信息更新(backend)
	 * 	2：wifi设备在线状态Redis更新(backend)
	 * 	3:统计增量 wifi设备的daily新增设备或活跃设备增量 (backend)
	 * 	4:统计增量 wifi设备的daily启动次数增量(backend)
	 * b:如果设备本身是在线的
	 * 	do nothing
	 *  5:增量索引
	 * @throws Exception 
	 */
	public void cmupWithWifiDeviceOnlinesHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s]", message));
		
		CMUPWithWifiDeviceOnlinesDTO cmupWithOnlinesDto = JsonHelper.getDTO(message, CMUPWithWifiDeviceOnlinesDTO.class);
		List<WifiDeviceDTO> dtos = cmupWithOnlinesDto.getDevices();
		if(dtos != null && !dtos.isEmpty()){
			List<String> ids = new ArrayList<String>();
			for(WifiDeviceDTO dto : dtos){
				ids.add(dto.getMac().toLowerCase());
			}
			List<WifiDevice> entitys = wifiDeviceService.findByIds(ids, true, true);
			//新上线的设备列表(非新注册)
			List<WifiDevice> entityNewOnlines = new ArrayList<WifiDevice>();
			//新上线的并且是新注册的设备列表
			List<WifiDevice> entityNewRegisters = new ArrayList<WifiDevice>();
			//不在线的设备的ids
			List<String> offline_ids = new ArrayList<String>();
			int cursor = 0;
			for(WifiDevice entity : entitys){
				if(entity != null && entity.isOnline()){
					continue;
				}
				WifiDeviceDTO dto = dtos.get(cursor);
				if(entity == null){
					entityNewRegisters.add(BusinessModelBuilder.wifiDeviceDtoToEntity(dto));
				}else{
					entityNewOnlines.add(BusinessModelBuilder.wifiDeviceDtoToEntity(dto));
				}
				offline_ids.add(dto.getMac());
				cursor++;
			}
			//3:统计增量 wifi设备的daily新增设备增量
			int incr_statistics_news = 0;
			//3:统计增量 wifi设备的daily活跃设备增量
			int incr_statistics_active = 0;
			//4:统计增量 wifi设备的daily启动次数增量
			int incr_statistics_accesscount = 0;
			//有新上线的设备(非新注册)
			int newOnline_length = entityNewOnlines.size();
			if(newOnline_length > 0){
				incr_statistics_accesscount += newOnline_length;
				//今天的时间
				Date today = new Date();
				for(WifiDevice entity: entityNewOnlines){
					//如果最后的登录时间和今天不一样，说明今天是第一次登录
					if(!DateTimeHelper.isSameDay(entity.getLast_reged_at(), today)){
						incr_statistics_active++;
					}
				}
				//1：wifi设备基础信息更新
				wifiDeviceService.updateAll(entityNewOnlines);
			}
			//新上线的并且是新注册的设备列表
			int newRegister_length = entityNewRegisters.size();
			if(newRegister_length > 0){
				incr_statistics_accesscount += newRegister_length;
				incr_statistics_news += newRegister_length;
				//1：wifi设备基础信息更新
				wifiDeviceService.insertAll(entityNewRegisters);
			}
			
			//2:wifi设备在线状态Redis更新
			if(!offline_ids.isEmpty()){
				WifiDevicePresentService.getInstance().addPresents(offline_ids, cmupWithOnlinesDto.getCtx());
			}
			
			//3:统计增量 wifi设备的daily新增设备增量
			if(incr_statistics_news > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_News, incr_statistics_news);
			}
			//3:统计增量 wifi设备的daily活跃设备增量
			if(incr_statistics_active > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Actives, incr_statistics_active);
			}
			//4:统计增量 wifi设备的daily启动次数增量
			if(incr_statistics_accesscount > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, incr_statistics_accesscount);
			}
			//5:增量索引
			wifiDeviceIndexIncrementService.cmupWithWifiDeviceOnlinesIndexIncrement(entitys);
		}
		logger.info(String.format("AnsyncMsgBackendProcessor cmupWithWifiDeviceOnlinesHandle message[%s] successful", message));
	}
	
	/**
	 * wifi设备下线
	 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
	 * 4:wifi设备对应handset在线列表redis清除 (backend)
	 * 5:统计增量 wifi设备的daily访问时长增量 (backend)
	 * 6:增量索引
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceOfflineHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOfflineHandle message[%s]", message));
		
		WifiDeviceOfflineDTO dto = JsonHelper.getDTO(message, WifiDeviceOfflineDTO.class);
		//3:wifi上的移动设备基础信息表的在线状态更新
		List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(dto.getMac());
		if(!handset_devices_online_entitys.isEmpty()){
			for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
				handset_devices_online_entity.setOnline(false);
			}
			handsetDeviceService.updateAll(handset_devices_online_entitys);
		}
		//4:wifi设备对应handset在线列表redis清除
		WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(dto.getMac());
		//5:统计增量 wifi设备的daily访问时长增量
		if(dto.getLast_login_at() > 0){
			long uptime = dto.getTs() - dto.getLast_login_at();
			if(uptime > 0){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Duration, uptime);
			}
		}
		
		//6:增量索引
		wifiDeviceIndexIncrementService.wifiDeviceOfflineIndexIncrement(dto.getMac());
		
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceOfflineHandle message[%s] successful", message));
	}
	
	/**
	 * 移动设备上线
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend)
	 * 5:wifi设备接入移动设备的接入数量增量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * @param message
	 */
	public void handsetDeviceOnlineHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle message[%s]", message));
		
		HandsetDeviceOnlineDTO dto = JsonHelper.getDTO(message, HandsetDeviceOnlineDTO.class);
		
		//3:移动设备连接wifi设备的接入记录(非流水)
		int result_status = wifiHandsetDeviceRelationMService.addRelation(dto.getWifiId(), dto.getMac(), 
				new Date(dto.getLogin_ts()));

		//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
		if(result_status == WifiHandsetDeviceRelationMService.AddRelation_Insert){
			//5:wifi设备接入移动设备的接入数量增量
			wifiHandsetDeviceLoginCountMService.incrCount(dto.getWifiId());
		}
		
		//判断移动设备是否是新设备
		if(dto.isNewHandset()){
			//6:统计增量 移动设备的daily新增用户
			DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
					DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_News, 1);
		}else{
			//判断本次登录时间和上次登录时间是否是同一天, 如果不是, 则移动设备的daily增量活跃移动设备
			if(!DateTimeHelper.isSameDay(dto.getLast_login_at(), dto.getLogin_ts())){
				//6:统计增量 移动设备的daily活跃移动设备增量
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_Actives, 1);
			}
		}
		//7:统计增量 移动设备的daily启动次数增量
		DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
				DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, 1);
		//4:移动设备连接wifi设备的流水log
		BusinessWifiHandsetRelationFlowLogger.doFlowMessageLog(dto.getWifiId(), dto.getMac(), dto.getLogin_ts());
		
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOnlineHandle message[%s] successful", message));
	}
	
	/**
	 * 移动设备下线
	 * 3:统计增量 移动设备的daily访问时长增量 (backend)
	 * @param message
	 */
	public void handsetDeviceOfflineHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOfflineHandle message[%s]", message));
		
		HandsetDeviceOfflineDTO dto = JsonHelper.getDTO(message, HandsetDeviceOfflineDTO.class);
		//3:统计增量 移动设备的daily访问时长增量
		if(!StringUtils.isEmpty(dto.getUptime())){
			DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
					DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_Duration, Long.parseLong(dto.getUptime()));
		}
		
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceOfflineHandle message[%s] successful", message));
	}
	
	/**
	 * 移动设备连接状态sync
	 * 1:清除wifi设备对应handset在线列表redis 并重新写入 (backend)
	 * 2:移动设备基础信息更新 (backend)
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend)
	 * 5:wifi设备接入移动设备的接入数量 (backend)
	 * 6:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 7:统计增量 移动设备的daily启动次数增量(backend)
	 * @param message
	 */
	public void handsetDeviceSyncHandle(String message){
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s]", message));
		
		HandsetDeviceSyncDTO sync_dto = JsonHelper.getDTO(message, HandsetDeviceSyncDTO.class);
		String wifiId = sync_dto.getMac();
		if(!StringUtils.isEmpty(wifiId)){
			//1:清除wifi设备对应handset在线列表redis
			WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(sync_dto.getMac());
			
			List<HandsetDeviceDTO> dtos = sync_dto.getDtos();
			if(dtos != null && !dtos.isEmpty()){
				List<String> ids = new ArrayList<String>();
				for(HandsetDeviceDTO dto : dtos){
					ids.add(dto.getMac().toLowerCase());
				}
				//新上线的设备列表(非新注册)
				List<HandsetDevice> entityNewOnlines = new ArrayList<HandsetDevice>();
				//新上线的并且是新注册的设备列表
				List<HandsetDevice> entityNewRegisters = new ArrayList<HandsetDevice>();
				
				List<HandsetDevice> entitys = handsetDeviceService.findByIds(ids, true, true);
				int cursor = 0;
				for(HandsetDevice entity : entitys){
					if(entity != null && entity.isOnline()){
						continue;
					}
					HandsetDeviceDTO dto = dtos.get(cursor);
					if(entity == null){
						entityNewRegisters.add(BusinessModelBuilder.handsetDeviceDtoToEntity(dto));
					}else{
						entityNewOnlines.add(BusinessModelBuilder.handsetDeviceDtoToEntity(dto));
					}
					//3:移动设备连接wifi设备的接入记录(非流水)
					int result_status = wifiHandsetDeviceRelationMService.addRelation(wifiId, dto.getMac(), new Date(sync_dto.getTs()));
					//如果接入记录是新记录 表示移动设备第一次连接此wifi设备
					if(result_status == WifiHandsetDeviceRelationMService.AddRelation_Insert){
						//5:wifi设备接入移动设备的接入数量增量
						wifiHandsetDeviceLoginCountMService.incrCount(wifiId);
					}
					//4:移动设备连接wifi设备的流水log
					BusinessWifiHandsetRelationFlowLogger.doFlowMessageLog(wifiId, dto.getMac(), sync_dto.getTs());
					cursor++;
				}

				//6:统计增量 移动设备的daily新增设备增量
				int incr_statistics_news = 0;
				//6:统计增量 移动设备的daily活跃设备增量
				int incr_statistics_active = 0;
				//7:统计增量 移动设备的daily启动次数增量
				int incr_statistics_accesscount = 0;
				//有新上线的设备(非新注册)
				int newOnline_length = entityNewOnlines.size();
				if(newOnline_length > 0){
					incr_statistics_accesscount += newOnline_length;
					//今天的时间
					Date today = new Date();
					for(HandsetDevice entity: entityNewOnlines){
						//如果最后的登录时间和今天不一样，说明今天是第一次登录
						if(!DateTimeHelper.isSameDay(entity.getLast_login_at(), today)){
							incr_statistics_active++;
						}
					}
					//2:移动设备基础信息更新 
					handsetDeviceService.updateAll(entityNewOnlines);
				}
				//新上线的并且是新注册的设备列表
				int newRegister_length = entityNewRegisters.size();
				if(newRegister_length > 0){
					incr_statistics_accesscount += newRegister_length;
					incr_statistics_news += newRegister_length;
					//2:移动设备基础信息更新
					handsetDeviceService.insertAll(entityNewRegisters);
				}
				
				//6:统计增量 移动设备的daily新增设备增量
				if(incr_statistics_news > 0){
					DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
							DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_News, incr_statistics_news);
				}
				//6:统计增量 移动设备的daily活跃设备增量
				if(incr_statistics_active > 0){
					DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
							DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_Actives, incr_statistics_active);
				}
				//7:统计增量 移动设备的daily启动次数增量
				if(incr_statistics_accesscount > 0){
					DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
							DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, incr_statistics_accesscount);
				}
			}
		}
		logger.info(String.format("AnsyncMsgBackendProcessor handsetDeviceSyncHandle message[%s] successful", message));
	}
	
	/**
	 * 根据wifi设备的经纬度调用第三方服务获取地理位置的详细信息
	 * 1:记录wifi设备的坐标 (backend)
	 * 2:根据坐标提取地理位置详细信息 (backend)
	 * 3:增量索引
	 * @param message
	 * @throws Exception 
	 */
	public void wifiDeviceLocationHandle(String message) throws Exception{
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceLocationHandle message[%s]", message));
		
		WifiDeviceLocationDTO dto = JsonHelper.getDTO(message, WifiDeviceLocationDTO.class);
		//1:记录wifi设备的坐标 (backend)
		WifiDevice entity = wifiDeviceService.getById(dto.getMac());
		if(entity != null){
			//如果经纬度和记录的一样(如果经纬度有波动,可以考虑按误差值来判定), 并且地理信息已经提取, 就不再进行提取了
			if(dto.getLat().equals(entity.getLat()) && dto.getLon().equals(entity.getLon())){
				return;
			}
			
			entity.setLat(dto.getLat());
			entity.setLon(dto.getLon());
			
			try{
				//2:根据坐标提取地理位置详细信息 (backend)
				GeocodingDTO geocodingDto = GeocodingHelper.geocodingGet(String.valueOf(dto.getLat()), 
						String.valueOf(dto.getLon()));
				if(geocodingDto != null && geocodingDto.getStatus() == GeocodingDTO.Success_Status){
					GeocodingResultDTO resultDto = geocodingDto.getResult();
					if(resultDto != null){
						entity.setFormatted_address(resultDto.getFormatted_address());
						GeocodingAddressDTO addressDto = geocodingDto.getResult().getAddressComponent();
						if(addressDto != null){
							entity.setCountry(addressDto.getCountry());
							entity.setProvince(addressDto.getProvince());
							entity.setCity(addressDto.getCity());
							entity.setDistrict(addressDto.getDistrict());
							entity.setStreet(addressDto.getStreet());
						}
					}
				}else{
					logger.error(String.format("GeocodingHelper fail lat[%s] lon[%s] status[%s]",
							dto.getLat(),dto.getLon(), geocodingDto != null ? geocodingDto.getStatus() : ""));
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				logger.error(String.format("GeocodingHelper exception lat[%s] lon[%s] exmsg[%s]",
						dto.getLat(),dto.getLon(), ex.getMessage()), ex);
			}
			wifiDeviceService.update(entity);
			
			//3:增量索引
			wifiDeviceIndexIncrementService.wifiDeviceLocationIndexIncrement(entity);
		}
		logger.info(String.format("AnsyncMsgBackendProcessor wifiDeviceLocationHandle message[%s] successful", message));
	}
}
