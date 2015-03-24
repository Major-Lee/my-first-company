package com.bhu.vas.api.rpc.task.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
/*
 * 下行wifi设备的并且wifi设备返回完成确认的任务  
 */
@SuppressWarnings("serial")
public class WifiDeviceDownTaskCompleted extends WifiDeviceDownTask{
	
	//任务返回值
	private String response;
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

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	
	public static WifiDeviceDownTaskCompleted fromWifiDeviceDownTask(
			WifiDeviceDownTask downTask,String state,String response){
		WifiDeviceDownTaskCompleted downCompleted = new WifiDeviceDownTaskCompleted();
		try {
			BeanUtils.copyProperties(downCompleted, downTask);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace(System.out);
		}
		downCompleted.setState(state);
		downCompleted.setResponse(response);
		downCompleted.setCompleted_at(new Date());
		return downCompleted;
	}
}