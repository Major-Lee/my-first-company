package com.bhu.vas.api.dto.commdity;
/**
 * 用于展示用户的订单记录实体VTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderVTO implements java.io.Serializable{
	//订单id
	private String id;
	//商品id
	private Integer commdityid;
	//应用id
	private Integer appid;
	//订单类型
	private Integer type;
	//user agent sources count
	private String user_agent;
	//业务上下文
	private String context;
	//订单状态
	private Integer status;
	//订单创建时间
	private long created_ts;
	//订单支付时间
	private long paymented_ts;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getCommdityid() {
		return commdityid;
	}
	public void setCommdityid(Integer commdityid) {
		this.commdityid = commdityid;
	}
	public Integer getAppid() {
		return appid;
	}
	public void setAppid(Integer appid) {
		this.appid = appid;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getUser_agent() {
		return user_agent;
	}
	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public long getCreated_ts() {
		return created_ts;
	}
	public void setCreated_ts(long created_ts) {
		this.created_ts = created_ts;
	}
	public long getPaymented_ts() {
		return paymented_ts;
	}
	public void setPaymented_ts(long paymented_ts) {
		this.paymented_ts = paymented_ts;
	}
	
}
