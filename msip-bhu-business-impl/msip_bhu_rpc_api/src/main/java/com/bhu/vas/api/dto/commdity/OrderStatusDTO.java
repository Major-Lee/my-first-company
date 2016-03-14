package com.bhu.vas.api.dto.commdity;


/**
 * 订单状态DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderStatusDTO implements java.io.Serializable{
	//订单id
	private String id;
	//订单状态
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

