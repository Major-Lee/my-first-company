package com.bhu.vas.api.rpc.task.model;

import java.util.Date;
/*
 * 下行wifi设备的并且wifi设备返回完成确认的任务  
 */
@SuppressWarnings("serial")
public class WifiDeviceCompletedDownTask extends WifiDeviceDownTask{
	
	//任务的完成时间
	private Date completed_at;
	
	@Override
	public void preInsert() {
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public Date getCompleted_at() {
		return completed_at;
	}

	public void setCompleted_at(Date completed_at) {
		this.completed_at = completed_at;
	}

}