package com.bhu.vas.api.rpc.task.dto;

@SuppressWarnings("serial")
public class TaskResDTO implements java.io.Serializable{
	private int taskid;
	private String mac;
	private String state;
	private String channel;
	private String channel_taskid;
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getChannel_taskid() {
		return channel_taskid;
	}
	public void setChannel_taskid(String channel_taskid) {
		this.channel_taskid = channel_taskid;
	}
	
}
