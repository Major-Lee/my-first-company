package com.bhu.vas.api.rpc.task.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/*
 * 下行wifi设备的任务  
 */
@SuppressWarnings("serial")
public class WifiDeviceDownTask extends BaseIntModel{
	public static final int State_Pending = 1;//待处理状态
	public static final int State_Timeout = 2;//任务超时
	public static final int State_Failed = 3;//任务失败
	public static final int State_Completed = 10;//任务已经完成
	
	public static final String Task_LOCAL_CHANNEL = "VAS";
	private String payload;
	private int state = State_Pending;
	private String task;
	private String mac;
	private String time;
	private String serial;
	//给外部其他应用提供的channel值，每个外部应用有个编号
	private String channel = Task_LOCAL_CHANNEL;
	private int channel_taskid;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getChannel_taskid() {
		return channel_taskid;
	}

	public void setChannel_taskid(int channel_taskid) {
		this.channel_taskid = channel_taskid;
	}
	
	
}