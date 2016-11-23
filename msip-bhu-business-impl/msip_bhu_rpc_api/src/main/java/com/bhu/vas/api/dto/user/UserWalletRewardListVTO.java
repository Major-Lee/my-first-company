package com.bhu.vas.api.dto.user;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class UserWalletRewardListVTO implements java.io.Serializable{
	private TailPage<UserWalletRewardVTO> tailPage;
	private String totalDealCash;
	private String totalCash;
	public TailPage<UserWalletRewardVTO> getTailPage() {
		return tailPage;
	}
	public void setTailPage(TailPage<UserWalletRewardVTO> tailPage) {
		this.tailPage = tailPage;
	}
	public String getTotalDealCash() {
		return totalDealCash;
	}
	public void setTotalDealCash(String totalDealCash) {
		this.totalDealCash = totalDealCash;
	}
	public String getTotalCash() {
		return totalCash;
	}
	public void setTotalCash(String totalCash) {
		this.totalCash = totalCash;
	}
	
}
