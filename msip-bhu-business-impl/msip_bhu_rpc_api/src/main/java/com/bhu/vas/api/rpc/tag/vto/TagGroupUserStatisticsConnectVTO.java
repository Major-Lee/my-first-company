package com.bhu.vas.api.rpc.tag.vto;

import java.util.List;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class TagGroupUserStatisticsConnectVTO implements java.io.Serializable {
	TailPage<TagGroupRankUsersVTO> rankList;
	List<TagGroupUserConnectDataVTO> userConnectData;
	public TailPage<TagGroupRankUsersVTO> getRankList() {
		return rankList;
	}
	public List<TagGroupUserConnectDataVTO> getUserConnectData() {
		return userConnectData;
	}
	public void setUserConnectData(List<TagGroupUserConnectDataVTO> userConnectData) {
		this.userConnectData = userConnectData;
	}
	public void setRankList(TailPage<TagGroupRankUsersVTO> rankList) {
		this.rankList = rankList;
	}
}
