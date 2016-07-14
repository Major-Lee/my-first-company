package com.bhu.vas.api.dto.commdity.internal.useragent;

import java.util.Date;

/**
 * 订单创建成功
 * @author tangzichao
 *
 */
public class OrderUserAgentDTO {
	//订单id
	private String orderid;
	//用户mac
	private String umac;
	//用户终端类型
	private Integer umactype;
	//用户终端的用户id(充值虎钻)
	private Integer umac_uid;
	//用户终端的手机号(短信认证, 充值虎钻)
	private String umac_mobileno;
	//订单类型
	private Integer type;
	//设备mac
	private String mac;
	//设备的用户id
	private Integer uid;
	//user agent sources content
	private String user_agent;
	//订单创建时间
	private Date created_at;

    public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getUmac() {
		return umac;
	}

	public void setUmac(String umac) {
		this.umac = umac;
	}

	public Integer getUmactype() {
		return umactype;
	}

	public void setUmactype(Integer umactype) {
		this.umactype = umactype;
	}

	public Integer getUmac_uid() {
		return umac_uid;
	}

	public void setUmac_uid(Integer umac_uid) {
		this.umac_uid = umac_uid;
	}

	public String getUmac_mobileno() {
		return umac_mobileno;
	}

	public void setUmac_mobileno(String umac_mobileno) {
		this.umac_mobileno = umac_mobileno;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	/*
	 * 	//订单id
	private String orderid;
	//用户mac
	private String umac;
	//用户终端类型
	private Integer umactype;
	//用户终端的用户id(充值虎钻)
	private Integer umac_uid;
	//用户终端的手机号(短信认证, 充值虎钻)
	private String umac_mobileno;
	//订单类型
	private Integer type;
	//设备mac
	private String mac;
	//设备的用户id
	private Integer uid;
	//user agent sources content
	private String user_agent;
	//订单创建时间
	private Date created_at;
	 * */
	public static OrderUserAgentDTO builder(String orderid, String umac, Integer umactype, 
			Integer umac_uid, String umac_mobileno, Integer type, String mac, Integer uid, String user_agent,
			Date created_at){
		OrderUserAgentDTO userAgentDto = new OrderUserAgentDTO();
		userAgentDto.setOrderid(orderid);
		userAgentDto.setUmac(umac_mobileno);
		userAgentDto.setUmactype(umactype);
		userAgentDto.setUmac_uid(umac_uid);
		userAgentDto.setUmac_mobileno(umac_mobileno);
		userAgentDto.setType(type);
		userAgentDto.setUid(uid);
		userAgentDto.setUser_agent(user_agent);
		userAgentDto.setCreated_at(created_at);
		return userAgentDto;
	}
}
