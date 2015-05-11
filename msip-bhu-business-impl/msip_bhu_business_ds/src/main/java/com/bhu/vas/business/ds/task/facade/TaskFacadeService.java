package com.bhu.vas.business.ds.task.facade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
	public WifiDeviceDownTaskCompleted taskExecuteCallback(int taskid,String state,String response){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_UNDEFINED);
		}
		if(WifiDeviceDownTask.State_Done.equals(state) || WifiDeviceDownTask.State_Failed.equals(state)){
			WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
			WifiDeviceDownTaskCompleted result = wifiDeviceDownTaskCompletedService.insert(completed);
			wifiDeviceDownTaskService.deleteById(taskid);
			return result;
		}else{
			downtask.setState(state);
			wifiDeviceDownTaskService.update(downtask);
			return null;
		}
	}
	
	public void taskComming(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac())/* || StringUtils.isEmpty(downtask.getPayload())*/) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()))
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_VALIDATE_ILEGAL);
		if(StringUtils.isNotEmpty(downtask.getChannel_taskid()) && StringUtils.isNotEmpty(downtask.getChannel()) ){//外部应用触发任务
			//看看WifiDeviceDownTaskService是否存在此任务
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("channel_taskid", downtask.getChannel_taskid()).andColumnEqualTo("channel", downtask.getChannel());
			int count  = wifiDeviceDownTaskService.countByModelCriteria(mc);
			//if(count > 0) return RpcResponseCodeConst.Task_Already_Exist;
			if(count > 0)
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_EXIST);
			count  = wifiDeviceDownTaskCompletedService.countByModelCriteria(mc);
			//if(count > 0) return RpcResponseCodeConst.Task_Already_Completed;
			if(count > 0) 
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_COMPLETED);
		}
		downtask = wifiDeviceDownTaskService.insert(downtask);
	}
	
	
	public void taskUpdate(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) 
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_VALIDATE_ILEGAL);
		downtask = wifiDeviceDownTaskService.update(downtask);
	}
	
	/**
	 * 查询任务的状态 会对timeout的任务进行标记
	 * 1：已完成任务中查询
	 * 2：pending任务中查询
	 * @param channel
	 * @param channel_taskid
	 * @return
	 */
	public WifiDeviceDownTask queryTask(String channel, String channel_taskid){
		//从已完成任务中获取
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("channel_taskid", channel_taskid).andColumnEqualTo("channel", channel);
		List<WifiDeviceDownTaskCompleted> taskCompleteds = wifiDeviceDownTaskCompletedService.findModelByModelCriteria(mc);
		if(!taskCompleteds.isEmpty()){
			return taskCompleteds.get(0);
		}
		List<WifiDeviceDownTask> taskDowns = wifiDeviceDownTaskService.findModelByModelCriteria(mc);
		if(!taskDowns.isEmpty()){
			//如果获取的是pending任务 判断时间是否timeout
			WifiDeviceDownTask pending_task = taskDowns.get(0);
			validateTaskTimeout(pending_task);
//			WifiDeviceDownTask pending_task = taskDowns.get(0);
//			if((System.currentTimeMillis() - pending_task.getCreated_at().getTime()) 
//					> RuntimeConfiguration.TaskTimeoutMilliSecs){
//				//设置任务状态为timeout 此处不算任务完成
//				pending_task.setState(WifiDeviceDownTask.State_Timeout);
//				pending_task = wifiDeviceDownTaskService.update(pending_task);
//			}
			return pending_task;
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	/**
	 * 查询任务的状态 会对timeout的任务进行标记
	 * 1：已完成任务中查询
	 * 2：pending任务中查询
	 * @param taskid
	 * @return
	 */
	public WifiDeviceDownTask queryTask(Integer taskid){
		if(taskid != null){
			//从已完成任务中获取
			WifiDeviceDownTaskCompleted taskCompleted = wifiDeviceDownTaskCompletedService.getById(taskid);
			if(taskCompleted != null)
				return taskCompleted;
			
			WifiDeviceDownTask pending_task = wifiDeviceDownTaskService.getById(taskid);
			//如果获取的是pending任务 判断时间是否timeout
			if(pending_task != null){
				validateTaskTimeout(pending_task);
				return pending_task;
			}
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	/**
	 * 验证任务是否超时 如果超时则修改任务状态
	 * @param pending_task
	 */
	public void validateTaskTimeout(WifiDeviceDownTask pending_task){
		if(pending_task == null || !WifiDeviceDownTask.State_Pending.equals(pending_task.getState())) return;
		if((System.currentTimeMillis() - pending_task.getCreated_at().getTime()) 
				> RuntimeConfiguration.TaskTimeoutMilliSecs){
			//设置任务状态为timeout 此处不算任务完成
			pending_task.setState(WifiDeviceDownTask.State_Timeout);
			pending_task = wifiDeviceDownTaskService.update(pending_task);
		}
	}
	
	/*public boolean cancelTask(int taskid){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask != null) 
	}*/
}
