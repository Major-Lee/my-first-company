package com.bhu.vas.api.dto.push;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 用于业务数据的传递的pushDto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public abstract class PushDTO implements Serializable{
//	//用户id
//	private Integer uid;
	//设备mac
	private String mac;
	@JsonIgnore
	private long ts;
//	//透传内容
//	private String payload;
	
	public abstract String getPushType();
	
//	public Integer getUid() {
//		return uid;
//	}
//	public void setUid(Integer uid) {
//		this.uid = uid;
//	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
//	public String getPayload() {
//		return payload;
//	}
//	public void setPayload(String payload) {
//		this.payload = payload;
//	}
}
