package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class RewardIncomeStatisticsVTO implements java.io.Serializable{
	//收益统计结果数
	private String count;
	//收益统计金额总和
	private String cashSum;
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
