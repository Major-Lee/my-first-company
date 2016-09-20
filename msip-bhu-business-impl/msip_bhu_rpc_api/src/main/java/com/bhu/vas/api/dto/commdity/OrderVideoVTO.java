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
	//设备的上网时间 add by fengshibo
	private String forceTime;
	//本周用户数
	private String user7d;
	public String getUser7d() {
		return user7d;
	}
	public void setUser7d(String user7d) {
		this.user7d = user7d;
	}
	public String getForceTime() {
		return forceTime;
	}
	public void setForceTime(String forceTime) {
		this.forceTime = forceTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
