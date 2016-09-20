package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class OrderSMSPromotionDTO implements java.io.Serializable{
	//订单id
	private String id;
	//用户id
	private int uid;
	//花费虎钻
	private long vcurrency_cost;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public long getVcurrency_cost() {
		return vcurrency_cost;
	}
	public void setVcurrency_cost(long vcurrency_cost) {
		this.vcurrency_cost = vcurrency_cost;
	}
	
	public String toString(){
		return String.format("Orderid[%s],Uid[%s],SpendVcurrency[%s]", 
				this.getId(),this.getUid(),this.getVcurrency_cost());
	}
	
}
