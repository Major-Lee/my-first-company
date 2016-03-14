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
	private Integer status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}

