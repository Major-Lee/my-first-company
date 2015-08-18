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
	/*private String ko;
	private String score;*/
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
	
	public void analyseMaxFlow(){
		if(today_detail!=null && !today_detail.isEmpty()){
			HourUsedStatisticsDTO dto = fetchMaxFlowStatisticsDTO(today_detail);
			if(dto != null){
				if(this.getToday() != null){
					this.getToday().setFlow_max_time_num(dto.getRx_bytes());
					this.getToday().setFlow_max_time(String.valueOf(Integer.parseInt(dto.getTime())*3600));
				}
			}
		}
		if(yesterday_detail!=null && !yesterday_detail.isEmpty()){
			HourUsedStatisticsDTO dto = fetchMaxFlowStatisticsDTO(yesterday_detail);
			if(dto != null){
				if(this.getYesterday() != null){
					this.getYesterday().setFlow_max_time_num(dto.getRx_bytes());
					this.getYesterday().setFlow_max_time(String.valueOf(Integer.parseInt(dto.getTime())*3600));
				}
			}
		}
		
	}
	
	private static HourUsedStatisticsDTO fetchMaxFlowStatisticsDTO(List<HourUsedStatisticsDTO> details){
		long currentMaxFlow = 0l;
		HourUsedStatisticsDTO result_dto = null;
		for(HourUsedStatisticsDTO dto:details){
			long now = Long.parseLong(dto.getRx_bytes());
			if(now > currentMaxFlow){
				currentMaxFlow = now;
				result_dto = dto;
			}
		}
		return result_dto;
	}
}
