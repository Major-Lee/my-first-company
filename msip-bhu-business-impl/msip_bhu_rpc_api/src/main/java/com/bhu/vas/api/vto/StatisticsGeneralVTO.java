package com.bhu.vas.api.vto;

import java.io.Serializable;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
/**
 * 用于展现统计的通用数据vto
	页面中统计数据体现：
	a、总设备数、总用户数、在线设备数、在线用户数、总接入次数、总用户访问时长
	b、今日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
	c、昨日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class StatisticsGeneralVTO implements Serializable{
	private SystemStatisticsDTO system;
	private DailyStatisticsDTO today_daily;
	private DailyStatisticsDTO yesterday_daily;
	
	public SystemStatisticsDTO getSystem() {
		return system;
	}
	public void setSystem(SystemStatisticsDTO system) {
		this.system = system;
	}
	public DailyStatisticsDTO getToday_daily() {
		return today_daily;
	}
	public void setToday_daily(DailyStatisticsDTO today_daily) {
		this.today_daily = today_daily;
	}
	public DailyStatisticsDTO getYesterday_daily() {
		return yesterday_daily;
	}
	public void setYesterday_daily(DailyStatisticsDTO yesterday_daily) {
		this.yesterday_daily = yesterday_daily;
	}
}
