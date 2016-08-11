package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class RewardIncomeStatisticsVTO implements java.io.Serializable{
	//收益统计结果数
	private Long count;
	//收益统计金额总和
	private Double cashSum;
	
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
