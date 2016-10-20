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
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
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
public class AdvertiseBackendTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseBackendTaskLoader.class);

	@Resource
	private AdvertiseService advertiseService;

    @Resource
    private UserService userService;
	
	public void execute() {
		logger.info("AdvertiseBackendTaskLoader start...");
		
		logger.info("AdvertiseBackendTaskLoader end...");
	}
}
