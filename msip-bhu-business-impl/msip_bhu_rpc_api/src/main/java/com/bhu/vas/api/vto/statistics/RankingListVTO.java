package com.bhu.vas.api.vto.statistics;

import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class RankingListVTO implements java.io.Serializable{
	//收益前一百用户排名
	private Map<String,Object> rankingList;
	//用户个人排名
	private int rankNum;
	//用户昨日收益
	private String userIncome;
	//名次变化标识 0：上升   1：持平  2：下降
	private int changeFlag;
	//用户签名
	private String memo;
	private String avatar;
	
	private int pn;
	private int ps;
	private int totalPage;
	
	public int getPn() {
		return pn;
	}

	public void setPn(int pn) {
		this.pn = pn;
	}

	public int getPs() {
		return ps;
	}

	public void setPs(int ps) {
		this.ps = ps;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}


	

	public Map<String, Object> getRankingList() {
		return rankingList;
	}

	public void setRankingList(Map<String, Object> rankingList) {
		this.rankingList = rankingList;
	}

	public int getRankNum() {
		return rankNum;
	}

	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}

	public String getUserIncome() {
		return userIncome;
	}

	public void setUserIncome(String userIncome) {
		this.userIncome = userIncome;
	}

	public int getChangeFlag() {
		return changeFlag;
	}

	public void setChangeFlag(int changeFlag) {
		this.changeFlag = changeFlag;
	}
	
}
