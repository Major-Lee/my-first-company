package com.bhu.vas.api.dto.commdity;


/**
 * 针对应用请求生成订单url处理返回DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderPaymentUrlDTO implements java.io.Serializable{
	//订单id
	private String id;
	//第三方支付url信息
	private String third_payinfo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getThird_payinfo() {
		return third_payinfo;
	}
	public void setThird_payinfo(String third_payinfo) {
		this.third_payinfo = third_payinfo;
	}
}

