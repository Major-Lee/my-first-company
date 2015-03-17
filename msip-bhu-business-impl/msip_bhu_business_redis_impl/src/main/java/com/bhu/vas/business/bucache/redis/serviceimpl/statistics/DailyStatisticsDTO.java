package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

public class DailyStatisticsDTO {
	
	public static final String Field_News = "news";
	public static final String Field_Actives = "actives";
	public static final String Field_Startups = "startups";
	public static final String Field_Times = "times";
	private long news;
	private long actives;
	private long startups;
	private long times;
	
	private long total;
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
	public long getStartups() {
		return startups;
	}
	public void setStartups(long startups) {
		this.startups = startups;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	
}
