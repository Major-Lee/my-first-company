package com.bhu.vas.business.ds.task.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;

@Service
public class TaskFacadeService {
	@Resource
	private WifiDeviceDownTaskService wifiDeviceDownTaskService;
	
	@Resource
	private WifiDeviceDownTaskCompletedService wifiDeviceDownTaskCompletedService;
	
	
	
	/**
	 * 任务执行callback通知
	 * @param taskid
	 * @param status
	 */
	public void taskExecuteCallback(int taskid,int status){
		
	}
	
	public void newTaskComming(int taskid){
		
	}
	
	public void cancelTask(int taskid){
		
	}
}
