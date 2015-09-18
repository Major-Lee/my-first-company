package com.bhu.vas.api.rpc.agent.vto;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class SettlementPageVTO implements java.io.Serializable{
	private SettlementStatisticsVTO statistics;
	private TailPage<SettlementVTO> pages;
	public SettlementStatisticsVTO getStatistics() {
		return statistics;
	}
	public void setStatistics(SettlementStatisticsVTO statistics) {
		this.statistics = statistics;
	}
	public TailPage<SettlementVTO> getPages() {
		return pages;
	}
	public void setPages(TailPage<SettlementVTO> pages) {
		this.pages = pages;
	}
	
}
