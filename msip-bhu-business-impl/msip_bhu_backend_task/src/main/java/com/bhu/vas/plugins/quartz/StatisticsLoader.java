package com.bhu.vas.plugins.quartz;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * 每天凌晨0点1分启动的定时程序 
 * a:系统统计数据计算
 * 	1:总wifi设备数
 * 	2:总移动设备数
 * 	3:在线wifi设备数
 * 	4:在线移动设备数
 * 	5:总移动设备接入次数 (当前数量+昨日数量) 
 * 	6:总移动设备访问时长 (当前数量+昨日数量)
 * b:昨日daily的统计数据其中实时计算的数据正式计算存入redis
 * 	1:设备接入次数平均（3/(1+2)）(实时计算)
 * 	2:设备活跃率（1+2）/总设备 (实时计算)
 * 	3:设备接入时长平均（4/(1+2)）(实时计算)
 * 	4:新设备占比（1/(1+2)）(实时计算)
 * @author tangzichao
 */
public class StatisticsLoader {
	private static Logger logger = LoggerFactory.getLogger(StatisticsLoader.class);
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@SuppressWarnings("unchecked")
	public void execute() {
		
		try{
			logger.info("StatisticsLoader starting...");
			
			/******************              a:系统统计数据计算           ********************/
			
			//昨日的daily数据中的移动设备接入次数和移动设备访问时长，与总移动设备接入次数和总移动设备访问时长分别求合
			String yesterday_format = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), DateTimeHelper.FormatPattern5);
			DailyStatisticsDTO dailyStatisticsYesterdayDto = DailyStatisticsHashService.getInstance().getStatistics(BusinessKeyDefine.Statistics.
					DailyStatisticsHandsetInnerPrefixKey, yesterday_format);
			
			if(dailyStatisticsYesterdayDto != null){
				/**
				 * 	1:总wifi设备数
				 * 	2:总移动设备数
				 * 	3:在线wifi设备数
				 * 	4:在线移动设备数
				 */
				SystemStatisticsDTO systemStatisticsDto = deviceFacadeService.buildSystemStatisticsDto();
				/**
				 * 5:总移动设备接入次数 (当前数量+昨日数量) 
				 * 6:总移动设备访问时长 (当前数量+昨日数量)
				 */
				systemStatisticsDto = deviceFacadeService.systemStatisticsArith(systemStatisticsDto, dailyStatisticsYesterdayDto);

				SystemStatisticsHashService.getInstance().initOrReset2Statistics(systemStatisticsDto);
			}
			
			/******************    b:昨日daily的统计数据其中实时计算的数据正式计算存入redis    ********************/
			
			/**
			 * 	1:设备接入次数平均（3/(1+2)）
			 * 	2:设备活跃率（1+2）/总设备 
			 * 	3:设备接入时长平均（4/(1+2)）
			 * 	4:新设备占比（1/(1+2)）
			 */
			if(dailyStatisticsYesterdayDto != null){
				dailyStatisticsYesterdayDto = deviceFacadeService.dailyStatisticsArith(dailyStatisticsYesterdayDto);
				DailyStatisticsHashService.getInstance().initOrReset2Statistics(BusinessKeyDefine.Statistics.
						DailyStatisticsHandsetInnerPrefixKey, yesterday_format, BeanUtils.describe(dailyStatisticsYesterdayDto));
			}
			
			logger.info("StatisticsLoader ended");
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("StatisticsLoader throw exception", ex);
		}
	}
	
}
