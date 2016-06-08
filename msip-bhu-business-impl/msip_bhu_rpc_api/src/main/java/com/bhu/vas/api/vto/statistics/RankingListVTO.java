package com.bhu.vas.api.vto.statistics;

import java.util.List;

import com.bhu.vas.api.rpc.user.model.User;

public class RankingListVTO {
	//收益前一百用户排名
	private List<User> rankingList;
	//用户个人排名
	private int rankNum;
	//用户昨日收益
	private String userIncome;
	public List<User> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<User> rankingList) {
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
}
