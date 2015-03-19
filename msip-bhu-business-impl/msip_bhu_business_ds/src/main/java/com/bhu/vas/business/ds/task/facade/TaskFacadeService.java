package com.bhu.vas.business.ds.task.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
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
	
	public static final int Task_Illegal = -1;
	public static final int Task_Already_Exist = 0;
	public static final int Task_Already_Completed = 1;
	public static final int Task_Startup_OK = 2;
	public int taskComming(WifiDeviceDownTask downtask){
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()) ||StringUtils.isEmpty(downtask.getPayload())) return Task_Illegal;
		if(StringUtils.isNotEmpty(downtask.get)){
			
		}
		return 0;
	}
	
	
	public void cancelTask(int taskid){
		
	}
}
