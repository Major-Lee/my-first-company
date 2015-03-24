package com.bhu.vas.business.ds.task.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

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
	public boolean taskExecuteCallback(int taskid,String state,String response){
		/*if(taskid >=0 && taskid <100000){//保留任务号，用户触发定时任务的id号
			
		}*/
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask != null) {
			if(WifiDeviceDownTask.State_Done.equals(state)){
//			if(state == WifiDeviceDownTask.State_Completed){
				WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
				/*WifiDeviceDownTaskCompleted completed = new WifiDeviceDownTaskCompleted();
				try {
					BeanUtils.copyProperties(completed, downtask);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				completed.setState(WifiDeviceDownTask.State_Done);
				completed.setCompleted_at(new Date());*/
				wifiDeviceDownTaskCompletedService.insert(completed);
			}else{
				downtask.setState(state);
				wifiDeviceDownTaskService.update(downtask);
			}
			return true;
		}else{
			return false;
		}
		
	}
	
	public static final int Task_Illegal = -1;
	public static final int Task_Already_Exist = 0;
	public static final int Task_Already_Completed = 1;
	public static final int Task_Startup_OK = 2;
	
	public int taskComming(WifiDeviceDownTask downtask){
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) return Task_Illegal;
		if(downtask.getChannel_taskid()>0 && StringUtils.isNotEmpty(downtask.getChannel()) ){//外部应用触发任务
			//看看WifiDeviceDownTaskService是否存在此任务
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("channel_taskid", downtask.getChannel_taskid()).andColumnEqualTo("channel", downtask.getChannel());
			/*mc.setOrderByClause("id");
			mc.setPageNumber(1);
			mc.setPageSize(10);*/
			int count  = wifiDeviceDownTaskService.countByModelCriteria(mc);
			if(count > 0) return Task_Already_Exist;
			count  = wifiDeviceDownTaskCompletedService.countByModelCriteria(mc);
			if(count > 0) return Task_Already_Completed;
		}
		downtask = wifiDeviceDownTaskService.insert(downtask);
		return Task_Startup_OK;
	}
	
	
	/*public boolean cancelTask(int taskid){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask != null) 
	}*/
}
