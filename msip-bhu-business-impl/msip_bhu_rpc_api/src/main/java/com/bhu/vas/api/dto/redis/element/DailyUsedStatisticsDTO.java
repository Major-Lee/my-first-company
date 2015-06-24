package com.bhu.vas.api.dto.redis.element;

public class DailyUsedStatisticsDTO extends HourUsedStatisticsDTO{
	// time="all" tx_bytes="62816" rx_bytes="5654" sta="100" sta_max_time="458" sta_max_time_num ="5"
	private String sta_max_time;
	private String sta_max_time_num;
	public String getSta_max_time() {
		return sta_max_time;
	}
	public void setSta_max_time(String sta_max_time) {
		this.sta_max_time = sta_max_time;
	}
	public String getSta_max_time_num() {
		return sta_max_time_num;
	}
	public void setSta_max_time_num(String sta_max_time_num) {
		this.sta_max_time_num = sta_max_time_num;
	}
}
