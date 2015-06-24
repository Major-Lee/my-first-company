package com.bhu.vas.api.dto.redis;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.dto.redis.element.DailyUsedStatisticsDTO;
import com.bhu.vas.api.dto.redis.element.HourUsedStatisticsDTO;

/**
 * uRouter设备的使用情况的统计
 * 包括昨日和今日的每小时的统计信息详情 以及 昨日和今日的 整日的统计详情
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceUsedStatisticsDTO implements Serializable{
	private List<HourUsedStatisticsDTO> today_detail;
	private DailyUsedStatisticsDTO today;
	private List<HourUsedStatisticsDTO> yesterday_detail;
	private DailyUsedStatisticsDTO yesterday;
	//最后获取数据的时间 yyyy-MM-dd hh:mm:ss
	private long ts  = System.currentTimeMillis();
	private String ko;
	private String score;
	public List<HourUsedStatisticsDTO> getToday_detail() {
		return today_detail;
	}
	public void setToday_detail(List<HourUsedStatisticsDTO> today_detail) {
		this.today_detail = today_detail;
	}
	public DailyUsedStatisticsDTO getToday() {
		return today;
	}
	public void setToday(DailyUsedStatisticsDTO today) {
		this.today = today;
	}
	public List<HourUsedStatisticsDTO> getYesterday_detail() {
		return yesterday_detail;
	}
	public void setYesterday_detail(List<HourUsedStatisticsDTO> yesterday_detail) {
		this.yesterday_detail = yesterday_detail;
	}
	public DailyUsedStatisticsDTO getYesterday() {
		return yesterday;
	}
	public void setYesterday(DailyUsedStatisticsDTO yesterday) {
		this.yesterday = yesterday;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getKo() {
		return ko;
	}
	public void setKo(String ko) {
		this.ko = ko;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
	
}
