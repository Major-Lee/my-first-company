package com.bhu.vas.api.dto.commdity;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class RewardQueryPagesDetailVTO implements java.io.Serializable{
	//订单个数
	private Long count;
	//订单总金额
	private Double cashSum;
	
	private TailPage<OrderRewardVTO> detail;
	
	public TailPage<OrderRewardVTO> getTailPages() {
		return detail;
	}
	public void setTailPages(TailPage<OrderRewardVTO> detail) {
		this.detail = detail;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Double getCashSum() {
		return cashSum;
	}
	public void setCashSum(Double cashSum) {
		this.cashSum = cashSum;
	}
	
}
