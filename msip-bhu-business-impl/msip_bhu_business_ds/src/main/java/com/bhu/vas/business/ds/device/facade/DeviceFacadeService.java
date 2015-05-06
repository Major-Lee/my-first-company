package com.bhu.vas.business.ds.device.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.statistics.DeviceStatistics;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingAddressDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingResultDTO;

@Service
public class DeviceFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceFacadeService.class);
	/**
	 * 存在多种混合状态
	 * 100以上在线状态可以绑定
	 * 100以下未绑定
	 * @see com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService
	 */
	public final static int WIFI_DEVICE_STATUS_NOT_EXIST = 0;
	public final static int WIFI_DEVICE_STATUS_NOT_UROOTER = 98;
	public final static int WIFI_DEVICE_STATUS_NOT_ONLINE = 99;
	public final static int WIFI_DEVICE_STATUS_ONLINE = 100;

	private final static String WIFI_DEVICE_ORIGIN_MODEL = "Urouter";
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	/**
	 * 指定wifiId进行终端全部下线处理
	 * @param wifiId
	 */
	public void allHandsetDoOfflines(String wifiId){
		List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(wifiId);
		if(!handset_devices_online_entitys.isEmpty()){
			for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
				handset_devices_online_entity.setOnline(false);
			}
			handsetDeviceService.updateAll(handset_devices_online_entitys);
		}
		WifiDeviceHandsetPresentSortedSetService.getInstance().clearOnlinePresents(wifiId);
	}
	
	/**
	 * 根据wifi设备的经纬度获取地理信息数据，并且进行填充
	 * @param entity
	 */
	public boolean wifiDeiviceGeocoding(WifiDevice entity){
		if(entity == null) return false;
		if(StringUtils.isEmpty(entity.getLat()) || StringUtils.isEmpty(entity.getLon())) return false;
		
		try{
			//2:根据坐标提取地理位置详细信息 (backend)
			GeocodingDTO geocodingDto = GeocodingHelper.geocodingGet(String.valueOf(entity.getLat()), 
					String.valueOf(entity.getLon()));
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
						return true;
					}
				}
			}else{
				logger.error(String.format("GeocodingHelper fail lat[%s] lon[%s] status[%s]",
						entity.getLat(), entity.getLon(), geocodingDto != null ? geocodingDto.getStatus() : ""));
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("GeocodingHelper exception lat[%s] lon[%s] exmsg[%s]",
					entity.getLat(),entity.getLon(), ex.getMessage()), ex);
		}
		return false;
	}
	/**
	 * 用于填充当日daily的统计数据中的实际计算数据
	 * 1:设备接入次数平均（3/(1+2)）(实时计算)
	 * 2:设备活跃率（1+2）/总设备 (实时计算)
	 * 3:设备接入时长平均（4/(1+2)）(实时计算)
	 * 4:新设备占比（1/(1+2)）(实时计算)
	 * @return
	 */
	public DailyStatisticsDTO dailyHandsetStatisticsArith(){
		DailyStatisticsDTO dailyStatisticsDto = DailyStatisticsHashService.getInstance().getStatistics(
				BusinessKeyDefine.Statistics.DailyStatisticsHandsetInnerPrefixKey);
		if(dailyStatisticsDto == null) {
			return new DailyStatisticsDTO();
		}
		return dailyStatisticsArith(dailyStatisticsDto);
	}
	
	/**
	 * 用于填充任何日期daily的统计数据中的实际计算数据
	 * 1:设备接入次数平均（3/(1+2)）(实时计算)
	 * 2:设备活跃率（1+2）/总设备 (实时计算)
	 * 3:设备接入时长平均（4/(1+2)）(实时计算)
	 * 4:新设备占比（1/(1+2)）(实时计算)
	 * @param dailyStatisticsDto 任何日期的dailyDto
	 * @return
	 */
	public DailyStatisticsDTO dailyStatisticsArith(DailyStatisticsDTO dailyStatisticsDto){
		if(dailyStatisticsDto == null) return null;
		
		//(1+2)
		long news_add_actives = dailyStatisticsDto.getNews() + dailyStatisticsDto.getActives();
		//1:设备接入次数平均（3/(1+2)）
		if(news_add_actives > 0){
			long accesscount_avg = new Double(ArithHelper.div(dailyStatisticsDto.getAccesscount(),news_add_actives, 1)).longValue();
			dailyStatisticsDto.setAccesscount_avg(accesscount_avg);
		}

		//2:设备活跃率（1+2）/总设备
		SystemStatisticsDTO systemStatisticsDto = SystemStatisticsHashService.getInstance().getStatistics();
		long total_handsets = 0;
		
		if(systemStatisticsDto != null && systemStatisticsDto.getHandsets() > 0){
			total_handsets = systemStatisticsDto.getHandsets();
		}else{
			//只有系统运行第一天可能会出现此情况
			total_handsets = news_add_actives;
		}
		
		if(total_handsets > 0){
			String active_pet = ArithHelper.percent(news_add_actives, total_handsets, 0);
			dailyStatisticsDto.setActive_pet(active_pet);
		}

		//3:设备接入时长平均（4/(1+2)）
		if(news_add_actives > 0){
			long duration_avg = new Double(ArithHelper.div(dailyStatisticsDto.getDuration(),news_add_actives, 0)).longValue();
			dailyStatisticsDto.setDuration_avg(duration_avg);
		}
		//4:新设备占比（1/(1+2)）
		if(news_add_actives > 0){
			String news_pet = ArithHelper.percent(dailyStatisticsDto.getNews(),news_add_actives, 0);
			dailyStatisticsDto.setNews_pet(news_pet);
		}
		
		return dailyStatisticsDto;
	}
	
	/**
	 * 以当日daily的统计数据与system的统计数据求和 
	 * 1：总移动设备接入次数 (当前数量+当日daily数量) 
	 * 2：总移动设备访问时长 (当前数量+当日daily数量)
	 * @param systemStatisticsDto
	 * @return
	 */
	public SystemStatisticsDTO systemStatisticsArith(SystemStatisticsDTO systemStatisticsDto){
		DailyStatisticsDTO dailyStatisticsDto = DailyStatisticsHashService.getInstance().getStatistics(
				BusinessKeyDefine.Statistics.DailyStatisticsHandsetInnerPrefixKey);
		if(dailyStatisticsDto == null) {
			return systemStatisticsDto;
		}
		return systemStatisticsArith(systemStatisticsDto, dailyStatisticsDto);
	}
	
	/**
	 * 以daily的统计数据与system的统计数据求和 
	 * 1：总移动设备接入次数 (当前数量+daily数量) 
	 * 2：总移动设备访问时长 (当前数量+daily数量)
	 * @param systemStatisticsDto
	 * @param dailyStatisticsDto
	 * @return
	 */
	public SystemStatisticsDTO systemStatisticsArith(SystemStatisticsDTO systemStatisticsDto, DailyStatisticsDTO dailyStatisticsDto){
		if(dailyStatisticsDto == null || systemStatisticsDto == null) return systemStatisticsDto;

		long total_handset_accesscount = dailyStatisticsDto.getAccesscount() + systemStatisticsDto.getHandset_accesscount();
		long total_handset_duration = dailyStatisticsDto.getDuration() + systemStatisticsDto.getHandset_duration();
		systemStatisticsDto.setHandset_accesscount(total_handset_accesscount);
		systemStatisticsDto.setHandset_duration(total_handset_duration);
		return systemStatisticsDto;
	}
	
	/**
	 * 以最新的数据生成系统统计dto 
	 * 1:总wifi设备数 通过查询wifi设备基础信息表来获取
	 * 2:总移动设备数 通过查询移动设备基础信息表来获取
	 * 3:在线wifi设备数 通过查询wifi设备基础信息表来获取
	 * 4:在线移动设备数 通过查询移动设备基础信息表来获取
	 * 生成的数据中不包含
	 * 1:总移动设备接入次数
	 * 2:总移动设备访问时长
	 * @return
	 */
	public SystemStatisticsDTO buildSystemStatisticsDto() throws IllegalAccessException, InvocationTargetException{
		SystemStatisticsDTO dto = new SystemStatisticsDTO();
		BeanUtils.copyProperties(dto, buildSystemStatisticsMap());
		return dto;
	}
	
	
	/**
	 * 以最新的数据生成系统统计map 
	 * 1:总wifi设备数 通过查询wifi设备基础信息表来获取
	 * 2:总移动设备数 通过查询移动设备基础信息表来获取
	 * 3:在线wifi设备数 通过查询wifi设备基础信息表来获取
	 * 4:在线移动设备数 通过查询移动设备基础信息表来获取
	 * 生成的数据中不包含
	 * 5:总移动设备接入次数
	 * 6:总移动设备访问时长
	 * @return
	 */
	public Map<String,String> buildSystemStatisticsMap(){
		Map<String,String> system_statistics_map = new HashMap<String,String>();
		system_statistics_map.put(SystemStatisticsDTO.Field_Devices, String.valueOf(wifiDeviceService.count()));
		system_statistics_map.put(SystemStatisticsDTO.Field_Handsets, String.valueOf(handsetDeviceService.count()));
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineDevices, String.valueOf(wifiDeviceService.countByOnline()));
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineHandsets, String.valueOf(handsetDeviceService.countByOnline()));
		return system_statistics_map;
	}
	
	/**
	 * 根据新上线的设备/终端 统计设备/终端的新增、活跃
	 * @param device_statist 上线的设备/终端
	 */
	public void deviceStatisticsOnline(DeviceStatistics device_statists, int type){
		List<DeviceStatistics> ds = new ArrayList<DeviceStatistics>(1);
		ds.add(device_statists);
		deviceStatisticsOnlines(ds, type);
	}
	
	/**
	 * 根据新上线的设备/终端 统计设备/终端的新增、活跃
	 * @param device_statists 上线的设备
	 */
	public void deviceStatisticsOnlines(List<DeviceStatistics> device_statists, int type){
		if(device_statists == null || device_statists.isEmpty()) return;
		
		//4:统计增量 wifi设备的daily启动次数增量
		int incr_statistics_accesscount = device_statists.size();
		//3:统计增量 wifi设备的daily新增设备增量
		int incr_statistics_news = 0;
		//3:统计增量 wifi设备的daily活跃设备增量
		int incr_statistics_active = 0;
		
		Date current_date = new Date();
		for(DeviceStatistics ds : device_statists){
			if(ds.isNewed()){
				incr_statistics_news++;
			}else{
				//如果最后的登录时间和今天不一样，说明今天是第一次登录
				if(!DateTimeHelper.isSameDay(ds.getLast_reged_at(), current_date)){
					incr_statistics_active++;
				}
			}
		}
		
		//3:统计增量 wifi设备的daily新增设备增量
		if(incr_statistics_news > 0){
			if(DeviceStatistics.Statis_Device_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_News, incr_statistics_news);
			}
			else if(DeviceStatistics.Statis_HandsetDevice_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_News, incr_statistics_news);
			}
		}
		//3:统计增量 wifi设备的daily活跃设备增量
		if(incr_statistics_active > 0){
			if(DeviceStatistics.Statis_Device_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Actives, incr_statistics_active);
			}
			else if(DeviceStatistics.Statis_HandsetDevice_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_Actives, incr_statistics_active);
			}
		}
		//4:统计增量 wifi设备的daily启动次数增量
		if(incr_statistics_accesscount > 0){
			if(DeviceStatistics.Statis_Device_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, incr_statistics_accesscount);
			}
			else if(DeviceStatistics.Statis_HandsetDevice_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_AccessCount, incr_statistics_accesscount);
			}
		}
	}
	
	/**
	 * 统计设备/终端离线
	 * @param uptime
	 * @param type
	 */
	public void deviceStatisticsOffline(long uptime, int type){
		if(uptime > 0){
			if(DeviceStatistics.Statis_Device_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsDeviceInnerPrefixKey, DailyStatisticsDTO.Field_Duration, uptime);
			}
			else if(DeviceStatistics.Statis_HandsetDevice_Type == type){
				DailyStatisticsHashService.getInstance().incrStatistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, DailyStatisticsDTO.Field_Duration, uptime);
			}
		}
	}
	
	
	/**
	 * 获取设备在线状态.
	 * @param mac 设备mac地址
	 * @return
	 * WIFI_DEVICE_STATUS_NOT_EXIST : 0 : 设备未接入
	 * WIFI_DEVICE_STATUS_ONLINE : 100 : 设备在线
	 * WIFI_DEVICE_STATUS_NOT_ONLINE : 99 : 设备不在线
	 * WIFI_DEVICE_STATUS_NOT_UROOTER : 98 : 设备不是Urooter设备
	 */
	public int getWifiDeviceOnlineStatus(String mac) {
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);

		if (wifiDevice == null) {
			return WIFI_DEVICE_STATUS_NOT_EXIST;
		} else if (!isURooterDevice(mac)) {
			return WIFI_DEVICE_STATUS_NOT_UROOTER;
		} else if (wifiDevice.isOnline()){
			return WIFI_DEVICE_STATUS_ONLINE;
		} else {
			return WIFI_DEVICE_STATUS_NOT_ONLINE;
		}
	}

	/**
	 * 判断设备是否是URooter设备
	 * @param mac
	 * @return
	 */
	public boolean isURooterDevice(String mac) {
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
		return wifiDevice.getOrig_model() !=null &&
				WIFI_DEVICE_ORIGIN_MODEL.equals(wifiDevice.getOrig_model());
	}

	
	/**************************  具体业务修改配置数据 封装 **********************************/
	
	/**
	 * 生成设备配置的广告配置数据
	 * @param mac
	 * @param ds_opt 修改设备配置的ds_opt
	 * @param extparams 修改配置具体的参数
	 * @return
	 */
	public String generateDeviceSetting(String mac, String ds_opt, String extparams){
		String modify_setting = null;
		
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		//System.out.println("generateDeviceSetting 1"+mac+" "+ ds_opt+" "+extparams);
		if(entity != null){
			WifiDeviceSettingDTO ds_dto = entity.getInnerModel();
			if(ds_dto != null){
				//System.out.println("generateDeviceSetting 2");
				String config_sequence = DeviceHelper.getConfigSequence(ds_dto);
				if(!StringUtils.isEmpty(config_sequence)){
					//System.out.println("generateDeviceSetting 3");
					OperationDS ods = OperationDS.getOperationCMDFromNo(ds_opt);
					if(ods != null){
						switch(ods){
							case DS_Ad:
								modify_setting = DeviceHelper.builderDSAdOuter(config_sequence, extparams, ds_dto);
								break;
							case DS_Power:
								modify_setting = DeviceHelper.builderDSPowerOuter(config_sequence, extparams, ds_dto);
								break;
							case DS_VapPassword:
								modify_setting = DeviceHelper.builderDSVapPasswordOuter(config_sequence, extparams, ds_dto);
								break;
							case DS_AclMacs:
								modify_setting = DeviceHelper.builderDSAclMacsOuter(config_sequence, extparams, ds_dto);
								break;
							case DS_RateControl:
								modify_setting = DeviceHelper.builderDSRateControlOuter(config_sequence, extparams, ds_dto);
								break;
							default:
								break;
						}
					}
				}
			}
		}
		return modify_setting;
	}

}
