package com.bhu.vas.api.vto.statistics;

public class RankSingle {
	private String userName;
	//用户昨日收益
	private String userIncome;
	private int rankNum;
	//头像
	private String avatar;
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserIncome() {
		return userIncome;
	}
	public void setUserIncome(String userIncome) {
		this.userIncome = userIncome;
	}
	public int getRankNum() {
		return rankNum;
	}
	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}
	
}
