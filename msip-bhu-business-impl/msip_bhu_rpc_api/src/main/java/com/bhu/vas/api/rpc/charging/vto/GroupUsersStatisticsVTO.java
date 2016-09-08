package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class GroupUsersStatisticsVTO implements java.io.Serializable{
	//今日新增用户总数
	private String today_newly;
	//今日连接用户总数
	private String today_total;
	//昨日连接用户新增
	private String yesterday_newly;
	//昨日连接用户总数
	private String yesterday_total;
	//累计用户数
	private String count;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getToday_newly() {
		return today_newly;
	}
	public void setToday_newly(String today_newly) {
		this.today_newly = today_newly;
	}
	public String getToday_total() {
		return today_total;
	}
	public void setToday_total(String today_total) {
		this.today_total = today_total;
	}
	public String getYesterday_newly() {
		return yesterday_newly;
	}
	public void setYesterday_newly(String yesterday_newly) {
		this.yesterday_newly = yesterday_newly;
	}
	public String getYesterday_total() {
		return yesterday_total;
	}
	public void setYesterday_total(String yesterday_total) {
		this.yesterday_total = yesterday_total;
	}
}
