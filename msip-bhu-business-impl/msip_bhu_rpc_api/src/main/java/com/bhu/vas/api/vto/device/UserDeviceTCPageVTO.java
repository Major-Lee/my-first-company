package com.bhu.vas.api.vto.device;

import java.io.Serializable;

import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 1/25/16.
 *
 * 参照 com.bhu.vas.business.search.model.WifiDeviceDocument注释
 */
@SuppressWarnings("serial")
public class UserDeviceTCPageVTO implements Serializable {
	private UserDeviceStatisticsVTO statistics;
	private TailPage<UserDeviceVTO> pages;
	
	public UserDeviceStatisticsVTO getStatistics() {
		return statistics;
	}
	public void setStatistics(UserDeviceStatisticsVTO statistics) {
		this.statistics = statistics;
	}
	public TailPage<UserDeviceVTO> getPages() {
		return pages;
	}
	public void setPages(TailPage<UserDeviceVTO> pages) {
		this.pages = pages;
	}
}
