package com.bhu.vas.api.dto.commdity;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class RewardQueryPagesDetailVTO implements java.io.Serializable{
	//订单个数
	private String count;
	//订单总金额
	private String cashSum;
	
	private TailPage<OrderRewardVTO> tailPages;
	
	public TailPage<OrderRewardVTO> getTailPages() {
		return tailPages;
	}
	public void setTailPages(TailPage<OrderRewardVTO> tailPages) {
		this.tailPages = tailPages;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getCashSum() {
		return cashSum;
	}
	public void setCashSum(String cashSum) {
		this.cashSum = cashSum;
	}
	
}
