package com.bhu.vas.api.dto.commdity;
/**
 * 用于展示用户的订单记录实体VTO
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class OrderVideoVTO implements java.io.Serializable {
	//订单id
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
