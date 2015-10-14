package com.bhu.vas.api.dto.redis.element;

@SuppressWarnings("serial")
public class DailyUsedStatisticsDTO extends HourUsedStatisticsDTO{
	// time="all" tx_bytes="62816" rx_bytes="5654" sta="100" sta_max_time="458" sta_max_time_num ="5"
	private String sta_max_time;
	private String sta_max_time_num;
	private String flow_max_time;
	private String flow_max_time_num;
	private int score;
	
	private String ko;
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
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getKo() {
		return ko;
	}
	public void setKo(String ko) {
		this.ko = ko;
	}
	public String getFlow_max_time() {
		return flow_max_time;
	}
	public void setFlow_max_time(String flow_max_time) {
		this.flow_max_time = flow_max_time;
	}
	public String getFlow_max_time_num() {
		return flow_max_time_num;
	}
	public void setFlow_max_time_num(String flow_max_time_num) {
		this.flow_max_time_num = flow_max_time_num;
	}
	
}
