package com.bhu.vas.api.vto.wallet;

import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletDetailPagesVTO implements java.io.Serializable {
	private int uid;
	private long vcurrency_total = 0l;
	private String cash;
	//walletlog总数
	private int logs_count;
	//withdraw successful sum成功提现总和
	private String withdraw_success_sum;
	private TailPage<UserWalletLogVTO> pages;
	public TailPage<UserWalletLogVTO> getPages() {
		return pages;
	}
	public void setPages(TailPage<UserWalletLogVTO> pages) {
		this.pages = pages;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public int getLogs_count() {
		return logs_count;
	}
	public void setLogs_count(int logs_count) {
		this.logs_count = logs_count;
	}
	public String getWithdraw_success_sum() {
		return withdraw_success_sum;
	}
	public void setWithdraw_success_sum(String withdraw_success_sum) {
		this.withdraw_success_sum = withdraw_success_sum;
	}
	public long getVcurrency_total() {
		return vcurrency_total;
	}
	public void setVcurrency_total(long vcurrency_total) {
		this.vcurrency_total = vcurrency_total;
	}
	
}
