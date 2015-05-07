package com.bhu.vas.business.ds.task.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;
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
	/*public boolean cancelTask(int taskid){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask != null) 
	}*/
}
