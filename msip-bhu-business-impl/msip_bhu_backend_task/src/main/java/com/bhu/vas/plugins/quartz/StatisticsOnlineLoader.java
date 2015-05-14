package com.bhu.vas.plugins.quartz;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineDeviceService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 每小时执行的定时程序
 * 根据用户t_handset_devices 表统计当前在线的用户数量 count
 * 1、写入 redis 日 ：		key (yyyy-MM-dd) 	field（HH）value (count)
 * 2、获取当前日期的自然周 	key（yyyy-w） 		field(yyyy-MM-dd) value (count1) 如果count>count1 则覆盖更新，存储最高值
 * 3、获取当前日期的自然月 	key（yyyy-MM） 		field(yyyy-MM-dd) value (count1) 如果count>count1 则覆盖更新，存储最高值
 * 4、获取当前日期的自然季度 key（yyyy-MM） 		field(yyyy-MM-dd) value (count1) 如果count>count1 则覆盖更新，存储最高值
 * 5、获取当前自然年 		key（yyyy） 			field(yyyy-MM) value (count1) 如果count>count1 则覆盖更新，存储最高值
 * @author Edmond
 * handset & device Online
 */
public class StatisticsOnlineLoader {
	private static Logger logger = LoggerFactory.getLogger(StatisticsOnlineLoader.class);
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;

	
	public void execute() {
		logger.info("StatisticsOnlineUserLoader starting...");
		Date current = new Date();
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", 1);
		int countHandset = handsetDeviceService.countByModelCriteria(mc);
		StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentAllSet(current, countHandset);
		int countDevice = wifiDeviceService.countByModelCriteria(mc);
		StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentAllSet(current, countDevice);
		logger.info(String.format("StatisticsOnlineUserLoader ended, total handsetOnline[%s] deviceOnline[%s]!", countHandset,countDevice));
		
	}
	//TODO：TBD是否需要补齐？
	//补齐数据，补齐指定日期内（包括前一日、后一日）三天的数据
	public void polishingData(Date current){
		/*String fragment = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
		Map<String,String> result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
		int hopeSize = DateTimeExtHelper.getDayOfMonth(current);
		if(hopeSize == result.size())	return;*/
		
		
		
		/*Calendar c = Calendar.getInstance();
		c.setTime(current);
		int current_year = c.get(Calendar.YEAR);
		int current_month = c.get(Calendar.MONTH);
		int current_day = c.get(Calendar.DAY_OF_MONTH);
		System.out.println(String.format("%s %s %s", current_year,current_month,current_day));
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-1);
		current_year = c.get(Calendar.YEAR);
		current_month = c.get(Calendar.MONTH);
		current_day = c.get(Calendar.DAY_OF_MONTH);
		System.out.println(String.format("%s %s %s", current_year,current_month,current_day));
		
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+2);
		
		current_year = c.get(Calendar.YEAR);
		current_month = c.get(Calendar.MONTH);
		current_day = c.get(Calendar.DAY_OF_MONTH);
		System.out.println(String.format("%s %s %s", current_year,current_month,current_day));*/
	}
	
	/*public static void main(String[] argv){
		new StatisticsOnlineUserLoader().polishingData(DateTimeHelper.getDateDaysAgo(24));
	}*/
}
