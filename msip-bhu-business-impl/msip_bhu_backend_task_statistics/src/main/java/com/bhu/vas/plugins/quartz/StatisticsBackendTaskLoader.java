package com.bhu.vas.plugins.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.statistics.DeviceStateStatisticsDTO;
import com.bhu.vas.api.dto.statistics.UserStateStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStateStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.UserStateStatisticsHashService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author xiaowei
 * 
 */
public class StatisticsBackendTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(StatisticsBackendTaskLoader.class);

	@Resource
	private WifiDeviceService wifiDeviceService;

    @Resource
    private UserService userService;
	
	public void execute() {
		logger.info("StatisticsBackendTaskLoader start...");
		
		Calendar cal = Calendar.getInstance();
		
		
		logger.info("Statistics DevicesState...");
		DeviceStateStatisticsDTO deviceDto = fetchDetivcStateStatistics(cal.getTime());
		DeviceStateStatisticsHashService.getInstance().timeIntervalAllSet(cal.getTime(), deviceDto);
		
		logger.info("Statistics UserState...");
		UserStateStatisticsDTO userDto = fetchUserStateStatistics(cal.getTime());
		UserStateStatisticsHashService.getInstance().timeIntervalAllSet(cal.getTime(), userDto);
		
		logger.info("StatisticsBackendTaskLoader end...");
	}
	
	public DeviceStateStatisticsDTO fetchDetivcStateStatistics(Date date){
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(date);
		DeviceStateStatisticsDTO dto = new DeviceStateStatisticsDTO();
		
		dto.setCountsDevices(wifiDeviceService.count());
		dto.setOnline(wifiDeviceService.countByOnline());

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLike("created_at",fragments.get(DateTimeExtHelper.YEAR_MONTH_DD)+"%");
		dto.setNewInc(wifiDeviceService.countByModelCriteria(mc));
		
		ModelCriteria newMc = new ModelCriteria();
		newMc.createCriteria().andColumnLike("updated_at",fragments.get(DateTimeExtHelper.YEAR_MONTH_DD)+"%");
		dto.setLiveness(wifiDeviceService.countByModelCriteria(newMc));
		
		dto.setOnline_max(dto.getOnline());
		
		return dto;
	}
	
	public UserStateStatisticsDTO fetchUserStateStatistics(Date date){
		
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(date);
		
		UserStateStatisticsDTO dto = new UserStateStatisticsDTO();
		dto.setCountsUser(userService.count());
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLike("created_at",fragments.get(DateTimeExtHelper.YEAR_MONTH_DD)+"%");
		dto.setNewInc(userService.countByModelCriteria(mc));
		
		ModelCriteria newMc = new ModelCriteria();
		newMc.createCriteria().andColumnLike("updated_at",fragments.get(DateTimeExtHelper.YEAR_MONTH_DD)+"%");
		dto.setCurrentUser(userService.countByModelCriteria(newMc));
		
		ModelCriteria newMc2 = new ModelCriteria();
		newMc2.createCriteria().andColumnLike("updated_at",fragments.get(DateTimeExtHelper.YEAR_MONTH_DD_HH)+"%");
		dto.setOnline(userService.countByModelCriteria(newMc2));
		
		dto.setOnline_max(dto.getOnline());
		
		return dto;
	}
}
