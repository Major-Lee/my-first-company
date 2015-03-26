package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.api.dto.search.WifiDeviceSearchDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceRecentVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceCountRegionStatisticsStringService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.bhu.vas.business.search.service.device.WifiDeviceSearchService;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.es.exception.ESQueryValidateException;
import com.smartwork.msip.es.request.QueryResponse;

/**
 * device Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceRestBusinessFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSearchService wifiDeviceSearchService;
	
	/**
	 * 获取接入移动设备数量最多的wifi设备列表
	 * TODO：目前直接从mongodb中获取 后续改成后台程序定时从mongodb获取并放入指定的redis中 这边直接从redis提取数据
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		Pagination<WifiHandsetDeviceLoginCountMDTO> wifiDevices = wifiHandsetDeviceLoginCountMService.
				findWifiDevicesOrderMaxHandset(pageNo, pageSize);
		List<WifiHandsetDeviceLoginCountMDTO> mdtos = wifiDevices.getDatas();
		if(mdtos == null || mdtos.isEmpty()){
			return Collections.emptyList();
		}
		
		List<WifiDeviceMaxBusyVTO> vtos = new ArrayList<WifiDeviceMaxBusyVTO>();
		WifiDeviceMaxBusyVTO vto = null;
		for(WifiHandsetDeviceLoginCountMDTO mdto : mdtos){
			vto = new WifiDeviceMaxBusyVTO();
			vto.setWid(mdto.getId());
			vto.setHdc(mdto.getCount());
			vtos.add(vto);
		}
		return vtos;
	}
	
	/**
	 * 根据keyword来查询wifi设备
	 * 以当前在线和当前在线移动设备数量排序
	 * @param keyword
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ESQueryValidateException 
	 */
	public TailPage<WifiDeviceVTO> fetchWDeviceByKeyword(String keyword, int pageNo, int pageSize) 
			throws ESQueryValidateException{
		List<WifiDeviceVTO> vtos = null;
		
		QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchByKeyword(keyword, 
				(pageNo*pageSize)-pageSize, pageSize);
		
		int total = search_result.getTotal();
		if(total == 0){
			vtos = Collections.emptyList();
		}
		List<WifiDeviceSearchDTO> searchDtos = search_result.getResult();
		if(searchDtos.isEmpty()) {
			vtos = Collections.emptyList();
		}else{
			List<String> wifiIds = new ArrayList<String>();
			for(WifiDeviceSearchDTO searchDto : searchDtos){
				wifiIds.add(searchDto.getId());
			}
			List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
			vtos = new ArrayList<WifiDeviceVTO>();
			WifiDeviceVTO vto = null;
			WifiDeviceSearchDTO searchDto = null;
			int cursor = 0;
			for(WifiDevice entity : entitys){
				vto = new WifiDeviceVTO();
				searchDto = searchDtos.get(cursor);
				vto.setWid(searchDto.getId());
				vto.setOl(searchDto.getOnline());
				vto.setCohc(searchDto.getCount());
				vto.setAdr(searchDto.getAddress());
				vto.setDt(searchDto.getDevicetype());
				if(entity != null){
					vto.setDof(entity.getRx_bytes() > 0 ? (entity.getRx_bytes()/1024)+"KB" : "0KB");
					vto.setUof(entity.getTx_bytes() > 0 ? (entity.getTx_bytes()/1024)+"KB" : "0KB");
				}
				vtos.add(vto);
				cursor++;
			}
		}
		return new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos);
		
	}
	
	/**
	 * 获取统计数据的通用数据
		页面中统计数据体现：
		a、总设备数、总用户数、在线设备数、在线用户数、总接入次数、总用户访问时长
		b、今日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
		c、昨日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
	 * @return
	 */
	public StatisticsGeneralVTO fetchStatisticsGeneral(){
		StatisticsGeneralVTO vto = new StatisticsGeneralVTO();
		SystemStatisticsDTO systemStatisticsDto = SystemStatisticsHashService.getInstance().getStatistics();
		if(systemStatisticsDto == null) systemStatisticsDto = new SystemStatisticsDTO();
		deviceFacadeService.systemStatisticsArith(systemStatisticsDto);
		vto.setSystem(systemStatisticsDto);
		
		DailyStatisticsDTO daily_todayDto = DailyStatisticsHashService.getInstance().getStatistics(BusinessKeyDefine.
				Statistics.DailyStatisticsHandsetInnerPrefixKey);
		deviceFacadeService.dailyStatisticsArith(daily_todayDto);
		vto.setToday_daily(daily_todayDto);
		
		String yesterday_format = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), DateTimeHelper.FormatPattern5);
		DailyStatisticsDTO daily_yesterdayDto = DailyStatisticsHashService.getInstance().getStatistics(BusinessKeyDefine.
				Statistics.DailyStatisticsHandsetInnerPrefixKey, yesterday_format);
		vto.setYesterday_daily(daily_yesterdayDto);
		return vto;
	}
	
	/**
	 * 获取wifi设备地域分布饼图
	 * @param regions 地域名称 按逗号分隔
	 * @return
	 * @throws ESQueryValidateException 
	 */
	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions) throws ESQueryValidateException{
		if(StringUtils.isEmpty(regions)) return null;
		
		String regionCountJson = WifiDeviceCountRegionStatisticsStringService.getInstance().getWifiDeviceCountRegion();
		List<RegionCountDTO> dtos = null;
		if(StringUtils.isEmpty(regionCountJson)){
			//如果缓存失效 则从搜索引擎直接获取 (如果以后地域非常多,获取数据会相对耗时,可以放在定时程序去更新缓存)
			//获取总共的wifi设备数量
			long total_count = wifiDeviceSearchService.countByKeyword(null);
			long total_region_count = 0;
			dtos = new ArrayList<RegionCountDTO>();
			String[] regions_array = regions.split(StringHelper.COMMA_STRING_GAP);
			for(String region : regions_array){
				RegionCountDTO region_dto = new RegionCountDTO();
				//地域的wifi设备数量
				long region_count = 0;
				//地域对应显示内容
				String value = "0 0%";
				if(total_count > 0){
					region_count = wifiDeviceSearchService.countByKeyword(region);
					value = RegionCountDTO.builderValue(region_count, total_count);
				}
				region_dto.setR(region);
				region_dto.setV(value);
				dtos.add(region_dto);
				
				total_region_count = total_region_count + region_count;
			}
			//其他地域的wifi设备数量
			if(total_count > 0){
				RegionCountDTO other_region_dto = new RegionCountDTO();
				other_region_dto.setR("其他");
				other_region_dto.setV(RegionCountDTO.builderValue(total_count-total_region_count, total_count));
				dtos.add(other_region_dto);
			}
			regionCountJson = JsonHelper.getJSONString(dtos);
			//存入redis中
			WifiDeviceCountRegionStatisticsStringService.getInstance().setWifiDeviceCountRegion(regionCountJson);
		}else{
			dtos = JsonHelper.getDTOList(regionCountJson, RegionCountDTO.class);
		}
		return dtos;
	}
	
	/**
	 * 获取近期接入的wifi设备列表
	 * 近期 (最近30天)
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ESQueryValidateException
	 */
	public TailPage<WifiDeviceRecentVTO> fetchRecentWDevice(int pageNo, int pageSize) 
			throws ESQueryValidateException{
		List<WifiDeviceRecentVTO> vtos = null;
		
		long minRegisterAt = System.currentTimeMillis() - (30 * 3600 * 24 * 1000l);
		
		QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchGtByRegisterAt(minRegisterAt, 
				(pageNo*pageSize)-pageSize, pageSize);
		
		int total = search_result.getTotal();
		if(total == 0){
			vtos = Collections.emptyList();
		}
		List<WifiDeviceSearchDTO> searchDtos = search_result.getResult();
		if(searchDtos.isEmpty()) {
			vtos = Collections.emptyList();
		}else{
			vtos = new ArrayList<WifiDeviceRecentVTO>();
			WifiDeviceRecentVTO vto = null;
			for(WifiDeviceSearchDTO searchDto : searchDtos){
				vto = new WifiDeviceRecentVTO();
				vto.setWid(searchDto.getId());
				vto.setAdr(searchDto.getAddress());
				vto.setTs(searchDto.getRegister_at());
				vto.setWm(searchDto.getWorkmodel());
				vtos.add(vto);
			}
		}
		return new CommonPage<WifiDeviceRecentVTO>(pageNo, pageSize, total, vtos);
	}
	/**
	 * 根据wifi设备的id获取在线的移动设备列表
	 * @param wifiId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public TailPage<HandsetDeviceVTO> fetchHDevicesOnline(String wifiId, int pageNo, int pageSize){
		List<HandsetDeviceVTO> vtos = null;
		
		long total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(wifiId);
		if(total == 0){
			vtos = Collections.emptyList();
		}else{
			Set<Tuple> hdevicesList = WifiDeviceHandsetPresentSortedSetService.getInstance().
					fetchPresents(wifiId, (pageNo*pageSize)-pageSize, pageSize);
			if(hdevicesList.isEmpty()){
				vtos = Collections.emptyList();
			}else{
				vtos = new ArrayList<HandsetDeviceVTO>();
				HandsetDeviceVTO vto = null;
				for(Tuple tuple : hdevicesList){
					vto = new HandsetDeviceVTO();
					vto.setWid(wifiId);
					vto.setTs(new Double(tuple.getScore()).longValue());
					vto.setHid(tuple.getElement());
					vtos.add(vto);
				}
			}
		}
		return new CommonPage<HandsetDeviceVTO>(pageNo, pageSize, (int)total, vtos);
	}
}
