package com.bhu.vas.plugins.quartz;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;

/**
* 每5分钟启动一次
* 统计计算系统数据并写入redis
* 1:总wifi设备数
* 2:总移动设备数
* 3:在线wifi设备数
* 4:在线移动设备数
* 5:总移动设备接入次数 (通过每日定时程序来生成)
* 6:总移动设备访问时长 (通过每日定时程序来生成)
 * @author tangzichao
 *
 */
public class SystemStatisticsLoader {
	private static Logger logger = LoggerFactory.getLogger(SystemStatisticsLoader.class);
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	public void execute() {
		try{
			logger.info("SystemStatisticsLoader starting...");
			
			Map<String, String> system_statistics_map = deviceFacadeService.buildSystemStatisticsMap();
			SystemStatisticsHashService.getInstance().initOrReset2Statistics(system_statistics_map);
			
			logger.info("SystemStatisticsLoader ended");
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("SystemStatisticsLoader throw exception", ex);
		}
	}
	
}
