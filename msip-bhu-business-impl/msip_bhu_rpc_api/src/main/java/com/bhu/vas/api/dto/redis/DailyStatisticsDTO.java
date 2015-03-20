package com.bhu.vas.api.dto.redis;

import java.io.Serializable;

/**
 * 用于显示daily的统计数据
 * 1:新增设备
 * 2:活跃设备
 * 3:设备接入次数
 * 4:设备接入时长
 * 5:设备接入次数平均（3/(1+2)）(实时计算)
 * 6:设备活跃率（1+2）/总设备 (实时计算)
 * 7:设备接入时长平均（4/(1+2)）(实时计算)
 * 8:新设备占比（1/(1+2)）(实时计算)
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class DailyStatisticsDTO implements Serializable{
	
	public static final String Field_News = "news";//1:新增设备
	public static final String Field_Actives = "actives";//2:活跃设备
	public static final String Field_AccessCount = "accesscount";//3:设备接入次数
	public static final String Field_Duration = "duration";//4:设备接入时长
	
	public static final String Field_AccessCount_Average = "accesscount_avg";//5:设备接入次数平均
	public static final String Field_Active_Percent = "active_pet";//6:设备活跃率
	public static final String Field_Duration_Average = "duration_avg";//4:设备接入时长平均
	public static final String Field_News_Percent = "news_pet";//4:新设备占比
	
	private long news;
	private long actives;
	private long accesscount;
	private long duration;
	
	private String accesscount_avg = "0";
	private String active_pet = "0%";
	private String duration_avg = "0";
	private String news_pet = "0%";
	
	public long getNews() {
		return news;
	}
	public void setNews(long news) {
		this.news = news;
	}
	public long getActives() {
		return actives;
	}
	public void setActives(long actives) {
		this.actives = actives;
	}
	public long getAccesscount() {
		return accesscount;
	}
	public void setAccesscount(long accesscount) {
		this.accesscount = accesscount;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getAccesscount_avg() {
		return accesscount_avg;
	}
	public void setAccesscount_avg(String accesscount_avg) {
		this.accesscount_avg = accesscount_avg;
	}
	public String getActive_pet() {
		return active_pet;
	}
	public void setActive_pet(String active_pet) {
		this.active_pet = active_pet;
	}
	public String getDuration_avg() {
		return duration_avg;
	}
	public void setDuration_avg(String duration_avg) {
		this.duration_avg = duration_avg;
	}
	public String getNews_pet() {
		return news_pet;
	}
	public void setNews_pet(String news_pet) {
		this.news_pet = news_pet;
	}
	
}
