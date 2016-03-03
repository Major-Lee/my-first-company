package com.bhu.vas.api.dto.commdity;


/**
 * 针对应用请求生成订单的处理返回DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderCreatedRetDTO implements java.io.Serializable{
	//订单id
	private String id;
	//订单金额
	private String amount;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}

