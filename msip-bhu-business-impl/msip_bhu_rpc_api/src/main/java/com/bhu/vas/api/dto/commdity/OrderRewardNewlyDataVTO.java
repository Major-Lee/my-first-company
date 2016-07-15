package com.bhu.vas.api.dto.commdity;
/**
 * 最近订单统计dto
 * 1:新增订单数量
 * 2:新增收益总额
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderRewardNewlyDataVTO implements java.io.Serializable{
	//新增订单数量
	private String newly_count = "0";
	//新增收益总额
	private String newly_amount_count = "0.00";
	
	public String getNewly_count() {
		return newly_count;
	}
	public void setNewly_count(String newly_count) {
		this.newly_count = newly_count;
	}
	public String getNewly_amount_count() {
		return newly_amount_count;
	}
	public void setNewly_amount_count(String newly_amount_count) {
		this.newly_amount_count = newly_amount_count;
	}
}
