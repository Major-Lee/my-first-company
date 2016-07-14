package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class UcloudMacStatisticsVTO implements Serializable{
	//收益设备信息
	private List<UcloudMacStatistic> macsInfo;
	private int pageIndex;
	private int pageSize;
	private int pageNum;
	private String todayIncome;
	private String todayUserNum;
	private String yesterdayUserNum;
	private String yesterdayIncome;
	private String totalIncome;
	public List<UcloudMacStatistic> getMacsInfo() {
		return macsInfo;
	}
	public void setMacsInfo(List<UcloudMacStatistic> macsInfo) {
		this.macsInfo = macsInfo;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public String getTodayIncome() {
		return todayIncome;
	}
	public void setTodayIncome(String todayIncome) {
		this.todayIncome = todayIncome;
	}
	public String getTodayUserNum() {
		return todayUserNum;
	}
	public void setTodayUserNum(String todayUserNum) {
		this.todayUserNum = todayUserNum;
	}
	public String getYesterdayUserNum() {
		return yesterdayUserNum;
	}
	public void setYesterdayUserNum(String yesterdayUserNum) {
		this.yesterdayUserNum = yesterdayUserNum;
	}
	public String getYesterdayIncome() {
		return yesterdayIncome;
	}
	public void setYesterdayIncome(String yesterdayIncome) {
		this.yesterdayIncome = yesterdayIncome;
	}
	public String getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(String totalIncome) {
		this.totalIncome = totalIncome;
	}
	
}
