package com.bhu.vas.api.rpc.task.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * 下行wifi设备的任务  
 */
@SuppressWarnings("serial")
public class WifiDeviceDownTask extends BaseStringModel{
	
	private String payload;
	private String state;
	private String task;
	private String mac;
	private String time;
	private String serial;
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}