package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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
	 * 获取在线的wifi设备列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public TailPage<WifiDevice> fetchWDevicesOnline(int pageNo, int pageSize){
		 return wifiDeviceService.findModelByOnline(pageNo, pageSize);
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
}
