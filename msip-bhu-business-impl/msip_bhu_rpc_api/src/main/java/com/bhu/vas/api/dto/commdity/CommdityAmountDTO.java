package com.bhu.vas.api.dto.commdity;


/**
 * 针对应用请求生成商品的金额DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class CommdityAmountDTO implements java.io.Serializable{
	//随机金额
	private String amount;
	//设备的上网时间 add by fengshibo
	private String forceTime;
	
	public String getForceTime() {
		return forceTime;
	}
	public void setForceTime(String forceTime) {
		this.forceTime = forceTime;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}

