package com.bhu.vas.api.vto.statistics;

import java.util.List;

import com.bhu.vas.api.rpc.user.model.User;

public class RankingListVTO {
	//收益前一百用户排名
	private List<User> rankingList;

	public List<User> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<User> rankingList) {
		this.rankingList = rankingList;
	}
	
}
