package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class WifiDeviceCurrentStatus extends BaseStringModel{
	
	private String name;
	//开机时间
	private Date started_at;
	//最近心跳时间
	private Date heartbeated_at;
	//开机时长 heartbeated_at-started_at
	//private long operation_time;
	//内存负载
	private String memory_load;
	
	//本次开机上行流量
	private String upflow;
	//本次开机下行流量
	private String downflow;
	
	//本次开机上行速率
	private String upspeed;
	//本次开机下行速率
	private String downspeed;
	//本次开机到目前为止的在线人数
	private int onlinenumbers;
	private Date created_at;
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStarted_at() {
		return started_at;
	}
	public void setStarted_at(Date started_at) {
		this.started_at = started_at;
	}
	public Date getHeartbeated_at() {
		return heartbeated_at;
	}
	public void setHeartbeated_at(Date heartbeated_at) {
		this.heartbeated_at = heartbeated_at;
	}
	public String getMemory_load() {
		return memory_load;
	}
	public void setMemory_load(String memory_load) {
		this.memory_load = memory_load;
	}
	public String getUpflow() {
		return upflow;
	}
	public void setUpflow(String upflow) {
		this.upflow = upflow;
	}
	public String getDownflow() {
		return downflow;
	}
	public void setDownflow(String downflow) {
		this.downflow = downflow;
	}
	public String getUpspeed() {
		return upspeed;
	}
	public void setUpspeed(String upspeed) {
		this.upspeed = upspeed;
	}
	public String getDownspeed() {
		return downspeed;
	}
	public void setDownspeed(String downspeed) {
		this.downspeed = downspeed;
	}
	public int getOnlinenumbers() {
		return onlinenumbers;
	}
	public void setOnlinenumbers(int onlinenumbers) {
		this.onlinenumbers = onlinenumbers;
	}
}