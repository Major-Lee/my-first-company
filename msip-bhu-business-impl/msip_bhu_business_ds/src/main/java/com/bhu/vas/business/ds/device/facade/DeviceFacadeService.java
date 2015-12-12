package com.bhu.vas.business.ds.device.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.api.dto.ret.param.ParamVasModuleDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.dto.statistics.DeviceStatistics;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.IGenerateDeviceSetting;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevicePersistenceCMDState;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.PushMessageConstant;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceModeStatusService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceStateService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingAddressDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingResultDTO;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceFacadeService implements IGenerateDeviceSetting{
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

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	/*@Resource
	private HandsetDeviceService handsetDeviceService;*/
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private UserMobileDeviceService userMobileDeviceService;
	
	@Resource
	private UserMobileDeviceStateService userMobileDeviceStateService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	
	@Resource
	private UserSettingStateService userSettingStateService;

	
	/**
	 * 指定wifiId进行终端全部下线处理
	 * @param wifiId
	 * modified by Edmond Lee for handset storage
	 *
	 * @return  在线设备
	 */
	public List<HandsetDeviceDTO> allHandsetDoOfflines(String wifiId){
		List<String> onlinePresents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchAllOnlinePresents(wifiId);
		if(onlinePresents != null && !onlinePresents.isEmpty()){
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(onlinePresents);
			List<HandsetDeviceDTO> do_offline_handsets = new ArrayList<HandsetDeviceDTO>();
			for(HandsetDeviceDTO dto:handsets){
				if(dto != null){
					dto.setAction(HandsetDeviceDTO.Action_Offline);
					do_offline_handsets.add(dto);

				}
				//dto.setAction(HandsetDeviceDTO.Action_Offline);
			}
			HandsetStorageFacadeService.handsetsComming(do_offline_handsets);
			//清除设备在线终端列表
			WifiDeviceHandsetPresentSortedSetService.getInstance().changeOnlinePresentsToOffline(wifiId);
			return handsets;
		}
		/*List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(wifiId);
		if(!handset_devices_online_entitys.isEmpty()){
			for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
				handset_devices_online_entity.setOnline(false);
			}
			handsetDeviceService.updateAll(handset_devices_online_entitys);
		}*/
		return null;
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
	 * modified by Edmond Lee for handset storage
	 */
	public Map<String,String> buildSystemStatisticsMap(){
		Map<String,String> system_statistics_map = new HashMap<String,String>();
		system_statistics_map.put(SystemStatisticsDTO.Field_Devices, String.valueOf(wifiDeviceService.count()));
		//system_statistics_map.put(SystemStatisticsDTO.Field_Handsets, String.valueOf());//HandsetStorageFacadeService.countAll()));//String.valueOf(handsetDeviceService.count()));
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineDevices, String.valueOf(wifiDeviceService.countByOnline()));
		//system_statistics_map.put(SystemStatisticsDTO.Field_OnlineHandsets, String.valueOf());//handsetDeviceService.countByOnline()));
		int[] statistics = HandsetStorageFacadeService.statistics();
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineHandsets, String.valueOf(statistics[0]));
		system_statistics_map.put(SystemStatisticsDTO.Field_Handsets, String.valueOf(statistics[1]));
		
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
		}
		
		if (!WifiDeviceHelper.isURouterDevice(wifiDevice.getOrig_swver())) {
			return WIFI_DEVICE_STATUS_NOT_UROOTER;
		}
		
		if (wifiDevice.isOnline()){
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
	public boolean isURouterDevice(String mac) {
		if(StringUtils.isEmpty(mac)) return false;
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
		if(wifiDevice == null) return false;
		return WifiDeviceHelper.isURouterDevice(wifiDevice.getOrig_swver());
	}
	

	/**
	 * 验证用户所管理的设备
	 * 1：设备是否存在
	 * 2：设备是否在线
	 * 3：设备是否被此用户管理
	 * @param uid
	 * @param mac
	 * @return
	 */
	public WifiDevice validateUserDevice(Integer uid, String mac){
		//验证设备
		WifiDevice device_entity = validateDevice(mac);
		//验证用户是否管理设备
		UserDevice userdevice_entity = userDeviceService.getById(new UserDevicePK(mac, uid));
		if(userdevice_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_BINDED,new String[]{mac});
		}
		return device_entity;
	}
	
	/**
	 * 验证设备
	 * 1：设备是否存在
	 * 2：设备是否在线
	 * @param mac
	 * @return
	 */
	public WifiDevice validateDevice(String mac){
		//验证设备是否存在
		WifiDevice device_entity = wifiDeviceService.getById(mac);
		if(device_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{mac});
		}
		//验证设备是否在线
		if(!device_entity.isOnline()){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,new String[]{mac});
		}
		return device_entity;
	}
	
	public WifiDevice validateDeviceIgoneOffline(String mac){
		//验证设备是否存在
		WifiDevice device_entity = wifiDeviceService.getById(mac);
		if(device_entity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{mac});
		}
		/*//验证设备是否在线
		if(!device_entity.isOnline()){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE);
		}*/
		return device_entity;
	}
	/**
	 * 验证设备是否加载配置
	 * @param mac
	 * @return
	 */
	public WifiDeviceSetting validateDeviceSetting(String mac){
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_NOTEXIST);
		}
		if(entity.getInnerModel() == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
		}
		return entity;
	}
	/**
	 * 验证设备是否存在配置数据并且返回配置数据dto
	 * @param mac
	 * @return
	 */
	public WifiDeviceSettingDTO validateDeviceSettingAndGet(String mac){
		return validateDeviceSetting(mac).getInnerModel();
	}
	
	/**
	 * 获取用户绑定的设备PKS
	 * @param uid
	 * @return
	 */
	public List<UserDevicePK> getUserDevices(Integer uid){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
		return userDeviceService.findIdsByCommonCriteria(mc);
	}
	
	public UserDevice getUserDevice(Integer uid, String mac){
		return userDeviceService.getById(new UserDevicePK(mac, uid));
	}
	
	public String getUserDeviceName(Integer uid, String mac){
		UserDevice entity = this.getUserDevice(uid, mac);
		if(entity != null){
			return entity.getDevice_name();
		}
		return null;
	}
	
	/**
	 * 更新设备的mode状态信息
	 * @param mac
	 * @param dto
	 */
	public void updateDeviceModeStatus(String mac, WifiDeviceSettingLinkModeDTO dto){
		if(StringUtils.isEmpty(mac)) return;
		if(dto != null/* && !StringUtils.isEmpty(dto)*/){
			WifiDeviceModeStatusService.getInstance().addPresent(mac, JsonHelper.getJSONString(dto));
		}
	}
	
	/**
	 * 获取设备的mode状态信息
	 * @param mac
	 * @return
	 */
	public WifiDeviceSettingLinkModeDTO getDeviceModeStatus(String mac){
		if(StringUtils.isEmpty(mac)) return null;
		String mode_status_json = WifiDeviceModeStatusService.getInstance().getPresent(mac);
		if(StringUtils.isEmpty(mode_status_json))  return null;
		return JsonHelper.getDTO(mode_status_json, WifiDeviceSettingLinkModeDTO.class);
	}
	
	/**
	 * 根据设备mac获取设备配置数据
	 * @param mac
	 * @return
	 */
	public WifiDeviceSettingDTO queryDeviceSettingDTO(String mac){
		if(StringUtils.isEmpty(mac)) return null;
		
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity != null){
			return entity.getInnerModel();
		}
		return null;
	}


	/**
	 * 获取urouter的ssid
	 *
	 * @param mac
	 * @return
	 */
	public String getUrouterSSID(String mac) {
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if (entity != null) {
			WifiDeviceSettingDTO wifiDeviceSettingDTO = entity.getInnerModel();
			List<WifiDeviceSettingVapDTO> vaps = wifiDeviceSettingDTO.getVaps();
			if(vaps == null || vaps.isEmpty()) {
				return null;
			}

			for(WifiDeviceSettingVapDTO vap : vaps){
				if(WifiDeviceSettingVapDTO.Enable.equalsIgnoreCase(vap.getEnable())
						&& !WifiDeviceSettingVapDTO.Enable.equalsIgnoreCase(vap.getGuest_en())){
					return vap.getSsid();
				}
			}
		}
		return null;
	}

	/**
	 * 查询终端名称
	 * 1:根据设备配置查询是否存在别名
	 * 2:查询是否存在hostname
	 * 3:如果1，2都没有，则返回null
	 * @param hd_mac
	 * @param mac
	 * @return
	 */
/*	public String queryPushHandsetDeviceName(String hd_mac, String mac){


	}*/
	/**
	 * 获取终端别名
	 * @param hd_mac
	 * @param mac
	 * @return
	 */
	public String queryPushHandsetDeviceAliasName(String hd_mac, String mac){
		WifiDeviceSettingDTO setting_dto = queryDeviceSettingDTO(mac);
		if(setting_dto != null){
			//查询终端别名
			String alias = DeviceHelper.getHandsetDeviceAlias(hd_mac, setting_dto);
			if(!StringUtils.isEmpty(alias)){
				return StringHelper.chopMiddleString(alias, 16, StringHelper.ELLIPSIS_STRING_GAP);
			}
		}
		return null;
	}
	
	/**
	 * 获取终端主机名
	 * @param hd_mac
	 * @param mac
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public String queryPushHandsetDeviceHostname(String hd_mac, String mac){
		
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(hd_mac);
		//System.out.println(String.format("queryPushHandsetDeviceHostname handset [%s] hd_mac [%s]", handset, hd_mac));
		if(handset != null){
			String hostname = handset.getDhcp_name();
			//System.out.println(String.format("queryPushHandsetDeviceHostname handset [%s] hostname [%s]", handset, hostname));
			if(!StringUtils.isEmpty(hostname)){
				if(hostname.toLowerCase().startsWith(PushMessageConstant.Android_Host_Name_Match)){
					return PushMessageConstant.Android_Host_Name;
				}
				return StringHelper.chopMiddleString(hostname, 16, StringHelper.ELLIPSIS_STRING_GAP);
			}
		}
		return null;
		//如果没有别名 以终端主机名填充
		/*HandsetDevice hd_entity = handsetDeviceService.getById(hd_mac);
		if(hd_entity != null){
			String hostname = hd_entity.getHostname();
			if(!StringUtils.isEmpty(hostname)){
				if(hostname.toLowerCase().startsWith(PushMessageConstant.Android_Host_Name_Match)){
					return PushMessageConstant.Android_Host_Name;
				}
				return StringHelper.chopMiddleString(hostname, 16, StringHelper.ELLIPSIS_STRING_GAP);
			}
		}
		return null;*/
	}
	
	/**
	 * 获取设备的名称
	 * 1：根据用户设备绑定信息中查询设备别名
	 * 2：如果没有 则返回null
	 * @param uid
	 * @param mac
	 * @return
	 */
	public String queryDeviceName(Integer uid, String mac){
		UserDevice userDevice = userDeviceService.getById(new UserDevicePK(mac, uid));
		if(userDevice != null){
			if(!StringUtils.isEmpty(userDevice.getDevice_name())){
				return userDevice.getDevice_name();
			}
		}
		return null;
	}
	/**
	 * 用户注册app移动设备信息
	 * 1:当前用户使用app移动设备数据
	 * 2:用户使用app移动设备历史数据
	 * 3:用户所管理的设备的数据关系
	 * @param uid
	 * @param d  devicetype
	 * @param dt 设备token
	 * @param dm 设备mac
	 * @param cv client 系统版本号
	 * @param pv client production 版本号
	 * @param ut 设备型号
	 * @param pt (push type 针对ios的不同证书的参数)
	 * @return
	 */
	public void userMobileDeviceRegister(Integer uid, String d, String dt, String dm, 
			String cv, String pv, String ut, String pt){
		if(uid == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt) || StringUtils.isEmpty(pt)){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		DeviceEnum de = DeviceEnum.getBySName(d);
		if(de == null || !DeviceEnum.isHandsetDevice(de)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_TYPE_NOT_SUPPORTED);
		}
		//1:当前用户使用app移动设备数据
		userMobileDeviceService.deviceRegister(uid, dm, dt, d, pt);
		//2:用户使用app移动设备历史数据
		userMobileDeviceStateService.userNewDeviceRegisterOrReplace(uid, de, dm, dt, cv, pv, ut, pt);
		//3:用户所管理的设备的数据关系
		this.generateDeviceMobilePresents(uid, new DeviceMobilePresentDTO(uid, d, dt, pt, dm));
	}
	
	
	/**
	 * 用户注销app移动设备信息
	 * 1:清除当前用户使用app移动设备数据
	 * 2:修改用户使用app移动设备历史数据
	 * 3:清除用户所管理的设备的数据关系
	 * @param uid
	 * @param d
	 * @param dt
	 */
	public void userMobileDeviceDestory(Integer uid, String d, String dt){
		if(uid == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		if(StringUtils.isEmpty(d) || StringUtils.isEmpty(dt)){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		DeviceEnum de = DeviceEnum.getBySName(d);
		if(de == null || !DeviceEnum.isHandsetDevice(de)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_TYPE_NOT_SUPPORTED);
		}
		//1:清除当前用户使用app移动设备数据
		userMobileDeviceService.destoryRegister(uid, dt);
		//2:修改用户使用app移动设备历史数据
		userMobileDeviceStateService.userDeviceSignedOff(uid, dt, de);
		//3:清除用户所管理的设备的数据关系
		this.clearDeviceMobilePresents(uid);
	}
	
	/**
	 * 移除设备与用户移动设备信息的关联
	 * @param uid
	 * @param mac
	 */
	public void removeMobilePresent(Integer uid, String mac){
		WifiDeviceMobilePresentStringService.getInstance().destoryMobilePresent(mac);
		this.generateDeviceMobilePresents(uid);
	}
	
	/**
	 * 设备重置 解绑、清除相关设备和用户之间的数据
	 * @param mac
	 */
	public void deviceResetDestory(String mac){
		System.out.println("~~~~~~~~~~1:设备重置解除绑定操作："+mac);
		//现在一台设备只能被一个客户端绑定。此处考虑冗余兼容可能出现的多个用户绑定单个设备的情况
		List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
		List<Integer> uids = new ArrayList<Integer>();
        for (UserDevice bindDevice : bindDevices) {
        	uids.add(bindDevice.getUid());
        }
        System.out.println("~~~~~~~~~~2:设备绑定用户："+uids);
        for(Integer uid :uids){
        	UserDevicePK userDevicePK = new UserDevicePK(mac, uid);
        	System.out.println("~~~~~~~~~~21:设备绑定用户清除："+uid);
        	userDeviceService.deleteById(userDevicePK);
        	System.out.println("~~~~~~~~~~22:设备状态清除："+uid);
        	/*String present = WifiDeviceMobilePresentStringService.getInstance().getMobilePresent(mac);
        	System.out.println("~~~~~~~~~~221:清除前：present:"+present);*/
        	this.removeMobilePresent(uid, mac);
        	/*present = WifiDeviceMobilePresentStringService.getInstance().getMobilePresent(mac);
        	System.out.println("~~~~~~~~~~221:清除后：present:"+present);*/
        	System.out.println("~~~~~~~~~~23:设备插件状态清除："+uid);
			userSettingStateService.deleteById(mac);
			//System.out.println("~~~~~~~~~~24:设备插件状态清除："+uid);
			/*//如果没有绑定其他设备，删除别名
			int count = userDeviceService.countBindDevices(uid);
			if(count == 0 ){
				WifiDeviceHandsetAliasService.getInstance().hdelHandsetAlias(uid, mac);
			}*/
        }
        System.out.println("~~~~~~~~~~3:deviceResetDestory ok!");
	}
	
	
	/**
	 * 根据用户所管理的设备 生成mobile和设备的关系信息
	 * @param uid
	 * @param presentDto
	 */
	public void generateDeviceMobilePresents(Integer uid, DeviceMobilePresentDTO presentDto){
		if(uid == null || presentDto == null) return;
		
		List<UserDevicePK> userDevices = this.getUserDevices(uid);
		int size = userDevices.size();
		if(size == 0) return;
		
		List<String> macs = new ArrayList<String>();
		for(UserDevicePK pk : userDevices){
			macs.add(pk.getMac());
		}
		
		presentDto.setMulti(size > 1 ? true : false);
		WifiDeviceMobilePresentStringService.getInstance().setMobilePresents(macs, 
				JsonHelper.getJSONString(presentDto));
	}
	
	/**
	 * 根据用户所管理的设备 生成mobile和设备的关系信息
	 * @param uid
	 */
	public void generateDeviceMobilePresents(Integer uid){
		if(uid == null) return;
		
		UserMobileDevice entity = userMobileDeviceService.getById(uid);
		if(entity != null){
			this.generateDeviceMobilePresents(uid, new DeviceMobilePresentDTO(uid, entity.getD(), entity.getDt(),
					entity.getPt(), entity.getDm()));
		}
	}
	
	/**
	 * 根据用户所管理的设备 清除mobile和设备的关系信息
	 * @param uid
	 */
	public void clearDeviceMobilePresents(Integer uid){
		if(uid == null) return;
		
		List<UserDevicePK> userDevices = this.getUserDevices(uid);
		if(userDevices.isEmpty()) return;
		
		List<String> macs = new ArrayList<String>();
		for(UserDevicePK pk : userDevices){
			macs.add(pk.getMac());
		}
		WifiDeviceMobilePresentStringService.getInstance().destoryMobilePresent(macs);
	}

	
	/**************************  具体业务修改配置数据 封装 **********************************/
	//修改设备配置的通用序列号
	public static final String Common_Config_Sequence = "-1";
	
	/**
	 * 生成设备配置的广告配置数据
	 * @param mac
	 * @param ods 修改设备配置的ds_opt
	 * @param extparams 修改配置具体的参数
	 * @return
	 * @throws Exception 
	 */
	public String generateDeviceSetting(String mac, OperationDS ods, String extparams) throws Exception {
		if(ods == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		String config_sequence = Common_Config_Sequence;
		
		switch(ods){
			case DS_Http_Ad_Start:
				return DeviceHelper.builderDSHttpAdStartOuter(config_sequence, extparams);
			case DS_Http_Ad_Stop:
				return DeviceHelper.builderDSHttpAdStopOuter(config_sequence);	
			/*case DS_Http_Redirect_Start:
				return DeviceHelper.builderDSHttpRedirectStartOuter(config_sequence, extparams);
			case DS_Http_Redirect_Stop:
				return DeviceHelper.builderDSHttpRedirectStopOuter(config_sequence);
				
			case DS_Http_404_Start:
				return DeviceHelper.builderDSHttp404StartOuter(config_sequence, extparams);
			case DS_Http_404_Stop:
				return DeviceHelper.builderDSHttp404StopOuter(config_sequence);*/
			/*case DS_Http_Portal_Start:
				return DeviceHelper.builderDSStartHttpPortalOuter(config_sequence, extparams);
			case DS_Http_Portal_Stop:
				return DeviceHelper.builderDSStopHttpPortalOuter(config_sequence);*/
			case DS_VistorWifi_Limit:
				return DeviceHelper.builderDSLimitVisitorWifiOuter(extparams);
			case DS_VistorWifi_Start:
				return DeviceHelper.builderDSStartVisitorWifiOuter(extparams);
			case DS_VistorWifi_Stop:
				return DeviceHelper.builderDSStopVisitorWifiOuter();
				
			case DS_Power:
				return DeviceHelper.builderDSPowerOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
			case DS_RealChannel:
				return DeviceHelper.builderDSRealChannelOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
			case DS_VapPassword:
				return DeviceHelper.builderDSVapPasswordOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
			case DS_AclMacs:
				return DeviceHelper.builderDSAclMacsOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
			case DS_RateControl:
				return DeviceHelper.builderDSRateControlOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
			case DS_AdminPassword:
				return DeviceHelper.builderDSAdminPasswordOuter(config_sequence, extparams);
			case DS_LinkMode:
				return DeviceHelper.builderDSLinkModeOuter(config_sequence, extparams);
			case DS_MM:
				return DeviceHelper.builderDSHDAliasOuter(config_sequence, extparams, validateDeviceSettingAndGet(mac));
//			case DS_VapGuest:
//				return DeviceHelper.builderDSVapGuestOuter(config_sequence, extparams, ds_dto);
			default:
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
	}
	
	/**
	 * 获取持久化指令中的vapmodule支持的增值指令
	 * 只有404和redirect
	 * @param mac
	 * @return
	 */
	public List<String> fetchWifiDevicePersistenceVapModuleCMD(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		List<OperationDS> vap_module_ds = null;
		List<String> vap_module_ds_extparams = null;
		try{
			payloads = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null){// || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					if(WifiDeviceHelper.isVapCmdModuleSupported(opt_cmd,ods_cmd)){// && WifiDeviceHelper.isVapModuleSupported(wifiDevice.getOrig_swver())){
						if(vap_module_ds == null) vap_module_ds = new ArrayList<>();
						if(vap_module_ds_extparams == null) vap_module_ds_extparams = new ArrayList<>();
						if(OperationDS.DS_Http_VapModuleCMD_Start == ods_cmd){
							ParamVasModuleDTO param_dto = JsonHelper.getDTO(dto.getExtparams(), ParamVasModuleDTO.class);
							if(param_dto == null || StringUtils.isEmpty(param_dto.getStyle()))
								continue;
							VasModuleCmdDefined cmdDefined = vasModuleCmdDefinedService.getById(new VasModuleCmdPK(ods_cmd.getRef(),param_dto.getStyle()));
							if(cmdDefined == null || StringUtils.isEmpty(cmdDefined.getTemplate())){
								continue;
							}
							payloads.add(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(),cmdDefined.getTemplate()));
						}else if(OperationDS.DS_Http_VapModuleCMD_Stop == ods_cmd){
							payloads.add(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), DeviceHelper.DeviceSetting_VapModuleFull_Stop));
						}else{
							vap_module_ds.add(ods_cmd);
							vap_module_ds_extparams.add(dto.getExtparams());
						}
					}
				}
			}
			/*//取消老的增值指令下发数据 20151023
			if(vap_module_ds != null && !vap_module_ds.isEmpty()){
				String cmd = CMDBuilder.autoBuilderVapCMD4Opt(OperationCMD.ModifyDeviceSetting,vap_module_ds.toArray(new OperationDS[0]),mac,
						CMDBuilder.auto_taskid_fragment.getNextSequence(),vap_module_ds_extparams.toArray(new String[0]));
				if(StringUtils.isNotEmpty(cmd))
					payloads.add(cmd);
			}*/
			return payloads;
		}finally{
			if(vap_module_ds != null){
				vap_module_ds.clear();
				vap_module_ds = null;
			}
			if(vap_module_ds_extparams != null){
				vap_module_ds_extparams.clear();
				vap_module_ds_extparams = null;
			}
		}
	}
	
	/**
	 * 获取持久化指令中除vapmodule支持的增值指令的其他指令
	 * @param mac
	 * @return
	 */
	public List<String> fetchWifiDevicePersistenceExceptVapModuleCMD(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		try{
			payloads = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			StringBuilder sb_setting_inner = new StringBuilder();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					switch(ods_cmd){
						case DS_Http_Ad_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpAdStartFragmentOuter(dto.getExtparams()));
							break;	
						/*case DS_Http_404_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttp404StartFragmentOuter(dto.getExtparams()));
							break;	
						case DS_Http_Redirect_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpRedirectStartFragmentOuter(dto.getExtparams()));
							break;*/	
						default:
							break;
					}
					//}
				}else{
					payloads.add(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, ods_cmd, mac,0, dto.getExtparams(), this));
				}
			}
			if(sb_setting_inner.length() > 0){
				WifiDeviceSetting entity = validateDeviceSetting(mac);
				WifiDeviceSettingDTO ds_dto = entity.getInnerModel();
				String config_sequence = DeviceHelper.getConfigSequence(ds_dto);
				if(StringUtils.isEmpty(config_sequence))
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_SEQUENCE_NOTEXIST);
				payloads.add(
						CMDBuilder.builderDeviceSettingModify(
								mac, 
								CMDBuilder.auto_taskid_fragment.getNextSequence(), 
								DeviceHelper.builderDSHttpVapSettinStartOuter(config_sequence,sb_setting_inner.toString())));
			}
			return payloads;
		}finally{
		}
	}
	
	
/*	public List<String> fetchWifiDevicePersistenceCMD4VapModuleSupportedDevice(String mac,boolean ignoreVapModule){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		List<OperationDS> vap_module_ds = null;
		List<String> vap_module_ds_extparams = null;
		WifiDevice wifiDevice = null;
		try{
			payloads = new ArrayList<>();
			wifiDevice = wifiDeviceService.getById(mac);
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			StringBuilder sb_setting_inner = new StringBuilder();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					if(WifiDeviceHelper.isCmdVapModuleSupported(opt_cmd,ods_cmd) && WifiDeviceHelper.isVapModuleSupported(wifiDevice.getOrig_swver())){
						if(vap_module_ds == null) vap_module_ds = new ArrayList<>();
						if(vap_module_ds_extparams == null) vap_module_ds_extparams = new ArrayList<>();
						vap_module_ds.add(ods_cmd);
						vap_module_ds_extparams.add(dto.getExtparams());
					}else{
						switch(ods_cmd){
							case DS_Http_Ad_Start:
								sb_setting_inner.append(DeviceHelper.builderDSHttpAdStartFragmentOuter(dto.getExtparams()));
								break;	
							case DS_Http_404_Start:
								sb_setting_inner.append(DeviceHelper.builderDSHttp404StartFragmentOuter(dto.getExtparams()));
								break;	
							case DS_Http_Redirect_Start:
								sb_setting_inner.append(DeviceHelper.builderDSHttpRedirectStartFragmentOuter(dto.getExtparams()));
								break;	
							default:
								break;
						}
					}
				}else{
					payloads.add(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, ods_cmd, mac,0, dto.getExtparams(),wifiDevice.getOrig_swver(), this));
				}
			}
			
			if(sb_setting_inner.length() > 0){
				WifiDeviceSetting entity = validateDeviceSetting(mac);
				WifiDeviceSettingDTO ds_dto = entity.getInnerModel();
				String config_sequence = DeviceHelper.getConfigSequence(ds_dto);
				if(StringUtils.isEmpty(config_sequence))
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_SEQUENCE_NOTEXIST);
				payloads.add(
						CMDBuilder.builderDeviceSettingModify(
								mac, 
								CMDBuilder.auto_taskid_fragment.getNextSequence(), 
								DeviceHelper.builderDSHttpVapSettinStartOuter(config_sequence,sb_setting_inner.toString())));
			}
			if(!ignoreVapModule){
				if(vap_module_ds != null && !vap_module_ds.isEmpty()){
					String cmd = CMDBuilder.autoBuilderVapCMD4Opt(OperationCMD.ModifyDeviceSetting,vap_module_ds.toArray(new OperationDS[0]),mac,
							CMDBuilder.auto_taskid_fragment.getNextSequence(),vap_module_ds_extparams.toArray(new String[0]));
					if(StringUtils.isNotEmpty(cmd))
						payloads.add(cmd);
				}
			}
			return payloads;
		}finally{
			if(vap_module_ds != null){
				vap_module_ds.clear();
				vap_module_ds = null;
			}
			
			if(vap_module_ds_extparams != null){
				vap_module_ds_extparams.clear();
				vap_module_ds_extparams = null;
			}
		}

	}
	
	*//**
	 * 获取vapmodule模块的指令 只有新版本的需要，只有404和redirect
	 * 
	 * @param mac
	 * @return
	 *//*
	public List<String> fetchWifiDevicePersistenceOnlyVapModuleCMD(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		List<OperationDS> vap_module_ds = null;
		List<String> vap_module_ds_extparams = null;
		//WifiDevice wifiDevice = null;
		try{
			payloads = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					if(WifiDeviceHelper.isCmdVapModuleSupported(opt_cmd,ods_cmd) && WifiDeviceHelper.isVapModuleSupported(wifiDevice.getOrig_swver())){
						if(vap_module_ds == null) vap_module_ds = new ArrayList<>();
						if(vap_module_ds_extparams == null) vap_module_ds_extparams = new ArrayList<>();
						vap_module_ds.add(ods_cmd);
						vap_module_ds_extparams.add(dto.getExtparams());
					}
				}
			}
			if(vap_module_ds != null && !vap_module_ds.isEmpty()){
				String cmd = CMDBuilder.autoBuilderVapCMD4Opt(OperationCMD.ModifyDeviceSetting,vap_module_ds.toArray(new OperationDS[0]),mac,
						CMDBuilder.auto_taskid_fragment.getNextSequence(),vap_module_ds_extparams.toArray(new String[0]));
				if(StringUtils.isNotEmpty(cmd))
					payloads.add(cmd);
			}
			return payloads;
		}finally{
			if(vap_module_ds != null){
				vap_module_ds.clear();
				vap_module_ds = null;
			}
			
			if(vap_module_ds_extparams != null){
				vap_module_ds_extparams.clear();
				vap_module_ds_extparams = null;
			}
		}
	}
	
	*//**
	 * 对于设备配置的指令需要合并一起提交
	 * 针对vapmodule指定以前的设备
	 * @param mac
	 * @return
	 *//*
	public List<String> fetchWifiDevicePersistenceCMD4VapModuleNotSupportedDevice(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		//WifiDevice wifiDevice = null;
		try{
			payloads = new ArrayList<>();
			//wifiDevice = wifiDeviceService.getById(mac);
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			StringBuilder sb_setting_inner = new StringBuilder();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					switch(ods_cmd){
						case DS_Http_Ad_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpAdStartFragmentOuter(dto.getExtparams()));
							break;	
						case DS_Http_404_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttp404StartFragmentOuter(dto.getExtparams()));
							break;	
						case DS_Http_Redirect_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpRedirectStartFragmentOuter(dto.getExtparams()));
							break;	
						default:
							break;
					}
					//}
				}else{
					payloads.add(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, ods_cmd, mac,0, dto.getExtparams(),null, this));
				}
			}
			if(sb_setting_inner.length() > 0){
				WifiDeviceSetting entity = validateDeviceSetting(mac);
				WifiDeviceSettingDTO ds_dto = entity.getInnerModel();
				String config_sequence = DeviceHelper.getConfigSequence(ds_dto);
				if(StringUtils.isEmpty(config_sequence))
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_SEQUENCE_NOTEXIST);
				payloads.add(
						CMDBuilder.builderDeviceSettingModify(
								mac, 
								CMDBuilder.auto_taskid_fragment.getNextSequence(), 
								DeviceHelper.builderDSHttpVapSettinStartOuter(config_sequence,sb_setting_inner.toString())));
			}
			
			return payloads;
		}finally{
		}

	}*/

}
