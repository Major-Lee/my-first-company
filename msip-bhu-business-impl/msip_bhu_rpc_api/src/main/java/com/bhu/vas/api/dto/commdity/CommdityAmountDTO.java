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
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}

