package com.bhu.vas.api.vto.statistics;

@SuppressWarnings("serial")
public class RankSingle implements java.io.Serializable{
	private String userName;
	//用户昨日收益
	private String userIncome;
	private int rankNum;
	//头像
	private String avatar;
	//名次变化标识 0：上升   1：持平  2：下降
	private int changeFlag; 
	//用户签名
	private String memo;
	
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
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
	public int getChangeFlag() {
		return changeFlag;
	}
	public void setChangeFlag(int changeFlag) {
		this.changeFlag = changeFlag;
	}
	
}
